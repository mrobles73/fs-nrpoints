package co.sevenwide.nrpointsapi.service;

public interface UserSecurityManager {

    public String validatePasswordResetToken(String token);

    public void deletePasswordResetToken(String token);
}
