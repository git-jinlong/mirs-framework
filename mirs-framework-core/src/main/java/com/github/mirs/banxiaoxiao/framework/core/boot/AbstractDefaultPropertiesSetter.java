package com.github.mirs.banxiaoxiao.framework.core.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.Map;

/**
 * Allows for add some default configuration to {@code defaultProperties} prior to the application
 * context being refreshed.
 *
 * @author zw
 */
public abstract class AbstractDefaultPropertiesSetter implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "defaultProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        Map<String, Object> map = getDefaultMap();
        MapPropertySource target = null;
        MutablePropertySources mutablePropertySources = environment.getPropertySources();
        if (mutablePropertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = mutablePropertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!mutablePropertySources.contains(PROPERTY_SOURCE_NAME)) {
            mutablePropertySources.addLast(target);
        }
    }

    protected abstract Map<String, Object> getDefaultMap();
}
