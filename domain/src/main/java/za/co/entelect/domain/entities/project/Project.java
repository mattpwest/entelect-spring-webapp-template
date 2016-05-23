package za.co.entelect.domain.entities.project;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;
import za.co.entelect.domain.entities.history.HistoryEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
@EqualsAndHashCode(of = {
    "name"
}, callSuper = false)
@ToString(of = {
    "name"
})
public class Project extends IdentifiableEntity implements HistoryEntity {

    @Column(length = 200, unique = true, nullable = false)
    private String name;

    @Override
    public String getEntityType() {
        return getClass().getSimpleName();
    }

    @Override
    public String getEntityName() {
        return name;
    }
}
