package com.whimthen.intelliJ.transfer.utils;

import java.util.Objects;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class StringUtils {

	public static boolean isNullOrEmpty(String... resources) {
		if (Objects.isNull(resources) || resources.length <= 0) {
			return true;
		}
		for (String resource : resources) {
			if (Objects.isNull(resource) || resource.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNotNullOrEmpty(String... resources) {
		return !isNullOrEmpty(resources);
	}

}
