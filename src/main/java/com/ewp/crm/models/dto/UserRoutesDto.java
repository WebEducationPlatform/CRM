package com.ewp.crm.models.dto;

import com.ewp.crm.models.UserRoutes;

public class UserRoutesDto {
    private Long id;
    private Long user_id;
    private Integer weight;
    private UserRoutes.UserRouteType userRouteType;

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

    public UserRoutesDto() {
    }

    public UserRoutesDto(Long user_id, Integer weight, UserRoutes.UserRouteType userRouteType) {
        this.user_id = user_id;
        this.weight = weight;
        this.userRouteType = userRouteType;
    }

    public static UserRoutes getUserRoutesFromDto(UserRoutesDto userRoutesDto){
        return  new UserRoutes(userRoutesDto.getWeight(),userRoutesDto.getUserRouteType());
    }

}
