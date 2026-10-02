package com.synapse.chat.chat_app_backend.controllers;


import com.mongodb.client.gridfs.model.GridFSFile;
import com.synapse.chat.chat_app_backend.entities.Message;
import com.synapse.chat.chat_app_backend.entities.Room;
import com.synapse.chat.chat_app_backend.repositories.RoomRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin("http://localhost:5173")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    //create room
    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody String roomId,@RequestParam String username){

        if(roomRepository.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room is already exist !");
        }

        Room room = new Room();
        room.setRoomId(roomId);
        room.setCreatedBy(username);

        room.getMembers().add(username);

        Room savedRoom = roomRepository.save(room);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    // get : join room
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId,@RequestParam String username){
        Room room = roomRepository.findByRoomId(roomId);

        if(room == null){
            return ResponseEntity.badRequest().body("Room not exist");
        }

        if(!room.getMembers().contains(username)){
            room.getMembers().add(username);
            roomRepository.save(room);
        }

        return ResponseEntity.ok(room);
    }

    //get messages of room
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page",defaultValue = "0",required = false) int page,
            @RequestParam(value = "size",defaultValue = "25",required = false) int size
    ){
        Room room = roomRepository.findByRoomId(roomId);
        if(room == null){
            return  ResponseEntity.badRequest().build();
        }

        //get message
        //pagination

        List<Message> messages = room.getMessages();
        int start = Math.max(0,messages.size() - (page + 1) * size);
        int end = Math.min(messages.size() , start + size);
        List<Message> paginatedMessages = messages.subList(start,end);

        return  ResponseEntity.ok(paginatedMessages);

    }


    // Deleting the chat of the rooms
    @DeleteMapping("/{roomId}/messages")
    public ResponseEntity<?> clearChat(@PathVariable String roomId) {

        Room room = roomRepository.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        // Delete files associated with messages
        for (Message message : room.getMessages()) {

            if ("FILE".equals(message.getMessageType())
                    && message.getFileId() != null
                    && !message.getFileId().isBlank()) {

                try {
                    ObjectId fileId = new ObjectId(message.getFileId());

                    gridFsTemplate.delete(
                            new Query(
                                    Criteria.where("_id").is(fileId)
                            )
                    );

                } catch (IllegalArgumentException e) {
                    // Invalid ObjectId - ignore and continue
                }
            }
        }

        // Clear messages from the room
        room.getMessages().clear();

        roomRepository.save(room);

        return ResponseEntity.ok("Chat cleared successfully");
    }

    // Deleting the room along with the files chunks
    @DeleteMapping("/{roomId}")
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {

        Room room = roomRepository.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        // Delete files associated with this room's messages
        for (Message message : room.getMessages()) {

            if ("FILE".equals(message.getMessageType())
                    && message.getFileId() != null
                    && !message.getFileId().isBlank()) {

                try {
                    ObjectId fileId = new ObjectId(message.getFileId());

                    gridFsTemplate.delete(
                            new Query(
                                    Criteria.where("_id").is(fileId)
                            )
                    );

                } catch (IllegalArgumentException e) {
                    // Invalid file ID - continue deleting the room
                }
            }
        }

        // Delete the room itself
        roomRepository.delete(room);

        return ResponseEntity.ok("Room deleted successfully");
    }


    //upload the file/photo
    @PostMapping("/{roomId}/files")
    public ResponseEntity<?> uploadFile(
            @PathVariable String roomId,
            @RequestParam("file") MultipartFile file) {

        try {

            // Check whether room exists
            Room room = roomRepository.findByRoomId(roomId);

            if (room == null) {
                return ResponseEntity.notFound().build();
            }

            // Store actual file data in MongoDB GridFS
            Object fileId = gridFsTemplate.store(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType()
            );

            return ResponseEntity.ok().body(
                    java.util.Map.of(
                            "fileId", fileId.toString(),
                            "fileName", file.getOriginalFilename(),
                            "fileType", file.getContentType()
                    )
            );

        } catch (IOException e) {

            return ResponseEntity.internalServerError()
                    .body("Failed to upload file");
        }
    }


    @GetMapping("/files/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId) {

        try {

            ObjectId objectId = new ObjectId(fileId);

            GridFSFile file = gridFsTemplate.findOne(
                    new Query(
                            Criteria.where("_id").is(objectId)
                    )
            );

            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            GridFsResource resource = gridFsTemplate.getResource(file);

            String contentType = resource.getContentType();

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if (contentType != null) {
                try {
                    mediaType = MediaType.parseMediaType(contentType);
                } catch (Exception ignored) {
                }
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(
                            "Content-Disposition",
                            "inline; filename=\"" + resource.getFilename() + "\""
                    )
                    .body(new InputStreamResource(resource.getInputStream()));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body("Invalid file ID");
        }
    }

    // Get members of a room
    @GetMapping("/{roomId}/members")
    public ResponseEntity<?> getRoomMembers(@PathVariable String roomId) {

        Room room = roomRepository.findByRoomId(roomId);

        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        response.put(
                "createdBy",
                room.getCreatedBy() != null ? room.getCreatedBy() : ""
        );

        response.put(
                "members",
                room.getMembers() != null ? room.getMembers() : java.util.List.of()
        );

        return ResponseEntity.ok(response);
    }

}
