package com.ewp.crm.repository;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SlackInviteLink;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;

public interface SlackInviteLinkRepository extends CommonGenericRepository<SlackInviteLink> {
    boolean existsByHash(String hash);
    SlackInviteLink getByHash(String hash);
    SlackInviteLink deleteByClient(Client client);
}
