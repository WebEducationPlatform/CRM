package com.ewp.crm.configs;

import com.ewp.crm.configs.inteface.IPConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
@PropertySource("file:./ip.properties")
public class IPConfigImpl implements IPConfig {

	private String voximplantApiKey;

	private String voximplantAccountId;

	private String voximplantRuleId;

	private String voximplantLoginForWebCall;

	private String voximplantPasswordForWebCall;

	private String voximplantCodeToSetRecord;

	private static Logger logger = LoggerFactory.getLogger(IPConfigImpl.class);

	@Autowired
	public IPConfigImpl(Environment environment) {
		try {
			voximplantApiKey = environment.getRequiredProperty("voximplant.api.key");
			voximplantAccountId = environment.getRequiredProperty("voximplant.account.id");
			voximplantRuleId = environment.getRequiredProperty("voximplant.rule.id");
			voximplantLoginForWebCall = environment.getRequiredProperty("voximplant.webcall.login");
			voximplantPasswordForWebCall = environment.getRequiredProperty("voximplant.webcall.password");
			voximplantCodeToSetRecord = environment.getRequiredProperty("voximplant.salt");
			if (voximplantApiKey.isEmpty() || voximplantAccountId.isEmpty() || voximplantRuleId.isEmpty() || voximplantLoginForWebCall.isEmpty() || voximplantPasswordForWebCall.isEmpty()) {
				throw new NullPointerException();
			}
		} catch (IllegalStateException | NullPointerException e) {
			logger.error("IP configs have not initialized. Check ip.properties file");
			System.exit(-1);
		}
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

	public String getVoximplantLoginForWebCall() {
		return voximplantLoginForWebCall;
	}

	public String getVoximplantPasswordForWebCall() {
		return voximplantPasswordForWebCall;
	}

	public String getVoximplantCodeToSetRecord() {
		return voximplantCodeToSetRecord;
	}
}
