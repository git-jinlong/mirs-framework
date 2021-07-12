package com.github.mirs.banxiaoxiao.framework.core.dcc.conf;

/**
 * 
 * @author zcy 2019年3月12日
 * @param <v>
 */
public class SimpleDConfig<v> extends BaseDConfig<v> {

    public SimpleDConfig(String namespace) {
        super(namespace);
    }
    
    public SimpleDConfig(String namespace, Class<v> clazz) {
        super(namespace);
        setClazz(clazz);
    }
    
}
