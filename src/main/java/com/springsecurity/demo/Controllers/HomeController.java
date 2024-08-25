package com.springsecurity.demo.controllers;

import com.springsecurity.demo.Model.Post;
import com.springsecurity.demo.Services.PostService;
import com.springsecurity.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @GetMapping("/dashboard")
    public String home(Model model) {
        model.addAttribute("username", userService.getCurrentUser().getFirstname());
        model.addAttribute("posts", postService.getAllMessages());
        model.addAttribute("newPost", new Post());
        return "home";
    }


    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("username", userService.getCurrentUser().getFirstname());
        model.addAttribute("posts", postService.getAllMessages());
        model.addAttribute("newPost", new Post());
        return "home";
    }


    @PostMapping("/post")
    public String createPost(@Valid @ModelAttribute("newPost") Post newPost, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("posts", postService.getAllMessages());
            model.addAttribute("username", userService.getCurrentUser().getFirstname());
            return "home";
        }

        postService.createMessage(newPost);
        return "redirect:/dashboard";
    }
}
