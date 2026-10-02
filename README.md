# Synapse : Real-Time Chat Application

> A real-time room-based chat application built with Spring Boot, WebSocket, STOMP, and MongoDB.

Synapse is a real-time chat application that I built to understand how communication between multiple users works beyond traditional REST APIs.

The application supports room creation and joining, real-time messaging, typing indicators, file sharing and cloud database storage with MongoDB Atlas.

---

## ✨ Features

- 💬 Real-time messaging using WebSocket and STOMP
- 🏠 Create and join chat rooms
- 👥 View room members
- 👑 Identify the room creator
- ✍️ Real-time typing indicator
- 📎 File sharing in chat
- 🖼️ Image sharing and display
- 🗑️ Clear room messages
- 🚪 Leave a room
- ❌ Delete a room
- ☁️ MongoDB Atlas integration
- 📦 MongoDB GridFS for file storage
- 🔄 REST API + WebSocket communication

---

## 🛠️ Tech Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring WebSocket
- STOMP
- SockJS
- MongoDB
- MongoDB GridFS
- Maven

## 🏗️ Architecture

```text
                    ┌─────────────────────┐
                    │       User          │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │   React Frontend    │
                    │   React + Vite      │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    │                     │
                 REST API            WebSocket
                    │                 + STOMP
                    │                     │
                    ▼                     ▼
             ┌─────────────────────────────────┐
             │        Spring Boot Backend      │
             │                                 │
             │  Room APIs   Message Handling   │
             │  File APIs   Typing Events      │
             └───────────────┬─────────────────┘
                             │
                             ▼
                  ┌─────────────────────┐
                  │     MongoDB Atlas   │
                  │                     │
                  │  Rooms & Messages   │
                  │  GridFS Files       │
                  └─────────────────────┘
