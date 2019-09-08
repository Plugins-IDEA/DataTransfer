package com.whimthen.intelliJ.transfer.db.mysql;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.whimthen.intelliJ.transfer.db.DbOperator;
import com.whimthen.intelliJ.transfer.db.TableInfoSupplier;
import com.whimthen.intelliJ.transfer.model.DataLength;
import com.whimthen.intelliJ.transfer.model.SingleTableDataLength;
import com.whimthen.intelliJ.transfer.utils.LambdaExUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class MySqlTableInfoSupplier implements TableInfoSupplier {

	@Override
	public DataLength getSizeFromTables(DbDataSource dataSource, List<? extends DasTable> tables) throws Exception {
		if (Objects.isNull(tables) || tables.isEmpty()) {
			throw new UnsupportedOperationException("Table not selected for conversion!");
		}
		DataLength dataLength = new DataLength();
		getConnection(dataSource).ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			List<String> tableNames = tables.stream().map(DasTable::getName).collect(Collectors.toList());
			DbOperator   dbOperator = DbOperator.newInstance(connection);
			Map<String, Object> totalDataLengthMap = dbOperator.query("SELECT sum(DATA_LENGTH) totalDataLength from information_schema.TABLES WHERE TABLE_NAME IN (?)", tableNames);
			Map<String, SingleTableDataLength> tableDataLengthMap = dbOperator.query("SELECT TABLE_NAME tableName, AVG_ROW_LENGTH avgLength, DATA_LENGTH dataLength FROM information_schema.TABLES WHERE TABLE_NAME IN (?)",
				LambdaExUtil.rethrowBiFunction((resultSet, metaData) -> {
					Map<String, SingleTableDataLength> allTableDataLengthMap = new HashMap<>();
					int                                columnCount           = metaData.getColumnCount();
					while (resultSet.next()) {
						String tableName = null;
						SingleTableDataLength singleTableDataLength = new SingleTableDataLength();
						for (int i = 1; i <= columnCount; i++) {
							Object value = resultSet.getObject(i);
							BigDecimal length = null;
							if (i != 1) {
								length = Objects.nonNull(value) && StringUtils.isNotEmpty(value.toString())
									? new BigDecimal(value.toString()) : BigDecimal.ZERO;
							}
							if (i == 1) {
								tableName = value.toString();
							} else if (i == 2) {
								singleTableDataLength.setAvgLength(length);
							} else {
								singleTableDataLength.setDataLength(length);
							}
						}
						allTableDataLengthMap.put(tableName, singleTableDataLength);
					}
					return allTableDataLengthMap;
				}), tableNames);
			dataLength.setTotalLength(new BigDecimal(totalDataLengthMap.get("totalDataLength").toString()));
			dataLength.setTableLength(tableDataLengthMap);
		}));
		return dataLength;
	}

}
