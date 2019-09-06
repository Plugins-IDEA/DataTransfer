package com.whimthen.intelliJ.transfer.model;

import java.math.BigDecimal;

/**
 * @author whimthen
 * @version 1.0.0
 * @since 1.0.0
 */
public class SingleTableDataLength {

	private BigDecimal avgLength;
	private BigDecimal dataLength;

	public BigDecimal getAvgLength() {
		return avgLength;
	}

	public void setAvgLength(BigDecimal avgLength) {
		this.avgLength = avgLength;
	}

	public BigDecimal getDataLength() {
		return dataLength;
	}

	public void setDataLength(BigDecimal dataLength) {
		this.dataLength = dataLength;
	}

}
