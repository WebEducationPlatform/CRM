package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ClientFeedback;
import java.util.List;
import java.util.Optional;

public interface ClientFeedbackService {

    Optional<ClientFeedback> addFeedback(ClientFeedback feedback);

    ClientFeedback createFeedback(String socialUrl, String text, String videoUrl);

    Optional<List<ClientFeedback>> getAllByClientId(Long id);

    Optional<List<ClientFeedback>> getAllFeedback();

    void deleteFeedback(Long id);

    void updateFeedback(ClientFeedback feedback);
}
