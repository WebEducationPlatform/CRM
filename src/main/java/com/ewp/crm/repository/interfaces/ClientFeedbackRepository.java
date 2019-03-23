package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback, Long> {

    ClientFeedback getClientFeedbackById(Long id);

    List<ClientFeedback> getAllByClientId(Long id);

}
