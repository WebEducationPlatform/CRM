package com.ewp.crm.models.dto;

import java.time.ZonedDateTime;

public class ClientHistoryDto {

    private String title;
    private String link;
    private String recordLink;
    private ZonedDateTime date;

    public ClientHistoryDto() {
    }

    public ClientHistoryDto(String title, ZonedDateTime date) {
        this.title = title;
        this.date = date;
        this.link = null;
        this.recordLink = null;
    }

    public ClientHistoryDto(String title, String link, String recordLink, ZonedDateTime date) {
        this.title = title;
        this.link = link;
        this.recordLink = recordLink;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRecordLink() {
        return recordLink;
    }

    public void setRecordLink(String recordLink) {
        this.recordLink = recordLink;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
