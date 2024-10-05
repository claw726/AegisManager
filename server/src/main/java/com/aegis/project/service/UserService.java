package com.aegis.project.service;

import com.aegis.project.model.UserModel;
import com.aegis.project.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public boolean createUser(String email, String name, String password) {

        if (userRepository.existsByEmail(email)) {
            return false;
        }
        UserModel user = new UserModel(name, email, passwordEncoder.encode(password));

        userRepository.save(user);
        return true;
    }
}
