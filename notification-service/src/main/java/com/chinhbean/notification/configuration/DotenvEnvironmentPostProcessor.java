package com.chinhbean.notification.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DotenvEnvironmentPostProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Map<String, Object> envProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                envProperties.put(entry.getKey(), entry.getValue());
                logger.debug("Loaded .env property: {}={}", entry.getKey(), entry.getValue());
            });
            if (envProperties.isEmpty()) {
                logger.warn("No properties loaded from .env file. Ensure .env exists in the project root.");
            } else {
                environment.getPropertySources().addFirst(new MapPropertySource("dotenvProperties", envProperties));
                logger.info("Added .env properties to Spring environment: {}", envProperties.keySet());
            }
        } catch (Exception e) {
            logger.error("Failed to load .env file", e);
        }
    }
}