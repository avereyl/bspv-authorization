/**
 * 
 */
package org.bspv.authorization.repository.jooq;

import static org.bspv.authorization.jooq.tables.Authorities.AUTHORITIES;
import static org.bspv.authorization.jooq.tables.Users.USERS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bspv.authorization.jooq.Keys;
import org.bspv.authorization.jooq.tables.records.AuthoritiesRecord;
import org.bspv.authorization.jooq.tables.records.UsersRecord;
import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.authorization.repository.UserRepository;
import org.bspv.authorization.repository.jooq.converter.RecordConverterFactory;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 */
@Repository
@Transactional
public class UserJooqRepository implements UserRepository {
    
    /**
     * Jooq DSL context.
     */
    private final DSLContext dslContext;

    /**
     * Constructor.
     * @param dslContext
     */
    public UserJooqRepository(DSLContext dslContext) {
        super();
        this.dslContext = dslContext;
    }

 

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#insert(org.bspv.authorization.model.User)
     */
    @Override
    public User insert(User user) {
        //inserting the user
        UsersRecord record = this.dslContext
            .insertInto(USERS)
            .columns(USERS.ID,
                    USERS.VERSION,
                    USERS.USERNAME,
                    USERS.PASSWORD,
                    USERS.EMAIL,
                    USERS.ENABLED)
            .values(user.getId(),
                    0L,
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isEnabled())
            .returning()
            .fetchOne();
        return RecordConverterFactory.convert(record);
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#insertWithSilentError(org.bspv.authorization.model.User)
     */
    @Override
    public User insertWithSilentError(User user) {
        User insertedUser = null;
        try {
            this.dslContext
            .insertInto(USERS)
            .columns(USERS.ID,
                    USERS.VERSION,
                    USERS.USERNAME,
                    USERS.PASSWORD,
                    USERS.EMAIL,
                    USERS.ENABLED)
            .values(user.getId(),
                    0L,
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isEnabled())
            .execute();
            
        } catch (DuplicateKeyException e) {
            this.dslContext
            .update(USERS)
            .set(USERS.VERSION, USERS.VERSION.add(1))
            .where(USERS.ID.eq(user.getId()))
            .execute();
        } finally {
            UsersRecord record = this.dslContext
            .selectFrom(USERS)
            .where(USERS.ID.eq(user.getId()))
            .fetchOne();
            insertedUser = (record == null) ? null : RecordConverterFactory.convert(record);
        }
        return insertedUser;
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#update(org.bspv.authorization.model.User)
     */
    @Override
    public User update(User user) {
        Result<UsersRecord> results = this.dslContext
                .update(USERS)
                .set(USERS.VERSION, USERS.VERSION.add(1))
                .set(USERS.USERNAME, user.getUsername())
                .set(USERS.EMAIL, user.getEmail())
                .where(USERS.ID.eq(user.getId()))
                .and(USERS.VERSION.eq(user.getVersion()))
                .returning()
                .fetch();

        if (results.size() != 1) {
            throw new OptimisticLockingFailureException(results.size() + " line(s) updated instead of one !");
        } else {
            return RecordConverterFactory.convert(results.get(0));
        }
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findAllUsers(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        Condition condition = USERS.ENABLED.eq(Boolean.TRUE);
        List<User> users = new ArrayList<>();
        Result<UsersRecord> usersRecord = this.dslContext
                .selectFrom(USERS)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Map<UUID,Result<AuthoritiesRecord>> authorithiesMap = usersRecord
                .fetchChildren(Keys.FK_AUTHORITIES__USERS)
                .sortAsc(AUTHORITIES.USER_ID)
                .sortAsc(AUTHORITIES.SERVICE)
                .intoGroups(AUTHORITIES.USER_ID);
        users.addAll(
                usersRecord.stream()
                .map(r -> {
                    Set<ServiceGrantedAuthority> authorities = new HashSet<>();
                    authorities.addAll(UserJooqRepository.buildAuthorithies(authorithiesMap.get(r.get(USERS.ID))));
                    return RecordConverterFactory.toBuilder(r).authorities(authorities).build().clearCredentials();
                })
                .collect(Collectors.toList()));
        
        int nbTotalElements = this.dslContext.fetchCount(USERS, condition);
        return new PageImpl<>(users, pageable, nbTotalElements);
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findAnyUsers(org.springframework.data.domain.Pageable)
     */
    @Override
    public Page<User> findAnyUsers(Pageable pageable) {
        List<User> users = new ArrayList<>();
        Result<UsersRecord> usersRecord = this.dslContext
                .selectFrom(USERS)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        Map<UUID,Result<AuthoritiesRecord>> authorithiesMap = usersRecord
                .fetchChildren(Keys.FK_AUTHORITIES__USERS)
                .sortAsc(AUTHORITIES.USER_ID)
                .sortAsc(AUTHORITIES.SERVICE)
                .intoGroups(AUTHORITIES.USER_ID);
        users.addAll(
                usersRecord.stream()
                .map(r -> {
                    Set<ServiceGrantedAuthority> authorities = new HashSet<>();
                    authorities.addAll(UserJooqRepository.buildAuthorithies(authorithiesMap.get(r.get(USERS.ID))));
                    return RecordConverterFactory.toBuilder(r).authorities(authorities).build().clearCredentials();
                })
                .collect(Collectors.toList()));
        
        int nbTotalElements = this.dslContext.fetchCount(USERS);
        return new PageImpl<>(users, pageable, nbTotalElements);
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findUserById(java.util.UUID)
     */
    @Override
    public User findUserById(UUID uuid) {
        Map<UsersRecord, Result<Record>> recordResultMap = 
                this.dslContext
                .select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(AUTHORITIES.USER_ID.eq(USERS.ID))
                .where(USERS.ID.eq(uuid))
                .and(USERS.ENABLED.eq(Boolean.TRUE))
                .fetch()
                .intoGroups(USERS);

        if (recordResultMap.size() != 1) {
            return null;
        } 
        Entry<UsersRecord, Result<Record>> entry = recordResultMap.entrySet().iterator().next();
        UsersRecord userRecord = entry.getKey();
        Set<ServiceGrantedAuthority> authorities = UserJooqRepository.buildAuthorithies(entry.getValue());
        return RecordConverterFactory.toBuilder(userRecord).authorities(authorities).build().clearCredentials();
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findAnyUserById(java.util.UUID)
     */
    @Override
    public User findAnyUserById(UUID uuid) {
        Map<UsersRecord, Result<Record>> recordResultMap = 
                this.dslContext
                .select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(AUTHORITIES.USER_ID.eq(USERS.ID))
                .where(USERS.ID.eq(uuid))
                .fetch()
                .intoGroups(USERS);

        if (recordResultMap.size() != 1) {
            return null;
        } 
        Entry<UsersRecord, Result<Record>> entry = recordResultMap.entrySet().iterator().next();
        UsersRecord userRecord = entry.getKey();
        Set<ServiceGrantedAuthority> authorities = UserJooqRepository.buildAuthorithies(entry.getValue());
        return RecordConverterFactory.toBuilder(userRecord).authorities(authorities).build().clearCredentials();
    }

    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findAnyUsersByIds(java.util.Set)
     */
    @Override
    public Set<User> findUsersByIds(Set<UUID> uuids) {
        Set<User> users = new HashSet<>();
        Map<UsersRecord, Result<Record>> recordResultMap = 
                this.dslContext
                .select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(AUTHORITIES.USER_ID.eq(USERS.ID))
                .where(USERS.ID.in(uuids))
                .and(USERS.ENABLED.eq(Boolean.TRUE))
                .fetch()
                .intoGroups(USERS);
        recordResultMap.entrySet().forEach(entry -> {
            users.add(RecordConverterFactory.toBuilder(entry.getKey())
                    .authorities(UserJooqRepository.buildAuthorithies(entry.getValue())).build());
        });
        return users;
    }
    /* (non-Javadoc)
     * @see org.bspv.authorization.repository.UserRepository#findAnyUsersByIds(java.util.Set)
     */
    @Override
    public Set<User> findAnyUsersByIds(Set<UUID> uuids) {
        Set<User> users = new HashSet<>();
        Map<UsersRecord, Result<Record>> recordResultMap = 
                this.dslContext
                .select()
                .from(USERS)
                .leftJoin(AUTHORITIES)
                .on(AUTHORITIES.USER_ID.eq(USERS.ID))
                .where(USERS.ID.in(uuids))
                .fetch()
                .intoGroups(USERS);
        recordResultMap.entrySet().forEach(entry -> {
            users.add(RecordConverterFactory.toBuilder(entry.getKey())
                    .authorities(UserJooqRepository.buildAuthorithies(entry.getValue())).build());
        });
        return users;
    }

    public static Set<ServiceGrantedAuthority> buildAuthorithies(Result<? extends Record> result) {
        return result.stream()
        .map(r -> {
            return ServiceGrantedAuthority.builder()
                    .grantedAuthority(new SimpleGrantedAuthority(r.get(AUTHORITIES.AUTHORITY)))
                    .service(r.get(AUTHORITIES.SERVICE))
                    .build();
        })
        .collect(Collectors.toSet());
    }
}
