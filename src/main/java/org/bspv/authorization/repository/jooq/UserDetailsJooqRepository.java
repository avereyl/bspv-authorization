package org.bspv.authorization.repository.jooq;

import static org.bspv.authorization.jooq.tables.Authorities.AUTHORITIES;
import static org.bspv.authorization.jooq.tables.Users.USERS;

import java.util.Map;
import java.util.Map.Entry;

import org.bspv.authorization.jooq.tables.records.UsersRecord;
import org.bspv.authorization.repository.UserDetailsRepository;
import org.bspv.authorization.repository.jooq.converter.RecordConverterFactory;
import org.bspv.security.model.User;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserDetailsJooqRepository implements UserDetailsRepository {

    /**
     * Jooq DSL context.
     */
    private final DSLContext dslContext;

    /**
     * Constructor.
     * @param dslContext
     */
    public UserDetailsJooqRepository(DSLContext dslContext) {
        super();
        this.dslContext = dslContext;
    }
    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#loadByUsername(java.lang.String)
     */
    @Override
    public User loadByUsername(String username) throws UsernameNotFoundException {
        User user = null;
//      @formatter:off
        Map<UsersRecord, Result<Record>> recordResultMap = 
        this.dslContext
            .select()
            .from(USERS)
            .leftJoin(AUTHORITIES)
            .on(AUTHORITIES.USER_ID.eq(USERS.ID))
            .where(USERS.USERNAME.eq(username))
            .and(USERS.ENABLED.eq(Boolean.TRUE))
            .fetch()
            .intoGroups(USERS);
//      @formatter:on
        if (recordResultMap.size() != 1) {
            throw new UsernameNotFoundException("User not found by its username.");
        } else {
            Entry<UsersRecord, Result<Record>> entry = recordResultMap.entrySet().iterator().next();
            UsersRecord userRecord = entry.getKey();
            user = RecordConverterFactory.toBuilder(userRecord)
                    .authorities(UserJooqRepository.buildAuthorithies(entry.getValue())).build();
        }
        return user;
    }
    
    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.UserDetailsRepository#changePassword(java.lang.String, java.lang.String)
     */
    @Override
    public void changePassword(String username, String newPasword) {
        this.dslContext.update(USERS).set(USERS.PASSWORD, newPasword).where(USERS.USERNAME.eq(username)).execute();
    }
    
    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.UserDetailsRepository#enableUser(java.lang.String)
     */
    @Override
    public void enableUser(String username) {
        this.dslContext.update(USERS).set(USERS.ENABLED, true).where(USERS.USERNAME.eq(username)).execute();
        
    }
    
    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.UserDetailsRepository#disableUser(java.lang.String)
     */
    @Override
    public void disableUser(String username) {
        this.dslContext.update(USERS).set(USERS.ENABLED, false).where(USERS.USERNAME.eq(username)).execute();
    }
    
}
