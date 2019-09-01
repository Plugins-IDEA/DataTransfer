package com.whimthen.intelliJ.transfer.utils;

import javax.swing.*;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class PasswordUtil {

	/**
	 * 获取Password输入框的文本
	 *
	 * @param password password输入框
	 * @return password
	 */
	public static String password(JPasswordField password) {
		if (Objects.isNull(password)) {
			throw new NullPointerException();
		}
		char[] passwords = password.getPassword();
		return Stream.of(passwords).map(String::valueOf).collect(Collectors.joining(""));
	}

}
