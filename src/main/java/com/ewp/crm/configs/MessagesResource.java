package com.ewp.crm.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:messages.yml")
public class MessagesResource {
    @Autowired
    public MessagesResource() {}
}
