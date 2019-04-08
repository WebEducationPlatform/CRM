package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface DownloadCallRecordService {

	Optional<String> getRecordLink(String url, Long clientId, Long historyId);

	Optional<String> getRecordLink(String downloadUrl, Long commonCallId);

}
