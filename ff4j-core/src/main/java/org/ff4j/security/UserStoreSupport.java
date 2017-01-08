package org.ff4j.security;

import java.util.Optional;
import java.util.stream.Stream;

import org.ff4j.audit.AuditTrail;
import org.ff4j.store.FF4jRepositoryListener;
import org.ff4j.store.FF4jRepositorySupport;

public abstract class UserStoreSupport extends FF4jRepositorySupport < FF4jUser , FF4jRepositoryListener< FF4jUser >> implements UserStore {

    /** serialVersionUID. */
    private static final long serialVersionUID = 2472380934533153376L;

    /** {@inheritDoc} */
    @Override
    public void delete(String entityId) {
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String id) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Stream<FF4jUser> findAll() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<FF4jUser> findById(String id) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void create(FF4jUser entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void update(FF4jUser entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void registerAuditListener(AuditTrail auditTrail) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void unRegisterAuditListener() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public FF4jUser getUserByUid(String userName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FF4jUser login(String userName, String password) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stream<FF4jUser> listUsers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Stream<FF4jUser> searchUsers(String query) {
        // TODO Auto-generated method stub
        return null;
    }
    

}
