package com.whimthen.intelliJ.transfer.cache;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class DataSourceCache {

	private static ConcurrentHashMap<String, DbDataSource> dataSourceCache = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, DasNamespace> schemaCache = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, JBIterable<? extends DasNamespace>> dataSourceSchemaCache = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, List<? extends DasTable>> tableCache = new ConcurrentHashMap<>();

	/**
	 * 添加数据源缓存
	 * dataSourceName -> DataSource
	 * dataSourceName -> Schema
	 * schemaName -> Tables
	 *
	 * @param dataSource 数据源
	 */
	public static void add(@NotNull DbDataSource dataSource) {
		JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dataSource);
		schemas.forEach(schema -> {
			schemaCache.put(schema.getName(), schema);
		});
		dataSourceCache.put(dataSource.getName(), dataSource);
		dataSourceSchemaCache.put(dataSource.getName(), schemas);
		Map<String, ? extends List<? extends DasTable>> tableMaps =
			DasUtil.getTables(dataSource).toList().stream()
				   .collect(Collectors.groupingBy(tn -> Objects.requireNonNull(tn.getDasParent()).getName()));
		if (Objects.nonNull(tableMaps) && !tableMaps.isEmpty()) {
			tableCache.putAll(tableMaps);
		}
	}

	/**
	 * 获取数据源, 根据保存的名称
	 *
	 * @param dataSourceName 数据源名称
	 * @return 数据源
	 */
	public static DbDataSource get(@NotNull String dataSourceName) {
		return dataSourceCache.get(dataSourceName);
	}

	/**
	 * 获取所有的数据源
	 *
	 * @return 数据源
	 */
	public static List<DbDataSource> getDataSources() {
		return new ArrayList<>(dataSourceCache.values());
	}

	/**
	 * 数据源是否为空
	 *
	 * @return true | false
	 */
	public static boolean isEmpty() {
		return dataSourceCache.isEmpty();
	}

	/**
	 * 获取Schema, 根据SchemaName
	 *
	 * @param schemaName schema名称
	 * @return Schema
	 */
	public static DasNamespace getSchema(String schemaName) {
		return schemaCache.get(schemaName);
	}

	/**
	 * 获取所有的Schema
	 *
	 * @param dataSourceName 数据源名称
	 * @return Schema
	 */
	public static JBIterable<? extends DasNamespace> getSchemas(String dataSourceName) {
		return dataSourceSchemaCache.get(dataSourceName);
	}

	/**
	 * 获取Table, 根据Schema名称
	 *
	 * @param schemaName Schema名称
	 * @return Tables
	 */
	public static List<? extends DasTable> getTables(String schemaName) {
		return tableCache.get(schemaName);
	}

	/**
	 * 获取Table, 根据DataSource名称
	 *
	 * @param dataSourceName DataSource名称
	 * @return Tables
	 */
	public static Optional<List<? extends DasTable>> getTablesByDataSourceName(String dataSourceName) {
		List<DasTable> tableList = new ArrayList<>();
		getSchemas(dataSourceName).forEach(schema -> {
			GlobalUtil.nonNullConsumer(getTables(schema.getName()),
				scs -> tableList.addAll(new ArrayList<DasTable>(scs)));
		});
		return Optional.of(tableList);
	}

}
