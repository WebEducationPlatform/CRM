package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Role;
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

	List<Status> getAllByRole(Role role);

	@Query("SELECT MAX(s.position) from Status s")
	Long findMaxPosition();

	@Query("SELECT s FROM Status s WHERE s.createStudent = true")
	List<Status> getAllStatusesForStudents();

	@Query("SELECT s.id FROM Status s")
	List<Long> getAllStatusesId();

	@Query(value = "SELECT status_id FROM status_roles where role_id = (SELECT id from role where role_name = ?1)", nativeQuery = true)
	List<Long> getAllStatusByRole(String roleName);

	String getStatusNameById(Long id);

	@Query(value = "SELECT is_invisible FROM status WHERE status_id = ?1", nativeQuery = true)
	boolean getStatusIsInvisibleById(Long id);

	@Query(value = "SELECT position FROM status WHERE status_id = ?1", nativeQuery = true)
	Long getPositionById(Long id);

	@Query("SELECT s.role FROM Status s WHERE s.id = ?1")
	List<Role> getRoleById(Long id);

	@Query(value = "SELECT create_student FROM status WHERE status_id = ?1", nativeQuery = true)
	Boolean getCreateStudentById(Long id);

	@Query(value = "SELECT trial_offset FROM status WHERE status_id = ?1", nativeQuery = true)
	Integer getTrialOffsetById(Long id);

	@Query(value = "SELECT next_payment_offset FROM status WHERE status_id = ?1", nativeQuery = true)
	Integer getNextPaymentOffsetById(Long id);
}