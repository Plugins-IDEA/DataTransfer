package com.whimthen.intelliJ.transfer.db;

import com.android.internal.util.FunctionalUtils;
import com.intellij.database.model.DasTable;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.UiUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * DB操作类
 *
 * @author whimthen
 * @version 1.0.0
 */
public class DataBaseOperator {

	/**
	 * 数据转移
	 *
	 * @param model 用户输入选择的可选项
	 */
	public static void transfer(TransferModel model) {
		List<? extends DasTable> tables = model.getTables();
		if (Objects.nonNull(tables) && !tables.isEmpty()) {
			ThreadContainer.getInstance().run(e -> model.getEvenLog().append(e.getMessage()));
			for (int i = 0; i < tables.size(); i++) {
				DasTable table = tables.get(i);
				final boolean isEnd = i == tables.size() - 1;
				FunctionalUtils.ThrowingRunnable throwingRunnable = () -> {
					model.getEvenLog().append(log(table.getName() + GlobalUtil.getLineSeparator()));
					UiUtil.scrollDown(model.getEvenLog());
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (isEnd) {
						UiUtil.setButtonEnable(model.getEnableButtons(), true);
					}
				};
				ThreadContainer.getInstance().log(throwingRunnable);
			}
		} else {
			UiUtil.setButtonEnable(model.getEnableButtons(), true);
		}
	}

	private static String log(String content) {
		return String.format("[%s] - ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + content;
	}

	/**
	 * 测试输入的是否可以连接
	 *
	 * @param info 连接信息
	 * @return true | false
	 */
	public static boolean testConnection(DataBaseInfo info) {
		return false;
	}

}
