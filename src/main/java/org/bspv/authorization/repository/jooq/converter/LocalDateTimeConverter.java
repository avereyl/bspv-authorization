/**
 * 
 */
package org.bspv.authorization.repository.jooq.converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jooq.Converter;

/**
 * A {@link org.jooq.Converter} for {@link LocalDateTime} objects.
 */
public class LocalDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public LocalDateTime from(Timestamp databaseObject) {
		return databaseObject == null ? null : databaseObject.toLocalDateTime();
	}

	@Override
	public Timestamp to(LocalDateTime dt) {
		return dt == null ? null : Timestamp.from(dt.toInstant(ZoneOffset.ofHours(0)));
	}

	@Override
	public Class<Timestamp> fromType() {
		return Timestamp.class;
	}

	@Override
	public Class<LocalDateTime> toType() {
		return LocalDateTime.class;
	}
}