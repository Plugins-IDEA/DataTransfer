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

	public static String password(JPasswordField password) {
		if (Objects.isNull(password)) {
			throw new NullPointerException();
		}
		char[] passwords = password.getPassword();
		return Stream.of(passwords).map(String::valueOf).collect(Collectors.joining(""));
	}

}
