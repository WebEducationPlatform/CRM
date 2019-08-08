package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.ClientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ClientRepository extends CommonGenericRepository<Client>, ClientRepositoryCustom {

    Client getClientBySocialProfiles(List<SocialProfile> list);

    List<Client> getClientsByOwnerUser(User ownerUser);

    List<Client> getAllByStatus(Status status);

    Client getClientBySkype(String skypeLogin);

    List<Client> getAllByIdIn(List<Long> ids);

    @Override
    Page<Client> findAll(Pageable pageable);

    Page<Client> getAllByOwnerUser(Pageable pageable, User clientOwner);

    Client getClientByNameAndLastNameIgnoreCase(String name, String lastName);

    Client getClientById(Long id);

    Client getClientByClientPhonesEquals(String phoneNumber);

    default Client getClientByPhoneNumber(String phoneNumber) {
        return getClientByClientPhonesEquals(phoneNumber);
    }

    Client getClientByClientEmailsEquals(String Email);

    default Client getClientByEmail(String Email) {
        return getClientByClientEmailsEquals(Email);
    }

    @Query(value = "from Client c inner join c.clientEmails ce where ce in :emails")
    List<Client> getClientsOfEmails(@Param("emails") List<String> emails);

    @Query(nativeQuery = true,
            value = "SELECT c.client_id, c.first_name, c.last_name, ce.client_email " +
                    "FROM client c " +
                    "JOIN client_emails ce ON c.client_id = ce.client_id " +
                    "WHERE LOWER(ce.client_email) IN :emails " /*+
                    "OR LOWER(c.email) IN :emails"*/
    )
    List<ClientDto.ClientTransformer> getClientsDtoByEmails(@Param("emails") List<String> emails);

    @Query(nativeQuery = true,
            value = "SELECT city FROM client GROUP BY city ORDER BY city")
    List<String> getClientsCities();

    @Query(nativeQuery = true,
            value = "SELECT country FROM client GROUP BY country ORDER BY country")
    List<String> getClientsCountries();
}
