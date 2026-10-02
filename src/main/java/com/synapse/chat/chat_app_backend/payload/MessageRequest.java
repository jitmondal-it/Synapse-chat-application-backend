package com.synapse.chat.chat_app_backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    private  String content;
    private String sender;
    private String roomId;

    private String messageType;
    private String fileId;
    private String fileName;
    private String fileType;


}
