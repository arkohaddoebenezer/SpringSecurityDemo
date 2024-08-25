package com.springsecurity.demo.Services;

import com.springsecurity.demo.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final PostRepository postRepository;

    @Autowired
    public MessageService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<com.springsecurity.demo.Model.Post> getAllMessages() {
        return postRepository.findAll();
    }

    public Optional<com.springsecurity.demo.Model.Post> getMessageById(Long id) {
        return postRepository.findById(id);
    }

    public com.springsecurity.demo.Model.Post createMessage(com.springsecurity.demo.Model.Post message) {
        return postRepository.save(message);
    }

    public com.springsecurity.demo.Model.Post updateMessage(Long id, com.springsecurity.demo.Model.Post messageDetails) {
        return postRepository.findById(id)
                .map(message -> {
                    message.setContent(messageDetails.getContent());
                    return postRepository.save(message);
                })
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    public void deleteMessage(Long id) {
        postRepository.deleteById(id);
    }
}