package za.co.entelect.domain.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Formula;
import za.co.entelect.domain.entities.history.HistoryEntity;
import za.co.entelect.domain.entities.IdentifiableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(of = {
    "email",
    "firstName",
    "lastName"
}, callSuper = false)
@ToString(of = {
    "email",
    "firstName",
    "lastName",
    "verified",
    "enabled"
})
public class AppUser extends IdentifiableEntity implements Serializable, HistoryEntity {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Boolean changeEmailEnabled = false;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "verificationToken", referencedColumnName = "id", nullable = true)
    private VerificationToken verificationToken;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "passwordResetToken", referencedColumnName = "id", nullable = true)
    private PasswordResetToken passwordResetToken;

    @ManyToMany
    @JoinTable(name = "AppUserRole",
        joinColumns = {@JoinColumn(name = "AppUser", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "Role", referencedColumnName = "id")})
    private Set<Role> roles = new LinkedHashSet<>();

    @Formula(" concat(firstName, ' ', lastName)")
    private String entityName;

    @Override
    public String getEntityType() {
        return getClass().getSimpleName();
    }
}
