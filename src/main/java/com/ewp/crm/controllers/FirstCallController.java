package com.ewp.crm.controllers;

import com.ewp.crm.models.dto.AssignSkypeCallsDto;
import com.ewp.crm.models.Role;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/firstCallClients")
@PreAuthorize("hasAnyAuthority('OWNER','HR','MENTOR')")
public class FirstCallController {
    
    private final AssignSkypeCallService assignSkypeCallService;
    
    @Autowired
    public FirstCallController(AssignSkypeCallService assignSkypeCallService) {
        this.assignSkypeCallService = assignSkypeCallService;
    }
    
    // Собираем всех клиентов, которым назначен первый созвон
    // Контроллер по умолчанию
    @GetMapping("")
    public ModelAndView firstCallClients() {
        return new ModelAndView("first-call-table");
    }
    
    // Контроллер который корректирует неправильные данные (дубли)
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AssignSkypeCallsDto> getAllFirstCallClients(@AuthenticationPrincipal User userFromSession) {
        try {
            boolean isMentor = false;
            List<Role> roles = userFromSession.getRole();
            for (Role role : roles) {
                if (role.getRoleName().equals("MENTOR")) {
                    isMentor = true;
                    break;
                }
            }
    
            AssignSkypeCallsDto calls =
                    new AssignSkypeCallsDto(assignSkypeCallService.getAssignSkypeCallClientsWithoutMentors(),
                            isMentor, userFromSession.getId());
            
            if (calls.getCalls().isEmpty()) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(calls);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}