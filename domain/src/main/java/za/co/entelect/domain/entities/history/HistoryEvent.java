package za.co.entelect.domain.entities.history;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(of = {"type", "timestamp"}, callSuper = false)
@ToString(of = {"type", "timestamp"})
public class HistoryEvent extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "EventType", nullable = false)
    private HistoryEventType type;

    @Column(name = "Timestamp", nullable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<HistoryEventEntity> entities = new HashSet<>(2);

    @Column(name = "StaticText")
    private String staticText;
}
