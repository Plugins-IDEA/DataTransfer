package com.whimthen.intelliJ.transfer.cache;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

	public static DbDataSource get(@NotNull String dataSourceName) {
		return dataSourceCache.get(dataSourceName);
	}

	public static List<DbDataSource> getDataSources() {
		return new ArrayList<>(dataSourceCache.values());
	}

	public static boolean isEmpty() {
		return dataSourceCache.isEmpty();
	}

	public static DasNamespace getSchema(String schemaName) {
		return schemaCache.get(schemaName);
	}

	public static JBIterable<? extends DasNamespace> getSchemas(String dataSourceName) {
		return dataSourceSchemaCache.get(dataSourceName);
	}

	public static List<? extends DasTable> getTables(String schemaName) {
		return tableCache.get(schemaName);
	}

}
