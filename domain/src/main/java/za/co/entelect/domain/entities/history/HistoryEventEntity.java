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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(of = {"event", "entityRole", "entityType", "entityId"}, callSuper = false)
@ToString(of = {"entityRole", "entityType", "entityId"})
public class HistoryEventEntity extends IdentifiableEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "HistoryEvent", referencedColumnName = "id", nullable = false)
    private HistoryEvent event;

    @Column(name = "EntityType", nullable = false)
    private String entityType;

    @Column(name = "EntityId", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "EntityRole", nullable = false)
    private HistoryEntityRole entityRole;
}
