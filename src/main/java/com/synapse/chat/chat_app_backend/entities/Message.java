package com.synapse.chat.chat_app_backend.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {

    private  String sender;
    private String content;
    private LocalDateTime timeStamp;

    private String messageType;
    private String fileId;
    private String fileName;
    private String fileType;

    private Message(String sender,String consent){
        this.sender = sender;
        this.content = consent;
        this.timeStamp = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

}
