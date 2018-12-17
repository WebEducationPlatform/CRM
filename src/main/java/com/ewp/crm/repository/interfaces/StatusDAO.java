package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusDAO extends JpaRepository<Status, Long> {

    Status getStatusByName(String name);

    Status getStatusByClientsIn(List<Client> users);

    List<Status> getStatusesByClientsOwnerUser(User ownerUser);

    List<Status> getAllByOrderByIdAsc();

    @Query("SELECT MAX(s.position) from Status s")
    Long findMaxPosition();

    @Query("SELECT s FROM Status s WHERE s.createStudent = true")
    List<Status> getAllStatusesForStudents();
}
