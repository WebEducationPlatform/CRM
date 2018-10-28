package com.ewp.crm.service.impl;

import com.ewp.crm.models.YouTubeTrackingCard;
import com.ewp.crm.repository.interfaces.YouTubeTrackingCardRepository;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.YouTubeTrackingCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class YouTubeTrackingCardServiceImpl implements YouTubeTrackingCardService {

	private final YouTubeTrackingCardRepository youTubeTrackingCardRepository;
	private final VKService vkService;

	@Autowired
	public YouTubeTrackingCardServiceImpl(YouTubeTrackingCardRepository youTubeTrackingCardRepository, VKService vkService) {
		this.youTubeTrackingCardRepository = youTubeTrackingCardRepository;
		this.vkService = vkService;
	}

	@Override
	public List<YouTubeTrackingCard> getAllYouTubeTrackingCards() {
		return youTubeTrackingCardRepository.findAll();
	}

	@Override
	public List<YouTubeTrackingCard> getAllByHasLiveStream(boolean hasLiveStream) {
		return youTubeTrackingCardRepository.getAllByHasLiveStream(hasLiveStream);
	}

	@Override
	public YouTubeTrackingCard getYouTubeTrackingCardByID(Long id) {
		Optional<YouTubeTrackingCard> optional = youTubeTrackingCardRepository.findById(id);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public YouTubeTrackingCard addYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard) {
		turnLinksIntoID(youTubeTrackingCard);
		return youTubeTrackingCardRepository.saveAndFlush(youTubeTrackingCard);
	}

	@Override
	public YouTubeTrackingCard updateYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard) {
		turnLinksIntoID(youTubeTrackingCard);
		return youTubeTrackingCardRepository.saveAndFlush(youTubeTrackingCard);
	}

	@Override
	public void deleteYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard) {
		youTubeTrackingCardRepository.delete(youTubeTrackingCard);
	}

	private void turnLinksIntoID(YouTubeTrackingCard youTubeTrackingCard) {
		String youTubeChannelID = youTubeTrackingCard.getYouTubeChannelID();
		int indexOfLastSlash = youTubeChannelID.lastIndexOf("/");
		if (indexOfLastSlash != -1) {
			youTubeChannelID = youTubeChannelID.substring(indexOfLastSlash + 1);
		}
		youTubeTrackingCard.setYouTubeChannelID(youTubeChannelID);

		String vkGroupID = youTubeTrackingCard.getVkGroupID();
		indexOfLastSlash = vkGroupID.lastIndexOf("/");
		if (indexOfLastSlash != -1) {
			vkGroupID = vkGroupID.substring(indexOfLastSlash + 1);
		}
		if (vkGroupID.startsWith("id")) {
			vkGroupID = vkGroupID.replaceFirst("id", "");
		} else if (vkGroupID.startsWith("public")) {
			vkGroupID = vkGroupID.replaceFirst("public", "");
		} else {
			vkGroupID = vkService.getLongIDFromShortName(vkGroupID);
		}
		youTubeTrackingCard.setVkGroupID(vkGroupID);
	}
}
