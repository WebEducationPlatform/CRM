package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.*;
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

    @Query(value = "SELECT us FROM UserStatus us WHERE us.isInvisible = 0 and us.user_id = :user_id")
    List<UserStatus> getAllIdsWhichNotInvisible(Long user_id);

    @Query(value = "SELECT status_name FROM status WHERE status_id = ?1", nativeQuery = true)
    String getStatusNameById(Long id);

    @Query(value = "SELECT position FROM status WHERE status_id = ?1", nativeQuery = true)
    Long getStatusPositionById(Long id); //кто будет использовать обязательно добавьте отбор по User

}
