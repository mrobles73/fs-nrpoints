package co.sevenwide.nrpointsapi.controller;

import co.sevenwide.nrpointsapi.document.User;
import co.sevenwide.nrpointsapi.dto.UserDTO;
import co.sevenwide.nrpointsapi.repository.UserRepository;
import co.sevenwide.nrpointsapi.service.UserManager;
import co.sevenwide.nrpointsapi.util.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserDetailsManager userDetailsManager;

    /*
    @GetMapping("/{id}")
    @PreAuthorize("#user.id == #id")
    public ResponseEntity userByID(@AuthenticationPrincipal User user, @PathVariable String id) {
        return ResponseEntity.ok(UserDTO.from(userRepository.findById(id).orElseThrow()));
    }
     */

    @GetMapping("/user")
    public ResponseEntity<UserDTO> user(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(UserDTO.from(userRepository.findById(user.getId()).orElseThrow()));
    }

    @PostMapping("/updateUsername")
    public ResponseEntity<UserDTO> updateUsername(@AuthenticationPrincipal User userAuth, @RequestBody UserDTO updatedUser) {
        if(updatedUser.getId().equals(userAuth.getId())) {
            UserDetails user = userDetailsManager.loadUserByUsername(updatedUser.getEmail());
            ((User) user).setUsername(updatedUser.getUsername());
            userDetailsManager.updateUser(user);
            return ResponseEntity.ok(UserDTO.from((User) user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/updateAvatar")
    public ResponseEntity<UserDTO> updateAvatar(@AuthenticationPrincipal User userAuth, @RequestBody UserDTO updatedUser) {
        if(updatedUser.getId().equals(userAuth.getId())) {
            UserDetails user = ((UserManager) userDetailsManager).loadUserById(updatedUser.getId());
            ((User) user).setAvatar(updatedUser.getAvatar());
            userDetailsManager.updateUser(user);
            return ResponseEntity.ok(UserDTO.from((User) user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
