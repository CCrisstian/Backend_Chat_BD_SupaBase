package com.cristian.springboot.backend.chat.app.controllers;

import com.cristian.springboot.backend.chat.app.models.Message;
import com.cristian.springboot.backend.chat.app.models.Usuario;
import com.cristian.springboot.backend.chat.app.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class ChatController {

    private final MessageService service;

    @Autowired
    private SimpMessagingTemplate webSocket;

    public ChatController(MessageService service) {
        this.service = service;
    }


    // RECIBIR: Captura los mensajes que el cliente ENVIÓ hacia la ruta "/app/message"
    @MessageMapping("/message")
    // ENVIAR: Todo lo que retorne este método se transmitirá automáticamente a TODOS los clientes que estén SUSCRITOS al canal "/chat"
    @SendTo("/chat/message")
    public Message receiveMessage(Message message) {

        // Le damos la marca de tiempo actual al mensaje apenas llega al servidor
        message.setDate(Instant.now());

        // PROCESAR: Si es un usuario nuevo conectándose, lo pasamos al servicio para que lo guarde/recupere en la BD
        if (message.getType().equals("NEW_USER")) {
            Usuario usuario = service.obtenerUsuario(message.getUsuario());
            message.setUsuario(usuario);
            message.setText("Nuevo Usuario");
            return message;
        }
        // PUBLICAR: Si es un mensaje de chat normal, al hacer service.save() el servicio se asegurará del usuario y guardará el mensaje en Supabase
        return service.save(message);

    }

    // RECIBIR: Escucha cuando un cliente avisa que empezó a teclear (enviando su username a la ruta "/app/writing")
    @MessageMapping("/writing")
    // ENVIAR: Reenvía el texto resultante automáticamente a TODOS los clientes suscritos al canal "/chat/writing"
    @SendTo("/chat/writing")
    public String isWriting(String username) {

        // PROCESAR: Toma el nombre del usuario recibido y le agrega el texto de acción
        return username.concat(" está escribiendo...");
    }

    // RECIBIR: Escucha cuando un usuario solicita el historial enviando su ID (Long) a la ruta "/app/history"
    @MessageMapping("/history")
    public void getHistoryMessages(Long usuarioId) {

        // ENVIAR MANUALMENTE: Dispara la lista de mensajes solo al canal privado de ese usuario
        webSocket.convertAndSend("/chat/history/".concat(usuarioId.toString()), service.findAll());
    }
}
