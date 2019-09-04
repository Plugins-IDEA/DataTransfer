package com.whimthen.intelliJ.transfer.model;

import com.intellij.database.model.DasTable;

import java.util.List;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class TransferModel {

	private StartType type;

	// Select Connection
	private String sourceConn;
	private String targetConn;
	private List<? extends DasTable> tables;

	// Select & Options common
	private String sourceDb;
	private String targetDb;

	// Options Connection
	private String sourceHost;
	private String sourcePort;
	private String sourceUser;
	private String sourcePwd;
	private String targetHost;
	private String targetPort;
	private String targetUser;
	private String targetPwd;

	// Table Options
	private boolean createTables;
	private boolean includeIndexes;
	private boolean includeForeignKeyConstraints;
	private boolean includeEngineTableType;
	private boolean includeCharacterSet;
	private boolean includeAutoIncrement;
	private boolean includeOtherTableOptions;
	private boolean includeTriggers;

	// Record Options
	private boolean insertRecords;
	private boolean lockTargetTables;
	private boolean useTransaction;
	private boolean useCompleteInsertStatements;
	private boolean useExtendedInsertStatements;
	private boolean useDelayedInsertStatements;
	private boolean useBLOB;

	// Convert object name to
	private boolean lowerCase;
	private boolean upperCase;

	// Other Options
	private boolean continueOnError;
	private boolean lockSourceTables;
	private boolean createTargetDatabaseIfNotExist;
	private boolean useDDLFromShowCreateTable;
	private boolean useSingleTransaction;
	private boolean dropTargetObjectsBeforeCreate;

	public StartType getType() {
		return type;
	}

	public void setType(StartType type) {
		this.type = type;
	}

	public String getSourceConn() {
		return sourceConn;
	}

	public void setSourceConn(String sourceConn) {
		this.sourceConn = sourceConn;
	}

	public String getSourceDb() {
		return sourceDb;
	}

	public void setSourceDb(String sourceDb) {
		this.sourceDb = sourceDb;
	}

	public String getTargetConn() {
		return targetConn;
	}

	public void setTargetConn(String targetConn) {
		this.targetConn = targetConn;
	}

	public String getTargetDb() {
		return targetDb;
	}

	public void setTargetDb(String targetDb) {
		this.targetDb = targetDb;
	}

	public List<? extends DasTable> getTables() {
		return tables;
	}

	public void setTables(List<? extends DasTable> tables) {
		this.tables = tables;
	}

	public String getSourceHost() {
		return sourceHost;
	}

	public void setSourceHost(String sourceHost) {
		this.sourceHost = sourceHost;
	}

	public String getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}

	public String getSourceUser() {
		return sourceUser;
	}

	public void setSourceUser(String sourceUser) {
		this.sourceUser = sourceUser;
	}

	public String getSourcePwd() {
		return sourcePwd;
	}

	public void setSourcePwd(String sourcePwd) {
		this.sourcePwd = sourcePwd;
	}

	public String getTargetHost() {
		return targetHost;
	}

	public void setTargetHost(String targetHost) {
		this.targetHost = targetHost;
	}

	public String getTargetPort() {
		return targetPort;
	}

	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}

	public String getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(String targetUser) {
		this.targetUser = targetUser;
	}

	public String getTargetPwd() {
		return targetPwd;
	}

	public void setTargetPwd(String targetPwd) {
		this.targetPwd = targetPwd;
	}

	public boolean isCreateTables() {
		return createTables;
	}

	public void setCreateTables(boolean createTables) {
		this.createTables = createTables;
	}

	public boolean isIncludeIndexes() {
		return includeIndexes;
	}

	public void setIncludeIndexes(boolean includeIndexes) {
		this.includeIndexes = includeIndexes;
	}

	public boolean isIncludeForeignKeyConstraints() {
		return includeForeignKeyConstraints;
	}

	public void setIncludeForeignKeyConstraints(boolean includeForeignKeyConstraints) {
		this.includeForeignKeyConstraints = includeForeignKeyConstraints;
	}

	public boolean isIncludeEngineTableType() {
		return includeEngineTableType;
	}

	public void setIncludeEngineTableType(boolean includeEngineTableType) {
		this.includeEngineTableType = includeEngineTableType;
	}

	public boolean isIncludeCharacterSet() {
		return includeCharacterSet;
	}

	public void setIncludeCharacterSet(boolean includeCharacterSet) {
		this.includeCharacterSet = includeCharacterSet;
	}

	public boolean isIncludeAutoIncrement() {
		return includeAutoIncrement;
	}

	public void setIncludeAutoIncrement(boolean includeAutoIncrement) {
		this.includeAutoIncrement = includeAutoIncrement;
	}

	public boolean isIncludeOtherTableOptions() {
		return includeOtherTableOptions;
	}

	public void setIncludeOtherTableOptions(boolean includeOtherTableOptions) {
		this.includeOtherTableOptions = includeOtherTableOptions;
	}

	public boolean isIncludeTriggers() {
		return includeTriggers;
	}

	public void setIncludeTriggers(boolean includeTriggers) {
		this.includeTriggers = includeTriggers;
	}

	public boolean isInsertRecords() {
		return insertRecords;
	}

	public void setInsertRecords(boolean insertRecords) {
		this.insertRecords = insertRecords;
	}

	public boolean isLockTargetTables() {
		return lockTargetTables;
	}

	public void setLockTargetTables(boolean lockTargetTables) {
		this.lockTargetTables = lockTargetTables;
	}

	public boolean isUseTransaction() {
		return useTransaction;
	}

	public void setUseTransaction(boolean useTransaction) {
		this.useTransaction = useTransaction;
	}

	public boolean isUseCompleteInsertStatements() {
		return useCompleteInsertStatements;
	}

	public void setUseCompleteInsertStatements(boolean useCompleteInsertStatements) {
		this.useCompleteInsertStatements = useCompleteInsertStatements;
	}

	public boolean isUseExtendedInsertStatements() {
		return useExtendedInsertStatements;
	}

	public void setUseExtendedInsertStatements(boolean useExtendedInsertStatements) {
		this.useExtendedInsertStatements = useExtendedInsertStatements;
	}

	public boolean isUseDelayedInsertStatements() {
		return useDelayedInsertStatements;
	}

	public void setUseDelayedInsertStatements(boolean useDelayedInsertStatements) {
		this.useDelayedInsertStatements = useDelayedInsertStatements;
	}

	public boolean isUseBLOB() {
		return useBLOB;
	}

	public void setUseBLOB(boolean useBLOB) {
		this.useBLOB = useBLOB;
	}

	public boolean isLowerCase() {
		return lowerCase;
	}

	public void setLowerCase(boolean lowerCase) {
		this.lowerCase = lowerCase;
	}

	public boolean isUpperCase() {
		return upperCase;
	}

	public void setUpperCase(boolean upperCase) {
		this.upperCase = upperCase;
	}

	public boolean isContinueOnError() {
		return continueOnError;
	}

	public void setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
	}

	public boolean isLockSourceTables() {
		return lockSourceTables;
	}

	public void setLockSourceTables(boolean lockSourceTables) {
		this.lockSourceTables = lockSourceTables;
	}

	public boolean isCreateTargetDatabaseIfNotExist() {
		return createTargetDatabaseIfNotExist;
	}

	public void setCreateTargetDatabaseIfNotExist(boolean createTargetDatabaseIfNotExist) {
		this.createTargetDatabaseIfNotExist = createTargetDatabaseIfNotExist;
	}

	public boolean isUseDDLFromShowCreateTable() {
		return useDDLFromShowCreateTable;
	}

	public void setUseDDLFromShowCreateTable(boolean useDDLFromShowCreateTable) {
		this.useDDLFromShowCreateTable = useDDLFromShowCreateTable;
	}

	public boolean isUseSingleTransaction() {
		return useSingleTransaction;
	}

	public void setUseSingleTransaction(boolean useSingleTransaction) {
		this.useSingleTransaction = useSingleTransaction;
	}

	public boolean isDropTargetObjectsBeforeCreate() {
		return dropTargetObjectsBeforeCreate;
	}

	public void setDropTargetObjectsBeforeCreate(boolean dropTargetObjectsBeforeCreate) {
		this.dropTargetObjectsBeforeCreate = dropTargetObjectsBeforeCreate;
	}

}
