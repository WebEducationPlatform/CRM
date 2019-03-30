package com.ewp.crm.service.impl;

import com.ewp.crm.service.interfaces.DownloadCallRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final String RECORD_FOLDER = "CallRecords";
	private static final String RECORDING_FORMAT = ".mp3";
	private static final String CALL_TO_CLIENT_RECORDING_PREFIX = "callRecord";
	private static final String COMMON_CALL_RECORDING_PREFIX = "commonCallRecord";
	private static final String DOWNLOAD_LINK_PREFIX = "/user/rest/call/record/";

	@Override
	public Optional<String> getRecordLink(String downloadUrl, Long clientCallId, Long historyId) {
		String fileName = CALL_TO_CLIENT_RECORDING_PREFIX + clientCallId + historyId + RECORDING_FORMAT;
		File file = new File(createDirectory(), fileName);

		return downloadRecording(file, downloadUrl, fileName);
	}

	@Override
	public Optional<String> getRecordLink(String downloadUrl, Long commonCallId) {
		String fileName = COMMON_CALL_RECORDING_PREFIX + commonCallId + RECORDING_FORMAT;
		File file = new File(createDirectory(), fileName);

		return downloadRecording(file, downloadUrl, fileName);
	}

	private Optional<String> downloadRecording(File file, String downloadUrl, String fileName) {
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					logger.error("Audio record file not created!");
					throw new FileNotFoundException("File doesn't exist");
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
		return Optional.of(DOWNLOAD_LINK_PREFIX + fileName);
	}

	private File createDirectory() {
		File dir = new File(RECORD_FOLDER);
		if (!dir.exists() ) {
			if (!dir.mkdirs()) {
				logger.error("Could not create folder for call records");
			}
		}
		return dir;
	}
}
