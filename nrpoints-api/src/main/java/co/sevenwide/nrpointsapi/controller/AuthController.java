package co.sevenwide.nrpointsapi.controller;

import co.sevenwide.nrpointsapi.document.User;
import co.sevenwide.nrpointsapi.dto.*;
import co.sevenwide.nrpointsapi.security.TokenGenerator;
import co.sevenwide.nrpointsapi.service.UserManager;
import co.sevenwide.nrpointsapi.service.UserSecurityManager;
import co.sevenwide.nrpointsapi.util.GenericResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    UserDetailsManager userDetailsManager;
    @Autowired
    UserSecurityManager securityService;
    @Autowired
    TokenGenerator tokenGenerator;
    @Autowired
    DaoAuthenticationProvider daoAuthenticationProvider;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    JwtAuthenticationProvider refreshTokenAuthProvider;

    @Value("${fe-client.url}")
    private String feClientURL;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody SignupDTO signupDTO) {
        if(!userDetailsManager.userExists(signupDTO.getEmail())) {
            User user = new User(signupDTO.getUsername(), signupDTO.getEmail(), signupDTO.getPassword(), "blue.png");
            userDetailsManager.createUser(user);

            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(user, signupDTO.getPassword(), Collections.EMPTY_LIST);
            //return ResponseEntity.ok(tokenGenerator.createToken(authentication));
            return ResponseEntity.ok(new GenericResponse("Successfully Registered User " + signupDTO.getUsername()));
        } else {
            return ResponseEntity.ok(new GenericResponse("User already exists with provided email", "Unable to register user"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenDTO> login(@RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        UserDetails user = userDetailsManager.loadUserByUsername(loginDTO.getEmail());
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getEmail(), loginDTO.getPassword()));
        TokensDTO tokens = tokenGenerator.createToken(authentication);


        Cookie cookie = new Cookie("refresh_token", tokens.getRefreshToken());
        cookie.setMaxAge(604800); // 3600 an hour, currently set to a week
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        response.addCookie(cookie);

        return ResponseEntity.ok(new AccessTokenDTO(tokens.getAccessToken(), UserDTO.from((User)user)));
    }

    /*
    @PostMapping("/token") //maybe just have on for the refresh? maybe don't have this
    public ResponseEntity token(@RequestBody TokensDTO tokensDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokensDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        // check if present in db and not revoked?
        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }
    */

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDTO> refresh(@CookieValue("refresh_token") String refreshToken) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(refreshToken));
        Jwt jwt = (Jwt) authentication.getCredentials();
        TokensDTO tokens = tokenGenerator.createToken(authentication);
        return ResponseEntity.ok(new AccessTokenDTO(tokens.getAccessToken(), null));
    }

    @PostMapping("/logout")
    public ResponseEntity<GenericResponse> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setPath("/api");
        response.addCookie(cookie);
        return ResponseEntity.ok(new GenericResponse("Successfully Logged Out"));
    }

    @PostMapping("/forgot")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody LoginDTO loginDTO, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        UserDetails user = userDetailsManager.loadUserByUsername(loginDTO.getEmail());
        String token = UUID.randomUUID().toString();
        ((UserManager) userDetailsManager).createPasswordResetTokenForUser(user, token);

        mailSender.send(constructResetTokenEmail(feClientURL, token, user));


        return ResponseEntity.ok(new GenericResponse("Sent reset password email to email: " + loginDTO.getEmail()));
    }

    @PostMapping("/resetPasswordValidate")
    public ResponseEntity<GenericResponse> validateResetPasswordToken(@RequestParam("token") String token) {
        String validationResult = securityService.validatePasswordResetToken(token);
        if(validationResult.equals("Valid")) {
            return ResponseEntity.ok(new GenericResponse(validationResult));
        } else { //delete token if it's not valid anymore
            if(validationResult.equals("Expired")) {
                securityService.deletePasswordResetToken(token);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Attempting to access reset password with invalid token", validationResult));
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody PasswordDTO passwordDTO) {
        String validationResult = securityService.validatePasswordResetToken(passwordDTO.getToken());

        if(validationResult.equals("Valid")) {
            if(passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
                UserDetails user = ((UserManager) userDetailsManager).loadUserByPasswordResetToken(passwordDTO.getToken());
                ((UserManager) userDetailsManager).updatePassword(user, passwordDTO.getPassword());
                securityService.deletePasswordResetToken(passwordDTO.getToken());
                return ResponseEntity.ok(new GenericResponse("Successfully reset password"));
            } else {
                return ResponseEntity.badRequest().body(new GenericResponse("Provided passwords do not match"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Reset password token is invalid or expired", validationResult));
        }
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<GenericResponse> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        try {
            Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(updatePasswordDTO.getEmail(), updatePasswordDTO.getOldPassword()));
            if(updatePasswordDTO.getNewPassword().equals(updatePasswordDTO.getConfirmNewPassword())) {
                UserDetails user = userDetailsManager.loadUserByUsername(updatePasswordDTO.getEmail());
                ((UserManager) userDetailsManager).updatePassword(user, updatePasswordDTO.getNewPassword());
                return ResponseEntity.ok(new GenericResponse("Successfully reset password"));
            } else {
                return ResponseEntity.badRequest().body(new GenericResponse("Provided passwords do not match"));
            }
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new GenericResponse("Password provided does not match stored password"));
            //return ResponseEntity.badRequest().body(new GenericResponse("Password provided does not match stored password"));
        }

    }



    // NON-API

    private MimeMessage constructResetTokenEmail(String contextPath, String token, UserDetails user) throws MessagingException, UnsupportedEncodingException {
        //String url = contextPath + "/user/changePassword?token=" + token;
        String url = contextPath + "/reset/" + token;
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("noreply@sevenwide.co", "Seven Wide Support");
        helper.setTo(((User) user).getEmail());

        String subject = "Reset Password";

        String content = "<p>Hello " + user.getUsername() + ",</p>"
                + "<p>Someone requested a password change for the account associated with this email: " + ((User) user).getEmail() + "</p>"
                + "<p>You can reset the password by clicking the link below:<br>"
                + "<a href=\"" + url + "\"> Reset Password</a></p>"
                + "<p>Or copy and paste the following URL into your browser:<br>"
                + url + "</p>"
                + "<p>This reset link is only valid for the next 30 minutes</p>"
                + "<p>If you did not request a password change you can ignore this email.</p>";

        helper.setSubject(subject);
        helper.setText(content, true);
        return message;
    }

    private SimpleMailMessage constructEmail(String subject, String body, UserDetails user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(((User) user).getEmail());
        email.setFrom("noreply@sevenwide.co");
        return email;
    }

}
