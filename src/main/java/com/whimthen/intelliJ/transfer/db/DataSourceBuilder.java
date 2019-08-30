package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.Dbms;
import com.intellij.database.autoconfig.DataSourceDetector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class DataSourceBuilder implements DataSourceDetector.Builder {
	@Override
	public DataSourceDetector.Builder commit() {
		return this;
	}

	@Override
	public DataSourceDetector.Builder reset() {
		return this;
	}

	@Override
	public DataSourceDetector.DriverBuilder driver(@NotNull Dbms dbms) {
		return null;
	}

	@Override
	public DataSourceDetector.Builder withCallback(@NotNull DataSourceDetector.Callback callback) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withName(@Nullable String name) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withComment(@Nullable String comment) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withGroupName(@Nullable String groupName) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withUrl(@Nullable String url) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withUser(@Nullable String user) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withPassword(@Nullable String password) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withDriverProperty(@Nullable String name, @Nullable String value) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withJdbcAdditionalProperty(@Nullable String name, @Nullable String value) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withDriverClass(@Nullable String name) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withDriver(@Nullable String name) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withVMOptions(@Nullable String options) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withVMEnv(@Nullable String name, @Nullable String value) {
		return this;
	}

	@Override
	public DataSourceDetector.Builder withOrigin(@Nullable Object origin) {
		return this;
	}
}
