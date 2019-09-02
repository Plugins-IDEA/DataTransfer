package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.Dbms;
import com.intellij.database.psi.DbDataSource;
import com.whimthen.intelliJ.transfer.db.mysql.MySqlTableInfoSupplier;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class OperatorFactory {

	public static TableInfoSupplier createTableInfoSupplier(DbDataSource dataSource) {
		TableInfoSupplier supplier = null;
		Dbms              dbms     = dataSource.getDbms();
		if (dbms.isMysql()) {
			supplier = new MySqlTableInfoSupplier();
		}
		return supplier;
	}

}
