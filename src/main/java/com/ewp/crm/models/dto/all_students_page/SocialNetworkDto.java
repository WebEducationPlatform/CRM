package com.ewp.crm.models.dto.all_students_page;

import com.ewp.crm.models.SocialProfile;

import java.util.List;
import java.util.stream.Collectors;

public class SocialNetworkDto {
    private long id;
    private String socialId;
    private SocialProfile.SocialNetworkType socialNetworkType;

    public SocialNetworkDto() {
    }

    public SocialNetworkDto(long id, String socialId, SocialProfile.SocialNetworkType socialNetworkType) {
        this.id = id;
        this.socialId = socialId;
        this.socialNetworkType = socialNetworkType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public SocialProfile.SocialNetworkType getSocialNetworkType() {
        return socialNetworkType;
    }

    public void setSocialNetworkType(SocialProfile.SocialNetworkType socialNetworkType) {
        this.socialNetworkType = socialNetworkType;
    }

    public static SocialNetworkDto getSocialNetworkDto(SocialProfile socialProfile) {
        SocialNetworkDto socialNetworkDto = new SocialNetworkDto();

        socialNetworkDto.id = socialProfile.getId();
        socialNetworkDto.socialId = socialProfile.getSocialId();
        socialNetworkDto.socialNetworkType = socialProfile.getSocialNetworkType();

        return socialNetworkDto;
    }

    public static List<SocialNetworkDto> getSocialNetworkDtos(List<SocialProfile> socialProfiles) {
        return socialProfiles
                .stream()
                .map(SocialNetworkDto::getSocialNetworkDto)
                .collect(Collectors.toList());

    }
}
