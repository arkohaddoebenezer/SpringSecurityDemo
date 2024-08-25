package com.springsecurity.demo.Controllers;

import com.springsecurity.demo.Model.User;
import com.springsecurity.demo.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute("user") User user, BindingResult result, Model model) {
        if (userService.emailExists(user.getEmail())) {
            model.addAttribute("emailError", "Email already exists");
            return "signup";
        }
        userService.saveUser(user);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    public String showUpdateForm(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login?logout";
        }
        model.addAttribute("user", user);
        return "userprofile";
    }

    @PostMapping("/profile")
    public String updateUser(@ModelAttribute User updatedUser, @AuthenticationPrincipal UserDetails currentUser,
                             RedirectAttributes redirectAttributes) {
        userService.updateUserDetails(updatedUser, currentUser.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "userprofile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match");
            redirectAttributes.addFlashAttribute("activeTab", "password");
            return "redirect:/profile#password-tab";
        }

        if (currentPassword.equals(newPassword)) {
            redirectAttributes.addFlashAttribute("passwordError", "New password must be different from the current password");
            redirectAttributes.addFlashAttribute("activeTab", "password");
            return "redirect:/profile#password-tab";
        }

        try {
            userService.changePassword(currentUser.getUsername(), currentPassword, newPassword);
            redirectAttributes.addFlashAttribute("passwordSuccess", "Password changed successfully");
            return "redirect:/profile#password-tab";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("activeTab", "password");
            return "redirect:/profile#password-tab";
        }
    }

    @GetMapping("/profilePicture/{id}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user != null && user.getProfilePicture() != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(user.getProfilePicture());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
