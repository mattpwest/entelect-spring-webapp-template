package za.co.entelect.domain.entities.email;

import lombok.Data;
import za.co.entelect.domain.entities.IdentifiableEntity;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class Template extends IdentifiableEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Basic
    @Column(name = "Name", unique = false, nullable = false)
    private String name;

    @Basic
    @Column(name = "Subject", unique = false, nullable = false)
    private String subject;

    @Basic
    @Column(name = "Body", unique = false, length = 30000)
    private String body;
}
