package co.sevenwide.nrpointsapi.service;

import co.sevenwide.nrpointsapi.document.PasswordResetToken;
import co.sevenwide.nrpointsapi.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserSecurityService implements UserSecurityManager {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> resultToken = passwordResetTokenRepository.findByToken(token);
        return resultToken.map(passwordResetToken -> isTokenExpired(passwordResetToken) ? "Expired" : "Valid").orElse("Invalid");
    }

    @Override
    public void deletePasswordResetToken(String token) {
        PasswordResetToken resultToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new NoSuchElementException("No reset token found under provided token"));
        passwordResetTokenRepository.delete(resultToken);
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken) {
        Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(calendar.getTime());
    }

}
