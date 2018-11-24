package com.ewp.crm.models;




import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * ????????
 */
@Entity
@Table(name = "youtube_tracking_card")
public class YouTubeTrackingCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "youtube_tracking_card_id")
	private Long id;

	@NotNull
	@Column(name = "youtube_channel", nullable = false)
	private String youTubeChannelID;

	/**
     * Группа youtube-канала в vk
     */
	@NotNull
	@Column(name = "vk_group", nullable = false)
	private String vkGroupID;

	/**
     * Идет ли стрим в прямом эфире???
     */
	@Column(name = "has_live_stream")
	private boolean hasLiveStream;

	@Column(name = "channel_name")
	private String channelName;

	@Column(name = "description")
	private String description;

	public YouTubeTrackingCard() {
		this.hasLiveStream = false;
	}

	public YouTubeTrackingCard(@NotNull String youTubeChannelID, @NotNull String vkGroupID) {
		this.youTubeChannelID = youTubeChannelID;
		this.vkGroupID = vkGroupID;
		this.hasLiveStream = false;
	}

	public YouTubeTrackingCard(@NotNull String youTubeChannelID, @NotNull String vkGroupID, String channelName, String description) {
		this.youTubeChannelID = youTubeChannelID;
		this.vkGroupID = vkGroupID;
		this.channelName = channelName;
		this.description = description;
		this.hasLiveStream = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getYouTubeChannelID() {
		return youTubeChannelID;
	}

	public void setYouTubeChannelID(String youTubeChannelID) {
		this.youTubeChannelID = youTubeChannelID;
	}

	public String getVkGroupID() {
		return vkGroupID;
	}

	public void setVkGroupID(String vkGroupID) {
		this.vkGroupID = vkGroupID;
	}

	public boolean isHasLiveStream() {
		return hasLiveStream;
	}

	public void setHasLiveStream(boolean hasLiveStream) {
		this.hasLiveStream = hasLiveStream;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YouTubeTrackingCard)) return false;

		YouTubeTrackingCard that = (YouTubeTrackingCard) o;

		if (hasLiveStream != that.hasLiveStream) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (youTubeChannelID != null ? !youTubeChannelID.equals(that.youTubeChannelID) : that.youTubeChannelID != null)
			return false;
		if (vkGroupID != null ? !vkGroupID.equals(that.vkGroupID) : that.vkGroupID != null) return false;
		if (channelName != null ? !channelName.equals(that.channelName) : that.channelName != null) return false;
		return description != null ? description.equals(that.description) : that.description == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (youTubeChannelID != null ? youTubeChannelID.hashCode() : 0);
		result = 31 * result + (vkGroupID != null ? vkGroupID.hashCode() : 0);
		result = 31 * result + (hasLiveStream ? 1 : 0);
		result = 31 * result + (channelName != null ? channelName.hashCode() : 0);
		result = 31 * result + (description != null ? description.hashCode() : 0);
		return result;
	}
}
