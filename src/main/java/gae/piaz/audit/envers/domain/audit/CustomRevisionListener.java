package gae.piaz.audit.envers.domain.audit;

import org.hibernate.envers.RevisionListener;

import java.util.UUID;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        // use security context to get the current user
        customRevisionEntity.setAuthorUuid(UUID.randomUUID());
    }
}
