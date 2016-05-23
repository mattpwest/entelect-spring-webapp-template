package za.co.entelect.domain.entities.history;

import za.co.entelect.domain.entities.Identifiable;

public interface HistoryEntity extends Identifiable<Long> {

    String getEntityType();
    String getEntityName();
}
