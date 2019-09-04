package com.whimthen.intelliJ.transfer.model;

import java.util.Map;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataLength {

	private long totalLength;
	private Map<String, Long> tableLength;

	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	public Map<String, Long> getTableLength() {
		return tableLength;
	}

	public void setTableLength(Map<String, Long> tableLength) {
		this.tableLength = tableLength;
	}

}
