package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long>, StatusRepositoryCustom {

    Status getStatusByName(String name);

    Status getStatusByClientsIn(List<Client> users);

    List<Status> getStatusesByClientsOwnerUser(User ownerUser);

    List<Status> getAllByOrderByIdAsc();

    List<Status> getAllByRole(Role role);

    @Query("SELECT MAX(s.position) from Status s")
    Long findMaxPosition();

    @Query("SELECT s FROM Status s WHERE s.createStudent = true")
    List<Status> getAllStatusesForStudents();

    @Query(value = "SELECT s.status_id FROM status s WHERE s.create_student IS TRUE", nativeQuery = true)
    List<BigInteger> getAllStatusesIdsForStudents();

    @Query(value = "SELECT status_id FROM status WHERE is_invisible = 0", nativeQuery = true)
    List<BigInteger> getAllIdsWhichNotInvisible();

    @Query(value = "SELECT status_name FROM status WHERE status_id = ?1", nativeQuery = true)
    String getStatusNameById(Long id);

    @Query(value = "SELECT ss.position FROM SortedStatuses ss WHERE ss.user = ?1 and ss.status = ?2")
    Long getStatusPositionById(User user, Status status);

}
