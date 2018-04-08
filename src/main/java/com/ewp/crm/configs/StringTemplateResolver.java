package com.ewp.crm.configs;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class StringTemplateResolver extends TemplateResolver {

	private String PREFIX = "";

	public StringTemplateResolver() {
		Set<String> patterns = new HashSet<>();
		patterns.add(PREFIX + "*");
		setResolvablePatterns(patterns);
		setResourceResolver(new StringResourceResolver());
	}

	public String getPREFIX() {
		return PREFIX;
	}

	public void setPREFIX(String PREFIX) {
		this.PREFIX = PREFIX;
	}

	@Override
	protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
		String templateName = templateProcessingParameters.getTemplateName();
		return templateName.substring(PREFIX.length());
	}

	private class StringResourceResolver implements IResourceResolver {

		@Override
		public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
			return new ByteArrayInputStream(resourceName.getBytes());
		}

		@Override
		public String getName() {
			return "stringResourceResolver";
		}
	}

}
