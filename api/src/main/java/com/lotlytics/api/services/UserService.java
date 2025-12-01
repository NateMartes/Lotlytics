package com.lotlytics.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.lotlytics.api.entites.exceptions.ConflictException;
import com.lotlytics.api.entites.exceptions.NotFoundException;
import com.lotlytics.api.entites.exceptions.UnauthorizedException;
import com.lotlytics.api.entites.token.UserToken;
import com.lotlytics.api.entites.user.CreateUserPayload;
import com.lotlytics.api.entites.user.LoginUserPayload;
import com.lotlytics.api.entites.user.User;
import com.lotlytics.api.repositories.UserRepository;
import com.lotlytics.api.repositories.UserTokensRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    
    private UserRepository userRepository;
    private UserTokensRepository userTokensRepository;
    private PasswordService passwordService;
    private JwtService jwtService;

    public UserService(UserRepository userRepository, UserTokensRepository userTokensRepository, PasswordService passwordService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userTokensRepository = userTokensRepository;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
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
            throw new ConflictException("Email already exists.");
        }
        
        String hashedPassword = passwordService.hashPassword(payload.getPassword());
        User u = new User(username, email, hashedPassword);
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

    /**
     * The getUserByUsername method returns a user by checking the username in the database.
     * @return A User.
     * @throws NotFoundException
     */
    public User getUserByUsername(String username) throws NotFoundException {
        User u = new User();
        u.setUsername(username);
        Optional<User> out = userRepository.findOne(Example.of(u));
        if (out.isEmpty()) {
            throw new NotFoundException("User does not exist");
        }
        return out.get();
    }

     /**
     * The loginUser method checks if a user is logged in, and if so, create a JWT token for the user.
     * @return A JWT Token
     * @throws NotFoundException
     * @throws UnauthorizedException
     */
    public String loginUser(LoginUserPayload payload) throws NotFoundException, UnauthorizedException {
        String username = payload.getUsername();
        String password = payload.getPassword();
        if (!isAUserByUsername(username)) {
            throw new NotFoundException("User does not exist");
        }

        User u = getUserByUsername(username);
        if (!passwordService.matches(password, u.getPassword())) {
            throw new UnauthorizedException("");
        }

        String token = jwtService.generateToken(username);

        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUserId(u.getId());
        userToken.setExpiresAt(jwtService.extractExpiration(token));
        userTokensRepository.save(userToken);

        return token;
    }

    /**
     * The logoutUser method removes the token that is stored for this user, invalidating their login token.
     * 
     * @param username A username
     * @throws NotFoundException If the user or token does not exist
     */
    public void logoutUser(String username) throws NotFoundException {

        User u = getUserByUsername(username);
        UserToken example = new UserToken();
        example.setUserId(u.getId());

        List<UserToken> matches = userTokensRepository.findAll(Example.of(example));
        if (matches.size() == 0) {
            throw new NotFoundException("User token not found");
        }

        userTokensRepository.deleteAll(matches);

        log.info("User " + matches.get(0).getId() + "'s tokens were expired");
    }

    /**
     * The loadUserByUsername method is needed for the UserDetailsService
     * @param username
     * @return 
     * @throws UsernameNotFoundException
    */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }   
}
