package com.ewp.crm.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_routes")
public class UserRoutes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_routes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "weight")
    private Integer weight;   // weight of the route in percent 0-100

    @Column(name = "userRouteType")
    @Enumerated(EnumType.STRING)
    private UserRouteType userRouteType;

    public enum UserRouteType {
        FROM_JM_EMAIL,     // канал с которого
        FROM_VK         //поступают заявки
    }

    public UserRoutes() {
    }

    public UserRoutes(Integer weight, UserRouteType userRouteType) {

        this.weight = weight;
        this.userRouteType = userRouteType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public UserRouteType getUserRouteType() {
        return userRouteType;
    }

    public void setUserRouteType(UserRouteType userRouteType) {
        this.userRouteType = userRouteType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoutes that = (UserRoutes) o;
        return Objects.equals(user, that.user) &&
                userRouteType == that.userRouteType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user,  userRouteType);
    }

}
