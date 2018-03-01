package org.bspv.authorization.repository.jooq;

import static org.bspv.authorization.jooq.tables.Authorities.AUTHORITIES;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bspv.authorization.jooq.tables.records.AuthoritiesRecord;
import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.authorization.repository.ServiceGrantedAuthorityRepository;
import org.bspv.authorization.repository.jooq.converter.RecordConverterFactory;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStep3;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class ServiceGrantedAuthorityJooqRepository implements ServiceGrantedAuthorityRepository {

    /**
     * Jooq DSL context.
     */
    private final DSLContext dslContext;

    /**
     * Constructor.
     * @param dslContext
     */
    public ServiceGrantedAuthorityJooqRepository(DSLContext dslContext) {
        super();
        this.dslContext = dslContext;
    }
    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#findAuthorithies(org.bspv.authorization.model.User)
     */
    @Override
    public Set<ServiceGrantedAuthority> findAuthorithies(User user) {
        return this.dslContext
                .select()
                .from(AUTHORITIES)
                .where(AUTHORITIES.USER_ID.eq(user.getId()))
                .fetch()
                .into(AuthoritiesRecord.class)
                .stream()
                .map(RecordConverterFactory::convert)
                .collect(Collectors.toSet());
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#findAuthorithies(org.bspv.authorization.model.User, java.lang.String)
     */
    @Override
    public Set<ServiceGrantedAuthority> findAuthorithies(User user, String service) {
        return this.dslContext
                .select()
                .from(AUTHORITIES)
                .where(AUTHORITIES.USER_ID.eq(user.getId()))
                .and(AUTHORITIES.SERVICE.eq(service))
                .fetch()
                .into(AuthoritiesRecord.class)
                .stream()
                .map(RecordConverterFactory::convert)
                .collect(Collectors.toSet());
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#grantAuthorithies(org.bspv.authorization.model.User, java.lang.String, java.util.Set)
     */
    @Override
    public void grantAuthorithies(User user, String service, Set<String> authorities) {
        InsertValuesStep3<AuthoritiesRecord, UUID, String, String > insertStep = this.dslContext
                .insertInto(AUTHORITIES)
                .columns(AUTHORITIES.USER_ID, AUTHORITIES.SERVICE, AUTHORITIES.AUTHORITY);
        for (String authority : authorities) {
            insertStep.values(user.getId(), service, authority);
        }
        insertStep.onDuplicateKeyIgnore().execute();
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#revokeAllAuthorities(org.bspv.authorization.model.User)
     */
    @Override
    public void revokeAllAuthorities(User user) {
        this.dslContext.deleteFrom(AUTHORITIES).where(AUTHORITIES.USER_ID.eq(user.getId())).execute();
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#revokeAllAuthorities(org.bspv.authorization.model.User, java.lang.String)
     */
    @Override
    public void revokeAllAuthorities(User user, String service) {
        this.dslContext
        .deleteFrom(AUTHORITIES)
        .where(AUTHORITIES.USER_ID.eq(user.getId()))
        .and(AUTHORITIES.SERVICE.eq(service))
        .execute();
    }

    /*
     * (non-Javadoc)
     * @see org.bspv.authorization.repository.ServiceGrantedAuthorityRepository#revokeAuthorities(org.bspv.authorization.model.User, java.lang.String, java.util.Set)
     */
    @Override
    public void revokeAuthorities(User user, String service, Set<String> authorities) {
        this.dslContext
        .deleteFrom(AUTHORITIES)
        .where(AUTHORITIES.USER_ID.eq(user.getId()))
        .and(AUTHORITIES.SERVICE.eq(service))
        .and(AUTHORITIES.AUTHORITY.in(authorities))
        .execute();
    }

}
