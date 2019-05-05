package com.ewp.crm.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class MessagesSource {

    private String deliveryError;
    private String invalidMobilePhone;
    private String incorrectId;
    private String unknownError;


    private PropertySource propertySource;

    @Autowired
    private ConfigurableListableBeanFactory beanFactory;

    @Bean
    public PropertySource properties() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("messages.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        // properties need to be processed by beanfactory to be accessible after
        propertySourcesPlaceholderConfigurer.postProcessBeanFactory(beanFactory);
        return propertySourcesPlaceholderConfigurer.getAppliedPropertySources().get(PropertySourcesPlaceholderConfigurer.LOCAL_PROPERTIES_PROPERTY_SOURCE_NAME);
    }

    @Autowired
    public MessagesSource(Environment env) {
    }

    public String getDeliveryError() {
        deliveryError = String.valueOf(properties().getProperty("messaging.phone.calls.delivery-error"));
        return deliveryError;
    }

    public String getInvalidMobilePhone() {
        invalidMobilePhone = String.valueOf(properties().getProperty("messaging.phone.calls.invalid-mobile-phone"));
        return invalidMobilePhone;
    }

    public String getIncorrectId() {
        incorrectId = String.valueOf(properties().getProperty("messaging.phone.calls.incorrect-id"));
        return incorrectId;
    }

    public String getUnknownError() {
        unknownError = String.valueOf(properties().getProperty("messaging.phone.calls.unknown-error"));
        return unknownError;
    }
}
