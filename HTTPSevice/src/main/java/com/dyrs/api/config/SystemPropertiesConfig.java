package com.dyrs.api.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class SystemPropertiesConfig {
    private static Properties props;

    public SystemPropertiesConfig() {

        try {
            Resource resource = new ClassPathResource("/application.properties");//
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {

        return props == null ? null : props.getProperty(key);

    }


    public static String getProperty(String key, String defaultValue) {

        return props == null ? null : props.getProperty(key, defaultValue);

    }


    public static Properties getProperties() {
        return props;
    }
}
