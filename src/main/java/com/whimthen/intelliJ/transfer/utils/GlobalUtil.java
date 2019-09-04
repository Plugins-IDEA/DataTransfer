package com.whimthen.intelliJ.transfer.utils;

import org.apache.commons.lang.StringUtils;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class GlobalUtil {

	/**
	 * 对象不为空时执行
	 *
	 * @param obj 对象
	 * @param consumer 消费者
	 * @param <T> 范型
	 */
	public static<T> void nonNullConsumer(T obj, Consumer<T> consumer) {
		if (Objects.nonNull(obj)) {
			consumer.accept(obj);
		}
	}

	/**
	 * 获取系统换行符
	 *
	 * @return 换行符
	 */
	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

	/**
	 * 设置输入框只能输入数字
	 *
	 * @param textFields 输入框
	 */
	public static void onlyInputNumber(JTextField... textFields) {
		Stream.of(textFields).forEach(textField -> {
			textField.addKeyListener(new KeyAdapter(){
				public void keyTyped(KeyEvent e) {
					int keyChar = e.getKeyChar();
					if(keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9){
						//关键，屏蔽掉非法输入
						e.consume();
					}
				}
			});
		});
	}

	/**
	 * 复制字符串
	 *
	 * @param resource 需要重复的字符串资源
	 * @param count    重复之后共次数
	 * @return 重复之后的字符串
	 */
	public static String repeat(String resource, int count) {
		if (StringUtils.isEmpty(resource) || count <= 0) {
			return resource;
		}
		int len = resource.length();
		if (len == 0 || count == 0) {
			return "";
		}
		if (count == 1) {
			return resource;
		}
		if (Integer.MAX_VALUE / count < len) {
			throw new OutOfMemoryError("Repeating " + len + " bytes String " + count +
										   " times will produce a String exceeding maximum size.");
		}
		StringBuilder repeatString = new StringBuilder(resource);
		for (int i = 1; i < count; i++) {
			repeatString.append(resource);
		}
		return repeatString.toString();
	}

	/**
	 * 缩进字符串
	 *
	 * @param source      待缩进的字符串
	 * @param indentCount 缩进数(空格的数目)
	 * @return 缩进后的字符串
	 */
	public static String indent(String source, int indentCount) {
		if (indentCount > 0 && StringUtils.isNotEmpty(source)) {
			String indent = repeat(" ", indentCount);
			source = indent + source;
		}
		return source;
	}

	/**
	 * 向日志中写入异常信息
	 * Write exception information to the log
	 *
	 * @param exception 异常
	 *                  exception
	 */
	public static String getMessage(Throwable exception) {
		StringBuilder msg = new StringBuilder();
		while (Objects.nonNull(exception)) {
			msg.append(exception.getClass().getCanonicalName())
			   .append(": ")
			   .append(exception.getMessage())
			   .append(getLineSeparator())
			   .append(indent(getStackElementsMessage(exception.getStackTrace()), 6));
			exception = exception.getCause();
		}
		return msg.toString();
	}
	/**
	 * 获取当前栈信息
	 *
	 * @param stackTraceElements 栈信息对象数组
	 * @return 当前栈信息
	 */
	private static String getStackElementsMessage(StackTraceElement[] stackTraceElements) {
		StringBuilder stackInfo = new StringBuilder();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			stackInfo.append(indent(stackTraceElement.toString(), 6));
			stackInfo.append(getLineSeparator());
		}
		return stackInfo.toString().trim();
	}

	public static void setFieldValue(Object object, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = object.getClass().getDeclaredField(fieldName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		field.set(object, value);
		field.setAccessible(accessible);
	}

}
