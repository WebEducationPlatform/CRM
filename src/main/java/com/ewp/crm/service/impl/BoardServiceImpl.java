package com.ewp.crm.service.impl;

import com.ewp.crm.models.Board;
import com.ewp.crm.repository.interfaces.BoardRepository;
import com.ewp.crm.service.interfaces.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardServiceImpl(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Override
    public void add(Board board) {
        boardRepository.save(board);
    }

    @Override
    public List<Board> getAll() {
        return boardRepository.findAll();
    }

    @Override
    public Board get(Long id) {
        return boardRepository.findById(id).get();
    }

    @Override
    public void update(Board board) {
        boardRepository.save(board);
    }
}
