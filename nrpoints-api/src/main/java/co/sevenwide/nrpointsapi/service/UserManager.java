package co.sevenwide.nrpointsapi.service;

import co.sevenwide.nrpointsapi.document.PasswordResetToken;
import co.sevenwide.nrpointsapi.document.User;
import co.sevenwide.nrpointsapi.exception.UserNotFoundException;
import co.sevenwide.nrpointsapi.repository.PasswordResetTokenRepository;
import co.sevenwide.nrpointsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
public class UserManager implements UserDetailsManager {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserDetails user) {
        ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save((User) user);
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.save((User) user);
    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    //changed to find by email
    @Override
    public boolean userExists(String username) {
        return userRepository.existsByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByEmail(usernameOrEmail).orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("User with email {0} not found", usernameOrEmail)));
    }

    public UserDetails loadUserById(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(MessageFormat.format("User with id {0} not found", id)));
    }

    public UserDetails loadUserByPasswordResetToken(String token) {
        Optional<PasswordResetToken> resultToken = passwordResetTokenRepository.findByToken(token);
        return resultToken.map(passwordResetToken -> userRepository.findById(passwordResetToken.getUserId()).orElseThrow(() -> new UserNotFoundException("No user found under provided token"))).orElseThrow(() -> new UserNotFoundException("No token object found under provided token"));
    }

    public void createPasswordResetTokenForUser(UserDetails user, String token) {
        PasswordResetToken userToken = new PasswordResetToken(token, ((User) user).getId());
        passwordResetTokenRepository.save(userToken);
    }


    public void updatePassword(UserDetails user, String newPassword) {
        ((User) user).setPassword(passwordEncoder.encode(newPassword));
        userRepository.save((User) user);
    }

}
