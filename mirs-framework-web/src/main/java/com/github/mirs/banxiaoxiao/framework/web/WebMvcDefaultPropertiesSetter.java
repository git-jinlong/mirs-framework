package com.github.mirs.banxiaoxiao.framework.web;


import com.github.mirs.banxiaoxiao.framework.core.boot.AbstractDefaultPropertiesSetter;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds default configuration for web mvc.
 *
 * @author zw
 */
public class WebMvcDefaultPropertiesSetter extends AbstractDefaultPropertiesSetter {

    @Override
    protected Map<String, Object> getDefaultMap() {
        Map<String, Object> props = new HashMap<>();
        props.put("spring.messages.basename", "messages/message,messages/default");
        props.put("spring.jackson.date-format", "yyyy-MM-dd HH:mm:ss");
        props.put("spring.jackson.time-zone", "Asia/Shanghai");
        return props;
    }

}
