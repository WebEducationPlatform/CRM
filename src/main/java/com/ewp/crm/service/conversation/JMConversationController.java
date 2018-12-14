package com.ewp.crm.service.conversation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/conversation")
public class JMConversationController {

    private final JMConversationHelper conversationHelper;

    @Autowired
    public JMConversationController(JMConversationHelper conversationHelper) {
        this.conversationHelper = conversationHelper;
    }


    //Need to create rest for:

    //send message

    //mark message as read

    //get new messages

    //get messages

    //init chat with client

    //close chat with client
}
