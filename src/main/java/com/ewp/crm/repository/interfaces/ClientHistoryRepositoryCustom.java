package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.dto.ClientHistoryDto;

import java.util.List;

public interface ClientHistoryRepositoryCustom {

    List<ClientHistoryDto> getAllDtoByClientId(long id, int page, int pageSize);

}
