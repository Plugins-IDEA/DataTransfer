package com.whimthen.intelliJ.transfer.db;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class DbOperator {

	private Pattern  inCompile            = Pattern.compile("[i|I][n|N]\\s*\\((\\?,*\\s*)+\\)");
	private Pattern  questionMarkCompile  = Pattern.compile("\\?");
	private Pattern  leftBracketsCompile  = Pattern.compile("\\(");
	private Pattern  rightBracketsCompile = Pattern.compile("\\)");
	private String[] noParameterSql       = new String[]{"CREATE", "USE", "ALTER", "DROP", "TRUNCATE"};

	private Connection connection;

	public int execute(String sql) throws SQLException {
		return preparedParams(sql).executeUpdate();
	}

	public int execute(String sql, Object... params) throws SQLException {
		return preparedParams(sql, params).executeUpdate();
	}

	public <T> T query(String querySql, BiFunction<ResultSet, ResultSetMetaData, T> function, Object... params) throws Exception {
		PreparedStatement statement = preparedParams(querySql, params);
		ResultSetMetaData metaData  = statement.getMetaData();
		ResultSet         resultSet = statement.executeQuery();
		return function.apply(resultSet, metaData);
	}

	public <T> T query(String querySql, BiFunction<ResultSet, ResultSetMetaData, T> function) throws Exception {
		PreparedStatement statement = connection.prepareStatement(querySql);
		ResultSetMetaData metaData  = statement.getMetaData();
		ResultSet         resultSet = statement.executeQuery();
		return function.apply(resultSet, metaData);
	}

	public <T> Map<String, T> query(String querySql, Object... params) throws Exception {
		PreparedStatement statement = preparedParams(querySql, params);
		return resultHandler(statement);
	}

	public Map<String, Object> query(String querySql) throws Exception {
		PreparedStatement statement = connection.prepareStatement(querySql);
		return resultHandler(statement);
	}

	public List<Map<String, Object>> queryList(String querySql, Object... params) throws Exception {
		PreparedStatement statement = preparedParams(querySql, params);
		return resultListHandler(statement);
	}

	public List<Map<String, Object>> queryList(String querySql) throws Exception {
		PreparedStatement statement = connection.prepareStatement(querySql);
		return resultListHandler(statement);
	}

	private PreparedStatement preparedParams(String sql, Object... params) throws SQLException {
		sql = sql.trim();
		Matcher              matcher  = inCompile.matcher(sql);
		Map<Integer, String> indexMap = new HashMap<>();
		if (matcher.find()) {
			String group = matcher.group();
			group = group.replaceAll("(\\?,*\\s*)+", "?");
			sql = matcher.replaceAll(group);
			int i = group.indexOf("?");

			int addCount           = 0;
			int singleInParamCount = 0;
			while (sql.contains(group)) {
				int index   = sql.indexOf(group) + i - addCount;
				int inIndex = getInIndex(sql, index);

				if (params.length >= inIndex) {
					Object param       = params[inIndex];
					int    preAddCount = singleInParamCount;
					int    tempInIndex = inIndex;
					inIndex = inIndex + singleInParamCount;
					if (param instanceof Collection) {
						singleInParamCount = ((Collection) param).size();
					} else if (param.getClass().isArray()) {
						singleInParamCount = ((Object[]) param).length;
					}
					indexMap.put(tempInIndex, inIndex + "-" + (singleInParamCount + (--inIndex)));
					StringBuilder newInReplacement = new StringBuilder();
					for (int j = 0; j < singleInParamCount; j++) {
						newInReplacement.append("?, ");
					}
					if (newInReplacement.length() > 0) {
						int lastIndexOf = newInReplacement.lastIndexOf(", ");
						newInReplacement.delete(lastIndexOf, lastIndexOf + 2);
					}

					addCount += newInReplacement.length() - 1;
					String newIn = questionMarkCompile.matcher(group).replaceAll(newInReplacement.toString());
					//group.replace("?", newInReplacement);
					String singleInString = questionMarkCompile.matcher(group).replaceAll("\\\\?");
					singleInString = leftBracketsCompile.matcher(singleInString).replaceAll("\\\\(");
					singleInString = rightBracketsCompile.matcher(singleInString).replaceAll("\\\\)");
					sql = sql.replaceFirst(singleInString, newIn);
					singleInParamCount += preAddCount - 1;
				} else {
					throw new SQLSyntaxErrorException("Parameter count not match!");
				}
			}
		}

		PreparedStatement statement = connection.prepareStatement(sql);
		if (isNotStartWithDDL(sql)) {
			ParameterMetaData parameterMetaData = statement.getParameterMetaData();
			int               parameterCount    = parameterMetaData.getParameterCount();

			List<Integer> allInIndex = getAllInIndex(indexMap);
			for (int i = 1, j = 0; i < parameterCount; i++) {
				int paramIndex = i - 1;
				// 说明是 in 参数
				if (allInIndex.contains(paramIndex)) {
					Object param = params[j];
					if (param instanceof Collection) {
						int lastIndex = 0;
						for (Object next : (Collection) param) {
							statement.setObject(i, next);
							if (lastIndex != ((Collection) param).size() - 1) {
								i++;
							}
							lastIndex++;
						}
					} else if (param.getClass().isArray()) {
						Object[] ps = (Object[]) param;
						for (int i1 = 0; i1 < ps.length; i1++) {
							statement.setObject(i, ps[i1]);
							if (i1 != ps.length - 1) {
								i++;
							}
						}
					}
					j++;
					continue;
				}
				statement.setObject(i, params[j]);
				j++;
			}
		}
		return statement;
	}

	private boolean isNotStartWithDDL(String sql) {
		for (String noP : noParameterSql) {
			if (sql.startsWith(noP)) {
				return false;
			}
		}
		return true;
	}

	private List<Integer> getAllInIndex(Map<Integer, String> indexMap) {
		List<Integer> indexes = new ArrayList<>();
		indexMap.values().forEach(value -> {
			String[] split = value.split("-");
			int      start = Integer.parseInt(split[0]);
			int      end   = Integer.parseInt(split[1]);
			for (int j = start; j <= end; j++) {
				indexes.add(j);
			}
		});
		return indexes;
	}

	private int getInIndex(String sql, int in) {
		sql = inCompile.matcher(sql).replaceAll("in (?)");
		int    inIndex = 0;
		char[] chars   = sql.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '?') {
				if (i == in) {
					break;
				}
				inIndex++;
			}
		}
		return inIndex;
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

	private <T> Map<String, T> resultHandler(PreparedStatement statement) throws SQLException {
		Map<String, T>    resultMap   = new HashMap<>();
		ResultSet         resultSet   = statement.executeQuery();
		ResultSetMetaData metaData    = statement.getMetaData();
		int               columnCount = metaData.getColumnCount();
		if (resultSet.next()) {
			for (int i = 1; i <= columnCount; i++) {
				String key   = metaData.getColumnLabel(i);
				Object value = resultSet.getObject(i);
				resultMap.put(key, (T) value);
			}
		}
		return resultMap;
	}

	private List<Map<String, Object>> resultListHandler(PreparedStatement statement) throws Exception {
		List<Map<String, Object>> result      = new ArrayList<>();
		ResultSetMetaData         metaData    = statement.getMetaData();
		int                       columnCount = metaData.getColumnCount();
		ResultSet                 resultSet   = statement.executeQuery();
		while (resultSet.next()) {
			Map<String, Object> map = new HashMap<>();
			for (int i = 1; i <= columnCount; i++) {
				String key   = metaData.getColumnLabel(i);
				Object value = resultSet.getObject(i);
				map.put(key, value);
			}
			result.add(map);
		}
		return result;
	}

	private DbOperator(Connection connection) throws SQLException {
		this.connection = connection;
		setAutoCommit();
	}

	public static DbOperator newInstance(Connection connection) throws SQLException {
		return new DbOperator(connection);
	}

}
