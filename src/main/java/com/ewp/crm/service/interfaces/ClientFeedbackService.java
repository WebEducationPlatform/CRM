package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ClientFeedback;

public interface ClientFeedbackService {

    ClientFeedback addFeedback(ClientFeedback feedback);

    ClientFeedback createFeedback(String socialurl, String text, String videoUrl);

    //List<Feedback> getAllFeedbacks();
}
