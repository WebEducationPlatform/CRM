package com.ewp.crm.repository.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface CommonGenericRepository<T> extends JpaRepository<T, Long> {

}
