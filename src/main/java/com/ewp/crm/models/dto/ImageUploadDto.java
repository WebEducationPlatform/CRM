package com.ewp.crm.models.dto;

import java.net.URI;

public class ImageUploadDto {
    private byte uploaded=1;
    private String fileName;
    private URI url;

    public ImageUploadDto() {
    }

    public ImageUploadDto(String fileName, URI url) {
        this.fileName = fileName;
        this.url = url;
    }

    public byte getUploaded() {
        return uploaded;
    }

    public void setUploaded(byte uploaded) {
        this.uploaded = uploaded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }
}
