package com.springsecurity.demo.Controller;

import com.springsecurity.demo.Model.Post;
import com.springsecurity.demo.Service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping(produces = "application/json; charset=UTF-8")
    public List<Post> getAllMessages() {
        return messageService.getAllMessages();
    }

    @GetMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Post> getMessageById(@PathVariable Long id) {
        Optional<Post> message = messageService.getMessageById(id);
        return message.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(produces = "application/json; charset=UTF-8", consumes = "application/json; charset=UTF-8")
    public Post createMessage(@Valid @RequestBody Post message) {
        return messageService.createMessage(message);
    }

    @PutMapping(value = "/{id}", produces = "application/json; charset=UTF-8", consumes = "application/json; charset=UTF-8")
    public ResponseEntity<Post> updateMessage(@PathVariable Long id, @RequestBody Post messageDetails) {
        try {
            Post updatedMessage = messageService.updateMessage(id, messageDetails);
            return ResponseEntity.ok(updatedMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
