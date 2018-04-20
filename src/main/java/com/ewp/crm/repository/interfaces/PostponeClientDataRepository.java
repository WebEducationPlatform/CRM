package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.PostponeClientData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostponeClientDataRepository extends JpaRepository<PostponeClientData, Long>, PostponeClientDataRepositoryCustom {
}
