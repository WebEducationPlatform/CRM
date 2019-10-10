package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
