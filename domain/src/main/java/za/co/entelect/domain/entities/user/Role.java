package za.co.entelect.domain.entities.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import za.co.entelect.domain.entities.IdentifiableEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.io.Serializable;
import java.util.Collection;

@Data
@Entity
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@ToString(of = {"name"})
public class Role extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Description", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Collection<AppUser> users;
}
