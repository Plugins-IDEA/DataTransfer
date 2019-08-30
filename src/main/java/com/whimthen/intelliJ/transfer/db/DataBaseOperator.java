package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.model.DasTable;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;

import javax.swing.JTextArea;
import java.util.List;
import java.util.Objects;

/**
 * DB操作类
 *
 * @author whimthen
 * @version 1.0.0
 */
public class DataBaseOperator {

	public static void transfer(TransferModel model, JTextArea eventLog) {
		List<DasTable> tables = model.getTables();
		if (Objects.nonNull(tables) && !tables.isEmpty()) {
			tables.forEach(table -> {
				ThreadContainer.getInstance().exec(() -> {
					eventLog.append(table.getName());
					eventLog.append(GlobalUtil.getLineSeparator());
					eventLog.selectAll();
					if (eventLog.getSelectedText() != null) {
						eventLog.setCaretPosition(eventLog.getSelectedText().length());
						eventLog.requestFocus();
					}
				});
			});
		}
	}

	public static boolean testConnection(DataBaseInfo info) {
		return false;
	}

}
