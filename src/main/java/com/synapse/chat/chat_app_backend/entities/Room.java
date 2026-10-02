package com.synapse.chat.chat_app_backend.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "rooms")
public class Room {

    private String id;
    private  String roomId;
    private String createdBy;
    private List<String> members = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
}
