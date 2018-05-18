package com.ewp.crm.component.util;

import com.ewp.crm.component.util.interfaces.IPUtil;
import com.ewp.crm.configs.inteface.IPConfig;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class IPUtilImpl implements IPUtil {

	private String voximplantApiKey;

	private String voximplantAccountId;

	private String voximplantRuleId;


	private static org.slf4j.Logger logger = LoggerFactory.getLogger(IPUtilImpl.class);


	@Autowired
	public IPUtilImpl(IPConfig ipConfig) {
		voximplantApiKey = ipConfig.getVoximplantApiKey();
		voximplantAccountId = ipConfig.getVoximplantAccountId();
		voximplantRuleId = ipConfig.getVoximplantRuleId();
	}

	public void call(String from, String to, Long callId) {

		String callVoximplant = "https://api.voximplant.com/platform_api/StartScenarios/" +
				"?account_id=" +
				voximplantAccountId +
				"&api_key=" +
				voximplantApiKey +
				"&rule_id=" +
				voximplantRuleId +
				"&script_custom_data=" +
				from +
				"%3A" +
				to +
				"%3A" +
				callId;

		try {
			HttpGet callVox = new HttpGet(callVoximplant);
			HttpClient httpClient = HttpClients.custom()
					.setDefaultRequestConfig(RequestConfig.custom()
							.setCookieSpec(CookieSpecs.STANDARD).build())
					.build();
			httpClient.execute(callVox);
		} catch (IOException e) {
			logger.error("Failed to connect to Voximplant server");
		}


	}
}


