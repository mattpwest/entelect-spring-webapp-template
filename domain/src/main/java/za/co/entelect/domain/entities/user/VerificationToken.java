package za.co.entelect.domain.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;
import za.co.entelect.domain.utils.TokenGeneratorUtil;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(of = {"token", "expiresAt"}, callSuper = false)
@ToString(of = {"token", "expiresAt"})
public class VerificationToken extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "Token", nullable = false, unique = true)
    private String token;

    @Column(name = "ExpiresAt", nullable = false)
    private LocalDateTime expiresAt;

    @OneToOne(mappedBy = "verificationToken", fetch = FetchType.EAGER)
    private AppUser user;

    public static VerificationToken generate(int expiresInHours) {
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(expiresInHours);
        VerificationToken token = new VerificationToken();

        token.setExpiresAt(expiresAt);
        token.setToken(TokenGeneratorUtil.generateToken(expiresAt));

        return token;
    }
}
