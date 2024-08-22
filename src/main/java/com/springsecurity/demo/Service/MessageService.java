package com.springsecurity.demo.Service;

import com.springsecurity.demo.Model.Post;
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

    public List<Post> getAllMessages() {
        return postRepository.findAll();
    }

    public Optional<Post> getMessageById(Long id) {
        return postRepository.findById(id);
    }

    public Post createMessage(Post message) {
        return postRepository.save(message);
    }

    public Post updateMessage(Long id, Post messageDetails) {
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
