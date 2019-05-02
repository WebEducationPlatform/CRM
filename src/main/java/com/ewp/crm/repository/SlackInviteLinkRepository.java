package com.ewp.crm.repository;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SlackInviteLink;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SlackInviteLinkRepository extends CommonGenericRepository<SlackInviteLink> {

    boolean existsByHash(String hash);

    SlackInviteLink getByHash(String hash);

    @Transactional
    void deleteByClient(Client client);

}
