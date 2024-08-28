package com.springsecurity.demo.Services;

import com.springsecurity.demo.Enums.Role;
import com.springsecurity.demo.Repository.UserRepository;
import com.springsecurity.demo.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    public void saveUser(User user) {
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void updateUserDetails(User updatedUser, String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        existingUser.setFirstname(updatedUser.getFirstname());
        existingUser.setLastname(updatedUser.getLastname());
        existingUser.setEmail(updatedUser.getEmail());

        userRepository.save(existingUser);
    }

    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
//        return userRepository.findByEmail(username);
        User user = new User();
        user.setLastname("Addo");
        user.setEmail("Ebenezer@"+ThreadLocalRandom.current().nextInt(1000, 10000)+".com");
        user.setPassword(bCryptPasswordEncoder.encode("oauth2user"));
        user.setRole("ROLE_"+ Role.USER.toString());
        userRepository.save(user);
        return user;

    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public String getCurrentUserFullName() {
        User user = this.getCurrentUser();
        return user.getFirstname() + " " + user.getLastname();
    }


}
