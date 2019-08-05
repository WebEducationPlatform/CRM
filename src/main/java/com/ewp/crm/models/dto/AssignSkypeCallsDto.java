package com.ewp.crm.models.dto;

import com.ewp.crm.models.AssignSkypeCall;

import java.util.List;

// ДТО для работы с вкладкой "Первые", для транспортировки объектов в таблицу клиентов,
// которым назначен первый созвон и не назначен ментор
public class AssignSkypeCallsDto {
    private List<AssignSkypeCall> calls;
    private boolean needActions;
    private Long userId;
    
    public AssignSkypeCallsDto(List<AssignSkypeCall> calls, boolean needActions, Long userId) {
        this.calls = calls;
        this.needActions = needActions;
        this.userId = userId;
    }
    
    public List<AssignSkypeCall> getCalls() {
        return calls;
    }
    public boolean getNeedActions() {
        return needActions;
    }
    public Long getUserId() {
        return userId;
    }
}
