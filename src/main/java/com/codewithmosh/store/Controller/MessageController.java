package com.codewithmosh.store.Controller;

import com.codewithmosh.store.entities.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @RequestMapping("/message")
    public Message message() {
        return new Message(1,"Hello World");
    }

}
