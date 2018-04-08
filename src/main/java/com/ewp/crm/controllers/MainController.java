package com.ewp.crm.controllers;

import com.ewp.crm.configs.ImageConfig;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MainController {

	private final ImageConfig imageConfig;

	public MainController(ImageConfig imageConfig) {
		this.imageConfig = imageConfig;
	}

	@RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
	public String homePage() {
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
			return "login";
		} else {
			return "redirect:/client";
		}
	}

	@ResponseBody
	@RequestMapping(value = "avatar/{file}", method = RequestMethod.GET)
	public byte[] getPhoto(@PathVariable("file") String file) throws IOException {
		Path fileLocation = Paths.get(imageConfig.getPathForAvatar() + file + ".png");
		return Files.readAllBytes(fileLocation);
	}

}
