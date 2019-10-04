package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Board;

import java.util.List;

public interface BoardService {

    void add(Board board);

    List<Board> getAll();

    Board get(Long id);

    void update(Board board);

}
