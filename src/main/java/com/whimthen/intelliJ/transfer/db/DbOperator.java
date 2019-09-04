package com.whimthen.intelliJ.transfer.db;

import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.LambdaExUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbOperator<T> {

	private Connection connection;

	public <T> T query(String querySql, BiFunction<ResultSet, ResultSetMetaData, T> function, Object... params) throws Exception {
		PreparedStatement statement   = connection.prepareStatement(querySql);
		AtomicInteger index = new AtomicInteger(1);
		Stream.of(params).forEach(LambdaExUtil.rethrowConsumer(param -> {
			if (param instanceof List) {
				statement.setArray(index.getAndIncrement(), connection.createArrayOf("text", ((List) param).toArray()));
			} else {
				statement.setObject(index.getAndIncrement(), param);
			}
		}));
		ResultSetMetaData metaData = statement.getMetaData();
		ResultSet resultSet = statement.executeQuery();
		return function.apply(resultSet, metaData);
	}

	public T query(String querySql, Class<T> target, Object... params) throws Exception {
		PreparedStatement statement   = connection.prepareStatement(querySql);
		AtomicInteger index = new AtomicInteger(1);
		Stream.of(params).forEach(LambdaExUtil.rethrowConsumer(param -> {
			statement.setObject(index.getAndIncrement(), param);
		}));
		return resultHandler(statement, target);
	}

	public T query(String querySql, Class<T> target) throws Exception {
		PreparedStatement statement   = connection.prepareStatement(querySql);
		return resultHandler(statement, target);
	}

	public Map<String, Object> query(String querySql) throws Exception {
		PreparedStatement statement   = connection.prepareStatement(querySql);
		T                 t           = resultHandler(statement, (Class<T>) HashMap.class);
		return (Map<String, Object>) t;
	}

	public DbOperator setAutoCommit() throws SQLException {
		return setAutoCommit(true);
	}

	public DbOperator setAutoCommit(boolean isAutoCommit) throws SQLException {
		connection.setAutoCommit(isAutoCommit);
		return this;
	}

	public DbOperator commit() throws SQLException {
		connection.commit();
		return this;
	}

	private T resultHandler(PreparedStatement statement, Class<T> target) throws Exception {
		T instance = target.newInstance();
		ResultSetMetaData metaData    = statement.getMetaData();
		int               columnCount = metaData.getColumnCount();
		ResultSet         resultSet   = statement.executeQuery();
		while (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String key = metaData.getColumnLabel(i);
				Object value = resultSet.getObject(i);
				if (instance instanceof Map) {
					((Map) instance).put(key, value);
				} else {
					GlobalUtil.setFieldValue(instance, key, value);
				}
			}
		}
		return instance;
	}

	private DbOperator(Connection connection) {
		this.connection = connection;
	}

	public static DbOperator newInstance(Connection connection) {
		return new DbOperator(connection);
	}

}
