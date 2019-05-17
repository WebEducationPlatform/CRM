package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.CallRecordConfig;
import com.ewp.crm.service.interfaces.DownloadCallRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;

@Service
public class DownloadCallRecordServiceImpl implements DownloadCallRecordService {

	private static Logger logger = LoggerFactory.getLogger(DownloadCallRecordServiceImpl.class);
	private final CallRecordConfig callRecordConfig;
	private final String recordFolderName;
	private final String recordingFormat;
	private final String callToClientRecordingPrefix;
	private final String commonCallRecordingPrefix;
	private final String downloadLinkPrefix;
	private Environment env;

	@Autowired
	public DownloadCallRecordServiceImpl(CallRecordConfig callRecordConfig, Environment env) {
		this.callRecordConfig = callRecordConfig;
		recordFolderName = callRecordConfig.getRecordFolderName();
		recordingFormat = callRecordConfig.getRecordingFormat();
		callToClientRecordingPrefix = callRecordConfig.getRecordingToClientNamePrefix();
		commonCallRecordingPrefix = callRecordConfig.getCommonRecordingNamePrefix();
		downloadLinkPrefix = callRecordConfig.getRecordDownloadLinkPrefix();
		this.env = env;
	}

	@Override
	public Optional<String> getRecordLink(String downloadUrl, Long clientCallId, Long historyId) {
		String fileName = callToClientRecordingPrefix + clientCallId + historyId + recordingFormat;
		File file = new File(createDirectory(), fileName);

		return downloadRecording(file, downloadUrl, fileName);
	}

	@Override
	public Optional<String> getRecordLink(String downloadUrl, Long commonCallId) {
		String fileName = commonCallRecordingPrefix + commonCallId + recordingFormat;
		File file = new File(createDirectory(), fileName);

		return downloadRecording(file, downloadUrl, fileName);
	}

	private Optional<String> downloadRecording(File file, String downloadUrl, String fileName) {
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					logger.error("Audio record file not created!");
					throw new FileNotFoundException(env.getProperty("messaging.call-records.exception.download-file-not-found"));
				}
			}
			URL url = new URL(downloadUrl);
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			rbc.close();
		} catch (IOException e) {
			logger.error("Could not download the call record file!", e);
		}
		return Optional.of(downloadLinkPrefix + fileName);
	}

	private File createDirectory() {
		File dir = new File(recordFolderName);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				logger.error("Could not create folder for call records");
			}
		}
		return dir;
	}
}
