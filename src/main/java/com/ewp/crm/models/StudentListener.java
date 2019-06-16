package com.ewp.crm.models;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.math.BigDecimal;

public class StudentListener {

    @PrePersist
    @PreUpdate
    public void enableSlackNotificationForBigPayment(Student student) {
        if ("Учатся".equals(student.getClient().getStatus().getName())) {
            if (student.getPrice().compareTo(BigDecimal.valueOf(10000)) > 0 && !student.isNotifySlack()) {
                if (student.getClient().getSocialProfiles().stream().filter(x -> x.getSocialNetworkType().equals(SocialProfile.SocialNetworkType.SLACK)).findFirst().isPresent()) {
                    student.setNotifySlack(true);
                }
            }
        }
    }
}