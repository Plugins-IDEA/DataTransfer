package com.whimthen.intelliJ.transfer.db.mysql;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.whimthen.intelliJ.transfer.db.DbOperator;
import com.whimthen.intelliJ.transfer.db.OperatorSupplier;
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
public class MySqlOperatorSupplier extends OperatorSupplier {

	private DbOperator dbOperator;
	private final String SELECT_TOTAL_DATA_LENGTH = "SELECT sum(DATA_LENGTH) totalDataLength from information_schema.TABLES WHERE TABLE_NAME IN (?)";
	private final String SELECT_TABLE_DATA_LENGTH = "SELECT TABLE_NAME tableName, AVG_ROW_LENGTH avgLength, DATA_LENGTH dataLength FROM information_schema.TABLES WHERE TABLE_NAME IN (?)";

	public MySqlOperatorSupplier(DbDataSource dataSource) throws Exception {
		super(dataSource);
		getConnection().ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			dbOperator = DbOperator.newInstance(connection);
		}));
	}

	@Override
	public DataLength getSizeFromTables(List<? extends DasTable> tables) throws Exception {
		if (Objects.isNull(tables) || tables.isEmpty()) {
			throw new UnsupportedOperationException("Table not selected for conversion!");
		}
		DataLength dataLength = new DataLength();
		getConnection().ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			List<String> tableNames = tables.stream().map(DasTable::getName).collect(Collectors.toList());
			Map<String, BigDecimal> totalDataLengthMap = dbOperator.query(SELECT_TOTAL_DATA_LENGTH, tableNames);
			Map<String, SingleTableDataLength> tableDataLengthMap = dbOperator.query(SELECT_TABLE_DATA_LENGTH,
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
			dataLength.setTotalLength(totalDataLengthMap.get("totalDataLength"));
			dataLength.setTableLength(tableDataLengthMap);
		}));
		return dataLength;
	}

	@Override
	public void createTable(String createSql) throws Exception {
		getConnection().ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			dbOperator.execute(createSql);
		}));
	}

	@Override
	public void selectDataBase(String dataBaseName) throws Exception {
		getConnection().ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
			dbOperator.execute("USE " + dataBaseName);
		}));
	}

}
