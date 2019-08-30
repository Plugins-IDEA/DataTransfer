package com.whimthen.intelliJ.transfer.utils;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class GlobalUtil {

	public static<T> void nonNullConsumer(T obj, Consumer<T> consumer) {
		if (Objects.nonNull(obj)) {
			consumer.accept(obj);
		}
	}

}
