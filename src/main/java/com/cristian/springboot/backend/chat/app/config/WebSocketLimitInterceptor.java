package com.cristian.springboot.backend.chat.app.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketLimitInterceptor implements ChannelInterceptor {

    private static final int MAX_USERS = 4;
    private final Set<String> activeSessions = ConcurrentHashMap.newKeySet();

    // Inyectamos con @Lazy para evitar dependencias circulares al arrancar Spring
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketLimitInterceptor(@Lazy SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private void emitirConteoUsuarios() {
        // Publica el número de usuarios al canal /chat/stats
        try {
            messagingTemplate.convertAndSend("/chat/stats", activeSessions.size());
        } catch (Exception e) {
            System.err.println("Error enviando estadísticas: " + e.getMessage());
        }
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Si el cliente envía el comando de conexión (CONNECT)
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            if (activeSessions.size() >= MAX_USERS) {
                // Al lanzar esta excepción, Spring Boot aborta el handshake y envía un frame STOMP de tipo ERROR al frontend
                throw new MessagingException("¡Servidor lleno! Se ha alcanzado el límite máximo de 4 usuarios conectados.");
            }
            activeSessions.add(accessor.getSessionId());
            System.out.println("Usuario conectado al WS. Total activos: " + activeSessions.size());

            // Emitir el conteo actualizado a todos en la sala
            emitirConteoUsuarios();
        }
        return message;
    }

    // Se dispara automáticamente al desconectarse, cerrar pestaña o perder internet
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        activeSessions.remove(event.getSessionId());
        System.out.println("Usuario desconectado del WS. Total activos: " + activeSessions.size());
    }
}