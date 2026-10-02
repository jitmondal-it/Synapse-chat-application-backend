package com.synapse.chat.chat_app_backend.repositories;

import com.synapse.chat.chat_app_backend.entities.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room,String> {
    Room findByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
