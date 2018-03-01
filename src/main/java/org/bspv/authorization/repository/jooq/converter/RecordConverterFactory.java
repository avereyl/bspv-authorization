/**
 * 
 */
package org.bspv.authorization.repository.jooq.converter;

import org.bspv.authorization.model.ServiceGrantedAuthority;
import org.bspv.authorization.model.User;
import org.bspv.authorization.jooq.tables.records.UsersRecord;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.bspv.authorization.jooq.tables.records.AuthoritiesRecord;
/**
 *
 */
public final class RecordConverterFactory {

	public static User convert(UsersRecord record) {
		return toBuilder(record).build();
	}
	
	public static User.Builder toBuilder(UsersRecord record) {
		return User
				.builder()
				.id(record.getId())
				.version(record.getVersion())
				.username(record.getUsername())
				.email(record.getEmail())
				.password(record.getPassword())
				.enable(record.getEnabled());
	}
	
	public static ServiceGrantedAuthority convert(AuthoritiesRecord record) {
	    return toBuilder(record).build();
	}
	
	public static ServiceGrantedAuthority.Builder toBuilder(AuthoritiesRecord record) {
	    return ServiceGrantedAuthority
	            .builder()
	            .service(record.getService())
	            .grantedAuthority(new SimpleGrantedAuthority(record.getAuthority()));
	}

	/**
	 * Private constructor. This class gathers only static method.
	 */
	private RecordConverterFactory() {
		super();
	}
}
