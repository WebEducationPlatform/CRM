package com.ewp.crm.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@PropertySources(value = {
		@PropertySource("classpath:application.properties"),
		@PropertySource("file:./image.properties")
})
public class ImageConfig {

	private static Logger logger = LoggerFactory.getLogger(ImageConfig.class);
	private String pathForAvatar;
	private long maxImageSize;
	private String pathForImages;

	@Autowired
	public ImageConfig(Environment environment) {
		maxImageSize = parseSize(environment.getProperty("spring.http.multipart.max-file-size"));
		pathForAvatar = environment.getProperty("pathForAvatar");
		pathForImages = environment.getProperty("pathForImages");
		checkConfig();
	}

	private void checkConfig() {
		if (pathForAvatar == null || pathForAvatar.isEmpty()) {
			logger.error("Path for avatars not specified");
			System.exit(-1);
		}
		if (pathForImages == null || pathForImages.isEmpty()) {
			logger.error("Path for images not specified");
			System.exit(-1);
		}
		File avatarPath = new File(pathForAvatar);
		if (!avatarPath.exists()) {
			if (!avatarPath.mkdirs()) {
				logger.error("Could not create folder for user photos");
				System.exit(-1);
			}
		}
		File imagePath = new File(pathForImages);
		if (!imagePath.exists()) {
			if (!imagePath.mkdirs()) {
				logger.error("Could not create folder for images");
				System.exit(-1);
			}
		}
		pathForAvatar = pathForAvatar + "\\";
		pathForImages = pathForImages + "\\";
		if (maxImageSize == 0) {
			maxImageSize = 1024;
			logger.info("The size of the uploaded file is not specified. The value in 1MB is set");
		}
	}

	public String getPathForAvatar() {
		return pathForAvatar;
	}

	public String getPathForImages() {
		return pathForImages;
	}

	public long getMaxImageSize() {
		return maxImageSize;
	}

	private long parseSize(String size) {
		if (size == null) {
			return 0;
		}
		size = size.toUpperCase();
		if (size.endsWith("KB")) {
			return Long.valueOf(size.substring(0, size.length() - 2)) * 1024L;
		} else {
			return size.endsWith("MB") ? Long.valueOf(size.substring(0, size.length() - 2)) * 1024L * 1024L : Long.valueOf(size);
		}
	}
}
