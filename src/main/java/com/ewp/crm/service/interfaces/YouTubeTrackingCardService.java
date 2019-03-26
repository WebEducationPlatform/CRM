package com.ewp.crm.service.interfaces;


import com.ewp.crm.models.YouTubeTrackingCard;

import java.util.List;
import java.util.Optional;

public interface YouTubeTrackingCardService {

	List<YouTubeTrackingCard> getAllYouTubeTrackingCards();

	List<YouTubeTrackingCard> getAllByHasLiveStream(boolean hasLiveStream);

	Optional<YouTubeTrackingCard> getYouTubeTrackingCardByID(Long id);

	Optional<YouTubeTrackingCard> addYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

	Optional<YouTubeTrackingCard> updateYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);

	void deleteYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard);
}
