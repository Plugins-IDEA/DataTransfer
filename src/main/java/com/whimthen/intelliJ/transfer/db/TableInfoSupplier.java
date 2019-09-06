package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.connection.DGDepartment;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DbImplUtil;
import com.intellij.database.util.GuardedRef;
import com.whimthen.intelliJ.transfer.model.DataLength;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public interface TableInfoSupplier {

	Map<String, Connection> connectionMap = new HashMap<>();

	DataLength getSizeFromTables(DbDataSource dataSource, List<? extends DasTable> tables) throws Exception;

	default Optional<Connection> getConnection(DbDataSource dataSource) throws Exception {
		if (connectionMap.containsKey(dataSource.getName())) {
			return Optional.of(connectionMap.get(dataSource.getName()));
		}
		GuardedRef<DatabaseConnection> connectionGuardedRef = DbImplUtil.getDatabaseConnection(dataSource, DGDepartment.TEXT_SEARCH);
		if (Objects.nonNull(connectionGuardedRef)) {
			DatabaseConnection databaseConnection = connectionGuardedRef.get();
			Connection         connection     = databaseConnection.getJdbcConnection();
			connectionMap.put(dataSource.getName(), connection);
			return Optional.of(connection);
		}
		return Optional.empty();
	}

}
