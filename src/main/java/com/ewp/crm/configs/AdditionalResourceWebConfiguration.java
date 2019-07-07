package com.ewp.crm.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@PropertySource("file:./ckeditor.properties")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:./messages.yml")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:./firstSkypeCall.yml")
public class AdditionalResourceWebConfiguration implements WebMvcConfigurer {

    @Value("${ckediror.img.uri}")
    String uri;
    @Value("${ckeditor.img.upload.path}")
    String path;
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uri+"**").addResourceLocations("file:"+path);
    }
}