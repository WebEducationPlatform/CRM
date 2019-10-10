package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Board;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/board")
public class BoardRestController {

    private static Logger logger = LoggerFactory.getLogger(BoardRestController.class);

    private final BoardService boardService;

    @Autowired
    public BoardRestController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity addNewBoard(@RequestParam(name = "boardName") String boardName,
                                      @AuthenticationPrincipal User currentAdmin) {

        final Board board = new Board(boardName);
        boardService.add(board);
        logger.info("{} has added board with name: {}", currentAdmin.getFullName(), board);
        return ResponseEntity.ok("Успешно добавлено");
    }

    @RequestMapping(value = "/boards", method = RequestMethod.GET)
    public @ResponseBody
    List<Board> boardsAjax() {
        return boardService.getAll();
    }
}
