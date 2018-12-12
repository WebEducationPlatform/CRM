package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.EwpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:./ewp.properties")
public class EwpConfigImpl implements EwpConfig {
    private boolean useEwpApi;
    private String linkForStudentProgressInfo;

    @Autowired
    public EwpConfigImpl(Environment env) {
        this.linkForStudentProgressInfo = env.getRequiredProperty("ewp.apiUrl.studentprogressinfo");
        this.useEwpApi = "yes".equals(env.getRequiredProperty("ewp.useewpapi"));
    }

    @Override
    public String getLinkForStatusStudent() {
        return linkForStudentProgressInfo;
    }

    @Override
    public boolean isUseEwpApi() {
        return useEwpApi;
    }
}
