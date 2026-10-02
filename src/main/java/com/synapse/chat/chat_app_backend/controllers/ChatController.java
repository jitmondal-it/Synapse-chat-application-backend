package com.synapse.chat.chat_app_backend.controllers;

import com.synapse.chat.chat_app_backend.entities.Message;
import com.synapse.chat.chat_app_backend.entities.Room;
import com.synapse.chat.chat_app_backend.payload.MessageRequest;
import com.synapse.chat.chat_app_backend.payload.TypingEvent;
import com.synapse.chat.chat_app_backend.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
@CrossOrigin("http://localhost:5173")
public class ChatController {

    @Autowired
    private RoomRepository roomRepository;


    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public Message sendMessage(
            @DestinationVariable String roomId,
            @RequestBody MessageRequest request){

        Room room = roomRepository.findByRoomId(request.getRoomId());
        Message message = new Message();
        message.setContent(request.getContent());
        message.setSender(request.getSender());
        message.setTimeStamp(LocalDateTime.now());
        message.setMessageType(request.getMessageType());
        message.setFileId(request.getFileId());
        message.setFileName(request.getFileName());
        message.setFileType(request.getFileType());
        if (room != null) {
            room.getMessages().add(message);
            roomRepository.save(room);
        } else {
            throw new RuntimeException("room not found !!");
        }

        return message;

    }

    @MessageMapping("/typing/{roomId}")
    @SendTo("/topic/typing/{roomId}")
    public TypingEvent typing(
            @DestinationVariable String roomId,
            @RequestBody TypingEvent event) {

        return event;
    }

}
