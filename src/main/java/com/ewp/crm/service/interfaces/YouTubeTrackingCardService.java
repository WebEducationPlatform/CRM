package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.YouTubeTrackingCard;

import java.util.List;

public interface YouTubeTrackingCardService {

	List<YouTubeTrackingCard> getAllYouTubeTrackingCards();

	List<YouTubeTrackingCard> getAllByHasLiveStream(boolean hasLiveStream);

	YouTubeTrackingCard getYouTubeTrackingCardByID(Long id);

	YouTubeTrackingCard addYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

	YouTubeTrackingCard updateYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

	void deleteYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);
}
