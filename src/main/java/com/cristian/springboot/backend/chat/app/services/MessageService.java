package com.cristian.springboot.backend.chat.app.services;

import com.cristian.springboot.backend.chat.app.models.Message;
import com.cristian.springboot.backend.chat.app.models.Usuario;
import com.cristian.springboot.backend.chat.app.repositories.MessageRepository;
import com.cristian.springboot.backend.chat.app.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UsuarioRepository usuarioRepository;

    public MessageService(MessageRepository messageRepository, UsuarioRepository usuarioRepository) {
        this.messageRepository = messageRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public String getRandomColor() {
        Random rand = new Random();
        int nextInt = rand.nextInt(0xffffff + 1);
        return String.format("#%06x", nextInt);
    }

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    // Busca al usuario en la BD. Si no existe, lo crea con un color aleatorio.
    public Usuario obtenerUsuario(Usuario usuarioParam) {
        return usuarioRepository.findByUsername(usuarioParam.getUsername()).orElseGet(() -> {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(usuarioParam.getUsername());
            // LLAMAMOS AL MÉTODO AQUÍ para obtener un color nuevo cada vez que se crea un usuario
            nuevoUsuario.setColor(getRandomColor());
            return usuarioRepository.save(nuevoUsuario);
        });
    }

    public Message save(Message message) {
        if (message.getUsuario() != null && message.getUsuario().getUsername() != null) {
            Usuario usuarioPersistido = obtenerUsuario(message.getUsuario());
            message.setUsuario(usuarioPersistido);
        }
        return messageRepository.save(message);
    }
}