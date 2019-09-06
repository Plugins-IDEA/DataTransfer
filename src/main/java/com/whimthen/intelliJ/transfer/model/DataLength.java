package com.whimthen.intelliJ.transfer.model;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataLength {

	private BigDecimal          totalLength;
	private Map<String, SingleTableDataLength> tableLength;

	public BigDecimal getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(BigDecimal totalLength) {
		this.totalLength = totalLength;
	}

	public Map<String, SingleTableDataLength> getTableLength() {
		return tableLength;
	}

	public void setTableLength(Map<String, SingleTableDataLength> tableLength) {
		this.tableLength = tableLength;
	}

}
