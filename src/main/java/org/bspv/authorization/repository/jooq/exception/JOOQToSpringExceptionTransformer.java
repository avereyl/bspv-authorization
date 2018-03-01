/**
 * 
 */
package org.bspv.authorization.repository.jooq.exception;

import org.jooq.ExecuteContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.stereotype.Component;


/**
 * This class wraps the Jooq exceptions so that we keep a consistent spring exception handling behaviour.
 */
@Component
public class JOOQToSpringExceptionTransformer extends DefaultExecuteListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void exception(ExecuteContext ctx) {
		SQLDialect dialect = ctx.configuration().dialect();
		SQLExceptionTranslator translator = (dialect != null) ? new SQLErrorCodeSQLExceptionTranslator(dialect.name())
				: new SQLStateSQLExceptionTranslator();

		ctx.exception(translator.translate("jOOQ", ctx.sql(), ctx.sqlException()));
	}
}
