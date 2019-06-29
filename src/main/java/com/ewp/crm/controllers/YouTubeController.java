package com.ewp.crm.controllers;

import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.FileService;
import com.ewp.crm.service.interfaces.PotentialClientService;
import com.ewp.crm.service.interfaces.YouTubeTrackingCardService;
import com.ewp.crm.service.interfaces.YoutubeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'OWNER', 'HR')")
@RequestMapping("/admin/YouTubeLive")
public class YouTubeController {

	private static Logger logger = LoggerFactory.getLogger(YouTubeController.class);

	private final YouTubeTrackingCardService youTubeTrackingCardService;
	private final YoutubeService youtubeService;
	private final VKService vkService;
	private final PotentialClientService potentialClientService;
	private final FileService fileService;

	@Autowired
	public YouTubeController(YouTubeTrackingCardService youTubeTrackingCardService, YoutubeService youtubeService, VKService vkService, PotentialClientService potentialClientService, FileService fileService) {
		this.youTubeTrackingCardService = youTubeTrackingCardService;
		this.youtubeService = youtubeService;
		this.vkService = vkService;
		this.potentialClientService = potentialClientService;
		this.fileService = fileService;
	}

	@GetMapping
	public ModelAndView youTubeLivePage() {
		ModelAndView modelAndView = new ModelAndView("youTube-live");
		modelAndView.addObject("youtubeCards", youTubeTrackingCardService.getAllYouTubeTrackingCards());
		return modelAndView;
	}

	@PostMapping(value = "/addYouTubeTrackingCard")
	public String addYouTubeTrackingCard(@ModelAttribute("youTubeTrackingCard") YouTubeTrackingCard youTubeTrackingCard) {
		youTubeTrackingCardService.addYouTubeTrackingCard(youTubeTrackingCard);
		return "redirect:/admin/YouTubeLive";
	}

	@PostMapping(value = "/editYouTubeTrackingCard")
	public String editYouTubeTrackingCard(@ModelAttribute("youTubeTrackingCard") YouTubeTrackingCard youTubeTrackingCard) {
		youTubeTrackingCardService.updateYouTubeTrackingCard(youTubeTrackingCard);
		return "redirect:/admin/YouTubeLive";
	}

	@PostMapping(value = "/deleteYouTubeTrackingCard")
	public String deleteYouTubeTrackingCard(long id) {
		youTubeTrackingCardService.getYouTubeTrackingCardByID(id).ifPresent(youTubeTrackingCardService::deleteYouTubeTrackingCard);
		return "redirect:/admin/YouTubeLive";
	}

	@GetMapping(value = "/getVkIDs")
	public void getVkIDs(HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename=vkIDs.txt");
		try (ServletOutputStream out = response.getOutputStream()) {
			Optional<String> result = fileService.getAllVkIDs();
			if (result.isPresent()) {
				out.println(result.get());
				out.flush();
			}
		} catch (IOException e) {
			logger.error("IOException: ", e);
		}
	}
}
