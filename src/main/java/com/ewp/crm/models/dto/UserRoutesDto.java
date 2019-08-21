package com.ewp.crm.models.dto;

import com.ewp.crm.models.UserRoutes;

public class UserRoutesDto {
    private Long id;
    private Long user_id;
    private String first_name;
    private String last_name;
    private Integer weight;
    private UserRoutes.UserRouteType userRouteType;

    public UserRoutesDto() {
    }

    public UserRoutesDto( Long user_id, Integer weight) {
        this.user_id = user_id;
        this.weight = weight;
    }

    public UserRoutesDto(Long user_id, Integer weight, UserRoutes.UserRouteType userRouteType) {
        this.user_id = user_id;
        this.weight = weight;
        this.userRouteType = userRouteType;
    }

    public UserRoutesDto(Long user_id,String first_name, String last_name, Integer weight, String userRouteType) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.weight = weight;
        this.userRouteType = getUserRouteType(userRouteType);
    }

    public UserRoutesDto(Long user_id, Integer weight, String userRouteType) {
        UserRoutes.UserRouteType type = null;
        type = getUserRouteType(userRouteType);
        this.user_id = user_id;
        this.weight = weight;
        this.userRouteType = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public UserRoutes.UserRouteType getUserRouteType() {
        return userRouteType;
    }

    public void setUserRouteType(UserRoutes.UserRouteType userRouteType) {
        this.userRouteType = userRouteType;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    private UserRoutes.UserRouteType getUserRouteType(String userRouteType) {
        UserRoutes.UserRouteType type = null;
        switch (userRouteType){
            case "FROM_JM_EMAIL":
                type = UserRoutes.UserRouteType.FROM_JM_EMAIL;
                break;
            case "FROM_VK":
                type = UserRoutes.UserRouteType.FROM_VK;
                break;
            case "FROM_WHATSAPP":
                type = UserRoutes.UserRouteType.FROM_WHATSAPP;
                break;
            case "FROM_TELEGRAM":
                type = UserRoutes.UserRouteType.FROM_TELEGRAM;
                break;

        }
        return type;
    }

    public static UserRoutes getUserRoutesFromDto(UserRoutesDto userRoutesDto) {
        return new UserRoutes(userRoutesDto.getWeight(), userRoutesDto.getUserRouteType());
    }
}
