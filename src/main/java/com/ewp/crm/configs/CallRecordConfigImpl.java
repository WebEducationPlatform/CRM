package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.CallRecordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "file:./call-record.properties", encoding = "windows-1251")
public class CallRecordConfigImpl implements CallRecordConfig {

    private static final Logger logger = LoggerFactory.getLogger(CallRecordConfigImpl.class);
    private String recordFolderName;
    private String recordingFormat;
    private String callToClientRecordingPrefix;
    private String commonCallRecordingPrefix;
    private String downloadLinkPrefix;

    @Autowired
    public CallRecordConfigImpl(Environment env) {
        try {
            recordFolderName = env.getRequiredProperty("call.record.folder.name");
            recordingFormat = env.getRequiredProperty("call.record.format");
            callToClientRecordingPrefix = env.getRequiredProperty("call.record.client.prefix");
            commonCallRecordingPrefix = env.getRequiredProperty("call.record.common.prefix");
            downloadLinkPrefix = env.getRequiredProperty("call.record.download.link.prefix");

            if (recordFolderName.isEmpty() || recordingFormat.isEmpty() || callToClientRecordingPrefix.isEmpty() ||
                    commonCallRecordingPrefix.isEmpty() || downloadLinkPrefix.isEmpty()) {
                throw new NoSuchFieldException();
            }
        } catch (NoSuchFieldException e) {
            logger.error("CallRecord configs haven't been initialized. Check call-record.properties file", e);
            System.exit(1);
        }
    }

    @Override
    public String getRecordFolderName() {
        return recordFolderName;
    }

    @Override
    public String getRecordingFormat() {
        return recordingFormat;
    }

    @Override
    public String getRecordingToClientNamePrefix() {
        return callToClientRecordingPrefix;
    }

    @Override
    public String getCommonRecordingNamePrefix() {
        return commonCallRecordingPrefix;
    }

    @Override
    public String getRecordDownloadLinkPrefix() {
        return downloadLinkPrefix;
    }
}
