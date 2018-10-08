package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.YouTubeTrackingCard;

import java.util.List;

public interface YouTubeTrackingCardRepository extends CommonGenericRepository<YouTubeTrackingCard>{

	List<YouTubeTrackingCard> getAllByHasLiveStream(boolean hasLiveStream);
}
