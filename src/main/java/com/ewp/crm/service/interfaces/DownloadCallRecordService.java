package com.ewp.crm.service.interfaces;

import java.util.Optional;

public interface DownloadCallRecordService {

	Optional<String> downloadRecord(String url, Long clientId, Long historyId);
}
