package com.cristian.springboot.backend.chat.app.controllers;

import com.cristian.springboot.backend.chat.app.config.WebSocketLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")// Inyecta dinámicamente los dominios
@CrossOrigin(originPatterns = "${cors.allowed-origins}")
public class ChatStatsController {

    @Autowired
    private WebSocketLimitInterceptor limitInterceptor;

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        // Devuelve un JSON: {"activeUsers": 2}
        return Collections.singletonMap("activeUsers", limitInterceptor.getActiveSessionsCount());
    }
}