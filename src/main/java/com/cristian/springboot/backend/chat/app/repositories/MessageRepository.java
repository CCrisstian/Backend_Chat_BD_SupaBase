package com.cristian.springboot.backend.chat.app.repositories;

import com.cristian.springboot.backend.chat.app.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findFirst10ByOrderByDateAsc();
}
