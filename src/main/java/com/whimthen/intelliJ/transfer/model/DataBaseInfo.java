package com.whimthen.intelliJ.transfer.model;

import com.intellij.database.model.DasTable;

import java.util.List;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class DataBaseInfo {

	private String host;
	private String port;
	private String user;
	private String password;
	private String database;

	private List<? extends DasTable> tableList;

	public List<? extends DasTable> getTableList() {
		return tableList;
	}

	public void setTableList(List<? extends DasTable> tableList) {
		this.tableList = tableList;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

}
