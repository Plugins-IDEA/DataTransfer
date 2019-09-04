package com.whimthen.intelliJ.transfer.db.mysql;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.whimthen.intelliJ.transfer.db.DbOperator;
import com.whimthen.intelliJ.transfer.db.TableInfoSupplier;
import com.whimthen.intelliJ.transfer.model.DataLength;
import com.whimthen.intelliJ.transfer.utils.LambdaExUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class MySqlTableInfoSupplier implements TableInfoSupplier {

	@Override
	public DataLength getSizeFromTables(DbDataSource dataSource, List<? extends DasTable> tables) throws Exception {
		DataLength dataLength = new DataLength();
		getConnection(dataSource).ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			if (connection.isClosed()) {
				return;
			}
			Map<String, Long> tableLength = new HashMap<>();
			BiFunction<ResultSet, ResultSetMetaData, DataLength> function = LambdaExUtil.rethrowBiFunction((resultSet, metaData) -> {
				DataLength dataLength1 = new DataLength();
				int               columnCount = metaData.getColumnCount();
				while (resultSet.next()) {
//					for (int i = 1; i <= columnCount; i++) {
//						String key = metaData.getColumnLabel(i);
//						Object value = resultSet.getObject(i);
//						if (Objects.nonNull(value)) {
//							tableLength.put(key, Long.parseLong(value.toString()));
//						}
//					}

					String key = metaData.getColumnLabel(3);
					Object value = resultSet.getObject(3);
					if (Objects.nonNull(value)) {
						tableLength.put(key, Long.parseLong(value.toString()));
					}
				}
				dataLength1.setTableLength(tableLength);
				return dataLength1;
			});
			List<String> tableNames = tables.stream().map(DasTable::getName).collect(Collectors.toList());
			DataLength query = (DataLength) DbOperator.newInstance(connection).query(
				"SELECT TABLE_NAME tableName, AVG_ROW_LENGTH avgLength, DATA_LENGTH dataLength FROM information_schema.TABLES WHERE TABLE_NAME IN ?", function, tableNames);
			System.out.println(query);
		}));
		return dataLength;
	}

}
