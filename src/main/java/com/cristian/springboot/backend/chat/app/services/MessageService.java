package com.cristian.springboot.backend.chat.app.services;

import com.cristian.springboot.backend.chat.app.models.Message;
import com.cristian.springboot.backend.chat.app.models.Usuario;
import com.cristian.springboot.backend.chat.app.repositories.MessageRepository;
import com.cristian.springboot.backend.chat.app.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private static final String[] COLORS = {
        "red", "green", "blue", "yellow", "magenta", "purple"
    };

    private final MessageRepository messageRepository;
    private  final UsuarioRepository usuarioRepository;

    public MessageService(MessageRepository messageRepository, UsuarioRepository usuarioRepository) {
        this.messageRepository = messageRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    // Busca al usuario en la BD. Si no existe, lo crea con un color aleatorio.
    public Usuario obtenerUsuario(Usuario usuarioParam) {
        return usuarioRepository.findByUsername(usuarioParam.getUsername()).orElseGet(() -> {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(usuarioParam.getUsername());
            String colorRandom = COLORS[(int) (Math.random() * COLORS.length)];
            nuevoUsuario.setColor(colorRandom);
            return usuarioRepository.save(nuevoUsuario);
        });
    }

    public Message save(Message message) {
        // Nos aseguramos de buscar al usuario en la base de datos para que Hibernate
        // lo reconozca como una entidad existente y administrada con su ID real
        if (message.getUsuario() != null && message.getUsuario().getUsername() != null) {
            Usuario usuarioPersistido = obtenerUsuario(message.getUsuario());
            message.setUsuario(usuarioPersistido);
        }

        return messageRepository.save(message);
    }
}
