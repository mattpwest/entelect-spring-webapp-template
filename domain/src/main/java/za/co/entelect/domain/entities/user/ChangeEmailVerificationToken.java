package za.co.entelect.domain.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;
import za.co.entelect.domain.utils.TokenGeneratorUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(of = {"token", "expiresAt"}, callSuper = false)
@ToString(of = {"token", "expiresAt", "oldEmail", "newEmail"})
public class ChangeEmailVerificationToken extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "Token", nullable = false, unique = true)
    private String token;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "Used", nullable = false)
    private boolean used;

    @Column(name = "OldEmail", nullable = false)
    private String oldEmail;

    @Column(name = "NewEmail", nullable = false)
    private String newEmail;

    @ManyToOne
    @JoinColumn(name = "AppUser", referencedColumnName = "id", nullable = false)
    private AppUser user;

    public static ChangeEmailVerificationToken generate(int expiresInHours) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(expiresInHours);
        ChangeEmailVerificationToken token = new ChangeEmailVerificationToken();

        token.setExpiresAt(expiresAt);
        token.setUsed(false);
        token.setToken(TokenGeneratorUtil.generateToken(expiresAt));

        return token;
    }
}
