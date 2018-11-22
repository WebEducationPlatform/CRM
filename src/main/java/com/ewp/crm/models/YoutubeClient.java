package com.ewp.crm.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "youtube_client")
public class YoutubeClient {

    @Id
    @GeneratedValue
    @Column(name = "youtube_client_id") // аккаунт клиента на youtube ???
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

	@NotNull
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinTable(name = "youtube_client_youtube_tracking_card",
			joinColumns = {@JoinColumn(name = "youtube_client_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE_CLIENT"))},
			inverseJoinColumns = {@JoinColumn(name = "youtube_tracking_card_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE__TRACKING_CARD"))})
    private YouTubeTrackingCard youTubeTrackingCard;

	@Column(name = "checked") // проверен, подтвержден
	private boolean checked;

	@Column(name = "upload_date") // дата загрузки?? чего??
	private LocalDateTime uploadDate;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "youtube_client_youtube_client_message",
            joinColumns = {@JoinColumn(name = "youtube_client_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE_CLIENT_YOUTUBE_CLIENT_MESSAGES"))},
            inverseJoinColumns = {@JoinColumn(name = "youtube_client_messages_id", foreignKey = @ForeignKey(name = "FK_YOUTUBE_CLIENT_MESSAGES"))})
    private List<YoutubeClientMessage> messages;

    public YoutubeClient() {
    	this.checked = false;
		this.uploadDate = LocalDateTime.now();
    }

    public YoutubeClient(String fullName, List<YoutubeClientMessage> messages) {
		this();
        this.fullName = fullName;
        this.messages = messages;
    }

    public YoutubeClient(String fullName) {
		this();
        this.fullName = fullName;
    }

	public YoutubeClient(String fullName, @NotNull YouTubeTrackingCard youTubeTrackingCard, List<YoutubeClientMessage> messages) {
		this();
    	this.fullName = fullName;
		this.youTubeTrackingCard = youTubeTrackingCard;
		this.messages = messages;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

	public YouTubeTrackingCard getYouTubeTrackingCard() {
		return youTubeTrackingCard;
	}

	public void setYouTubeTrackingCard(YouTubeTrackingCard youTubeTrackingCard) {
		this.youTubeTrackingCard = youTubeTrackingCard;
	}

	public LocalDateTime getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(LocalDateTime uploadDate) {
		this.uploadDate = uploadDate;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public List<YoutubeClientMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<YoutubeClientMessage> messages) {
        this.messages = messages;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof YoutubeClient)) return false;

		YoutubeClient that = (YoutubeClient) o;

		if (checked != that.checked) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
		if (youTubeTrackingCard != null ? !youTubeTrackingCard.equals(that.youTubeTrackingCard) : that.youTubeTrackingCard != null)
			return false;
		if (uploadDate != null ? !uploadDate.equals(that.uploadDate) : that.uploadDate != null) return false;
		return messages != null ? messages.equals(that.messages) : that.messages == null;
	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
		result = 31 * result + (youTubeTrackingCard != null ? youTubeTrackingCard.hashCode() : 0);
		result = 31 * result + (checked ? 1 : 0);
		result = 31 * result + (uploadDate != null ? uploadDate.hashCode() : 0);
		result = 31 * result + (messages != null ? messages.hashCode() : 0);
		return result;
	}

	@Override
    public String toString() {
        return "YoutubeClient{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
