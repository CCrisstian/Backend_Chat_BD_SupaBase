package com.cristian.springboot.backend.chat.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Habilita el servidor de WebSockets y el sistema de mensajes STOMP
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketLimitInterceptor webSocketLimitInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // PUNTO DE CONEXIÓN: Define la URL a la que se conectará el frontend inicialmente para abrir el WebSocket
        registry.addEndpoint("/chat-websocket")
                .setAllowedOrigins("http://localhost:4200") // Permite que Angular (u otro cliente local) se conecte sin errores de CORS
                .withSockJS(); // Soporte alternativo por si el navegador del cliente no soporta WebSockets puros
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // CANAL DE SALIDA (Recibir): Los clientes se SUSCRIBEN a rutas con este prefijo para RECIBIR mensajes del servidor
        registry.enableSimpleBroker("/chat/");

        // CANAL DE ENTRADA (Enviar): Los clientes usan este prefijo en la URL para ENVIAR mensajes al servidor (controladores)
        registry.setApplicationDestinationPrefixes("/app");
    }

    // REGISTRAMOS EL INTERCEPTOR DE LÍMITE DE USUARIOS
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketLimitInterceptor);
    }
}
