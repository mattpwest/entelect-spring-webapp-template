package za.co.entelect.domain.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

@Data
@MappedSuperclass
public class IdentifiableEntity implements Identifiable<Long>, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(unique = true, nullable = false, insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Version
    @Column(columnDefinition = "bigint DEFAULT 0", nullable = false)
    private Long version;
}
