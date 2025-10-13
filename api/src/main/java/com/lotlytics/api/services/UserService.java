package com.lotlytics.api.services;

import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.exceptions.ConflictException;
import com.lotlytics.api.entites.user.CreateUserPayload;
import com.lotlytics.api.entites.user.User;
import com.lotlytics.api.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
    
    private UserRepository userRepository;
    private PasswordService passwordService;

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    /**
     * The isAUser method determines if a user exists in the database by id
     * @param userId
     * @return true if and only if the Id maps to a user.
     */
    public boolean isAUser(Integer userId) {
        return userRepository.existsById(userId);
    }

   /**
     * The isAUserByUsername method determines if a user exists in the database by username
     * @param username
     * @return true if and only if the username maps to a user.
     */
    public boolean isAUserByUsername(String username) {
        User u = new User();
        u.setUsername(username);
        return userRepository.exists(Example.of(u));
    }

   /**
     * The isAUserByEmail method determines if a user exists in the database by email
     * @param email
     * @return true if and only if the email maps to a user.
     */
    public boolean isAUserByEmail(String email) {
        User u = new User();
        u.setEmail(email);
        return userRepository.exists(Example.of(u));
    }

    /**
     * The createUser methods adds a brand new user to the database.
     * 
     * @param payload The new user to add.
     * @return A User object
     * @throws ConflictException if user already exists.
     */
    public User createUser(CreateUserPayload payload) throws ConflictException {
        String username = payload.getUsername();
        String email = payload.getEmail();

        if (isAUserByUsername(username)) {
            throw new ConflictException("Username already exists.");
        }
        if (isAUserByEmail(email)) {
            throw new ConflictException("User already is using this email.");
        }
        
        User u = new User(username, email, payload.getPassword());
        User out = userRepository.save(u);
        log.info("User '" + username + "' created");
        return out;
    }

    /**
     * The getAllUsers method returns a list of all stored users.
     * @return List of users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
