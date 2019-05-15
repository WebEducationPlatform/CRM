package com.ewp.crm.exceptions.member;

public class NotFoundMemberList extends RuntimeException {
    public NotFoundMemberList(String message){
        super(message);
    }
}
