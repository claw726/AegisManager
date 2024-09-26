package com.aegis.project.service;

import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean createUser(String email, String name, String password) {

        if (userRepository.existsByEmail(email)) {
            return false;
        }
        UserModel user = new UserModel();
        user.setEmail(email);
        user.setUserName(name);
        user.setPWHash(passwordEncoder.encode(password));

        userRepository.save(user);
        return true;
    }
}
