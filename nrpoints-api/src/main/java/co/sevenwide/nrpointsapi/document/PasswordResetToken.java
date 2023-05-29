package co.sevenwide.nrpointsapi.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;
import java.util.Date;

@Document
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class PasswordResetToken {

    private static final int EXPIRATION = 30; //was 60*24 so a day, is time in minutes
    @Id
    @Generated
    private String id;
    @NonNull
    private String token;
    @NonNull
    private String userId;
    @NonNull
    private Date expiryDate;

    public PasswordResetToken(final String token, final String userId) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiration) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiration);
        return new Date(calendar.getTime().getTime());

    }

}
