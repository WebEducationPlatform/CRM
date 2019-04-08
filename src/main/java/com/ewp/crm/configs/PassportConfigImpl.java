package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.PassportConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./passport.properties")
public class PassportConfigImpl implements PassportConfig {

    private final byte[] secretKey;

    @Autowired
    public PassportConfigImpl(Environment env) {
        secretKey = env.getProperty("passport.secret.key", byte[].class);
    }

    @Override
    public byte[] getSecretKey() {
        return secretKey;
    }
}
