package com.github.mirs.banxiaoxiao.framework.core.dcc.autoval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mirs.banxiaoxiao.framework.core.dcc.DccClient;
import com.github.mirs.banxiaoxiao.framework.core.dcc.autoval.spring.SpringValueAnnotationSpace;

/**
 * <pre>
 * 基于zk的配置中心，应用启动时自动注入需要的参数值，并且在数据变化时自动推送给应用
 * 考虑到产品是项目型，配置纬度支持namespace即可完全满足需求
 * </pre>
 * 
 * @author zcy 2018年9月14日
 */
public class Vals {

    public static String ROOT_PATH = "/bee/vals";

    private static Map<String, ValSpace> valSpaces = new HashMap<String, ValSpace>();

    protected static void init(DccClient dccClient) throws IOException {
        dccClient.writeData(ROOT_PATH, "");
        // 读取本地配置 bee.properties、appname.properties、configs/appname.properties
        LocalAppProperties localProperties =loadLocalAppProperties();
        DccValListener listener = new DccValListener(localProperties);
        
        // 读取本地jvm sprig bean配置项及其默认值 及 dcc 中appname空间的配置； dcc appname空间配置会覆盖spring bean默认配置
        ValSpace springAppValSpace = loadSpringValueAnnotationSpace(dccClient, listener);
        // 读取dcc 中bee空间配置
        ValSpace dccBeeValSpace = loadBeePropertiesSpace(dccClient, listener);
        // 读取dcc中appname空间的配置
        ValSpace dccAppnameSpace = loadAppDefaultSpace(dccClient, null);
        
        // 本地配置聚合 先
        LocalValSpace togetherValSpace = new LocalValSpace();
        togetherValSpace.putAll(springAppValSpace);
        togetherValSpace.putAll(localProperties);
        togetherValSpace.putAll(dccBeeValSpace);
        togetherValSpace.putAll(dccAppnameSpace);
        
        valSpaces.put(SpringValueAnnotationSpace.namespace(), springAppValSpace);
        valSpaces.put(dccBeeValSpace.getNamespace(), dccBeeValSpace);
        valSpaces.put(dccAppnameSpace.getNamespace(), dccAppnameSpace);
        valSpaces.put(togetherValSpace.getNamespace(), togetherValSpace);
        
        // 覆写本地配置
        localProperties.putAll(togetherValSpace);
        localProperties.save();
    }
    
    private static LocalAppProperties loadLocalAppProperties() {
        LocalAppProperties localProperties = new LocalAppProperties("configs/" + AppDefaultSpace.namespace() + ".properties");
        localProperties.load();
        return localProperties;
    }
    
    private static ValSpace loadBeePropertiesSpace(DccClient dccClient, Valistener listener) {
        BeePropertiesSpace beeSpace = new BeePropertiesSpace(dccClient);
        beeSpace.load();
        beeSpace.registerListener(listener);
        return beeSpace;
    }
    
    private static ValSpace loadSpringValueAnnotationSpace(DccClient dccClient, Valistener listener) {
        SpringValueAnnotationSpace valSpace = new SpringValueAnnotationSpace(dccClient);
        valSpace.load();
        valSpace.registerListener(listener);
        return valSpace;
    }
    
    private static ValSpace loadAppDefaultSpace(DccClient dccClient, Valistener listener) {
        AppDefaultSpace valSpace = new AppDefaultSpace(dccClient);
        valSpace.load();
        valSpace.registerListener(listener);
        return valSpace;
    }

    public static ValSpace getValSpace(String namespace) {
        return valSpaces.get(namespace);
    }

    public static List<ValSpace> getValSpaces() {
        List<ValSpace> valspaces = new ArrayList<ValSpace>(valSpaces.values());
        Collections.sort(valspaces, new Comparator<ValSpace>() {

            public int compare(ValSpace arg0, ValSpace arg1) {
                return arg1.getPriority() - arg0.getPriority();
            }
        });
        return valspaces;
    }

    private static class DccValListener implements Valistener {

        LocalAppProperties localProperties;

        public DccValListener(LocalAppProperties localProperties) {
            super();
            this.localProperties = localProperties;
        }

        @Override
        public void onChange(String namespace, String key, Object value) {
            localProperties.put(key, value);
            try {
                localProperties.save();
            } catch (IOException e) {
                //
            }
        }
    }
}
