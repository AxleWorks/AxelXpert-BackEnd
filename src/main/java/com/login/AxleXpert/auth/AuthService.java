package com.login.AxleXpert.auth;

import com.login.AxleXpert.checkstatus.CheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public CheckService.User registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already taken");
        }
        CheckService.User user = new CheckService.User();
        user.setUsername(username);
        user.setPassword(password);

        return userRepository.save(user);
    }

    public boolean loginUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> user.getPassword().equals(password))
                .orElse(false);
    }
}
