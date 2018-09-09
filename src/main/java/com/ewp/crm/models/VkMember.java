package com.ewp.crm.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "vk_member")
public class VkMember implements Serializable{

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "vk_id", unique = true)
    private Long vkId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    public VkMember(Long vkId, Long groupId){
        this.vkId = vkId;
        this.groupId = groupId;
    }

    public VkMember(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VkMember vkMember = (VkMember) o;
        return Objects.equals(vkId, vkMember.vkId) &&
                Objects.equals(groupId, vkMember.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vkId, groupId);
    }

    public Long getVkId() {
        return vkId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setVkId(Long vkId) {
        this.vkId = vkId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
