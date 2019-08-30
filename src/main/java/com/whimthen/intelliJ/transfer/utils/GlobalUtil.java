package com.whimthen.intelliJ.transfer.utils;

import javax.swing.JTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}

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

}
