package za.co.entelect.domain.entities.email;

import lombok.Data;
import za.co.entelect.domain.entities.IdentifiableEntity;
import za.co.entelect.domain.entities.user.AppUser;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
public class Email extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public Email() {
        this.priority = Priority.NORMAL;
    }

    public Email(Priority priority) {
        this.priority = priority;
    }

    @Column(name = "Subject", unique = false, nullable = false)
    private String subject;

    @Column(name = "Template", unique = false, nullable = false)
    private String template;

    @Column(name = "DataJson", unique = false, nullable = false)
    private String dataJson;

    @Column(name = "SentAt", unique = false, nullable = true)
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AppUser", referencedColumnName = "id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "Priority", nullable = true)
    private Priority priority;

    public enum Priority {
        HIGH,
        NORMAL,
        LOW
    }
}
