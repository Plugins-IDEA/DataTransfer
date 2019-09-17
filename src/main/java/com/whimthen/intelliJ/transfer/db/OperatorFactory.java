package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.Dbms;
import com.intellij.database.psi.DbDataSource;
import com.whimthen.intelliJ.transfer.db.mysql.MySqlOperatorSupplier;
import com.whimthen.intelliJ.transfer.db.mysql.MySqlTransfer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class OperatorFactory {

	public static OperatorSupplier createSupplier(DbDataSource dataSource) throws Exception {
		OperatorSupplier supplier = null;
		Dbms             dbms     = dataSource.getDbms();
		if (dbms.isMysql()) {
			supplier = new MySqlOperatorSupplier(dataSource);
		}
		return supplier;
	}

	public static TransferOperator createTransfer(@Nullable Dbms dbms) {
		if (Objects.nonNull(dbms)) {
			if (dbms.isMysql())
				return new MySqlTransfer();
		}
		return new MySqlTransfer();
	}

}
