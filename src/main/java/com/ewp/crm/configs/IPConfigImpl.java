package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.IPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:ip.properties")
public class IPConfigImpl implements IPConfig {

	private String voximplantApiKey;

	private String voximplantAccountId;

	private String voximplantRuleId;

	private static Logger logger = LoggerFactory.getLogger(IPConfigImpl.class);

	@Autowired
	public IPConfigImpl(Environment environment) {
		voximplantApiKey = environment.getProperty("voximplant.api.key");
		voximplantAccountId = environment.getProperty("voximplant.account.id");
		voximplantRuleId = environment.getProperty("voximplant.rule.id");

		if (!configIsValid()) {
			logger.error("IP configs have not initialized. Check ip.properties file");
			System.exit(-1);
		}
	}


	private boolean configIsValid() {
		if (voximplantApiKey == null || voximplantApiKey.isEmpty()) return false;
		if (voximplantAccountId == null || voximplantAccountId.isEmpty()) return false;
		if (voximplantRuleId == null || voximplantRuleId.isEmpty()) return false;
		return true;
	}

	@Override
	public String getVoximplantApiKey() {
		return voximplantApiKey;
	}

	@Override
	public String getVoximplantAccountId() {
		return voximplantAccountId;
	}

	@Override
	public String getVoximplantRuleId() {
		return voximplantRuleId;
	}
}
