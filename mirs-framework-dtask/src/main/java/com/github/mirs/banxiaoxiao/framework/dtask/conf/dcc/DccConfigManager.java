package com.github.mirs.banxiaoxiao.framework.dtask.conf.dcc;

import com.github.mirs.banxiaoxiao.framework.core.dcc.conf.SimpleDConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.Constants;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfig;
import com.github.mirs.banxiaoxiao.framework.dtask.conf.TaskConfigProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zcy 2019年4月25日
 */
public class DccConfigManager<T extends TaskConfig> extends SimpleDConfig<T> implements TaskConfigProvider<T> {

    public DccConfigManager() {
        super("/bee");
    }

    public DccConfigManager(String configCode, Class<?> configModelClass) {
        super(String.format(Constants.KEY_FORMAT_CONFIG, configCode));
        setClazz(configModelClass);
    }

    @Override
    public void setTaskCode(String taskCode) {
        setAppKey(String.format(Constants.KEY_FORMAT_CONFIG, taskCode));
    }

    @Override
    public boolean isExist(String taskId) {
        return super.exist(taskId);
    }

    @Override
    public T getTaskConfig(String taskId) {
        return super.find(taskId);
    }

    @Override
    public List<T> getTaskConfigs(int pageNum) {
        if (pageNum == 0) {
            List<String> all = super.all();
            if (all != null && all.size() > 0) {
                List<T> configs = new ArrayList<T>();
                for (String configKey : all) {
                    T config = super.find(configKey);
                    if (config != null) {
                        configs.add(config);
                    }
                }
                return configs;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
