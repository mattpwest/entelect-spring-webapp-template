package za.co.entelect.domain.entities.history;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(of = {"type", "role"})
@ToString(of = {"type", "role"})
public class HistoryTemplate extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "EventType", nullable = false)
    private HistoryEventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "EntityRole", nullable = false)
    private HistoryEntityRole role;

    @Basic
    @Column(name = "Template", nullable = false)
    private String template;
}
