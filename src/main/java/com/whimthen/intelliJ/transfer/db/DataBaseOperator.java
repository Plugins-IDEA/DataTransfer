package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.util.DbImplUtil;
import com.whimthen.intelliJ.transfer.utils.RunnableFunction;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
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
			DbDataSource dbDataSource = DataSourceCache.get(model.getSourceConn());
			// 所有表的数据量总和
			long         dataLength = getAllDataLength(dbDataSource);
			LocalDataSource localDataSource = DbImplUtil.getLocalDataSource(dbDataSource);
			ThreadContainer.getInstance().run(e -> model.getEvenLog().append(e.getMessage()));
			for (int i = 0; i < tables.size(); i++) {
				DasTable table = tables.get(i);
				final boolean isEnd = i == tables.size() - 1;
				RunnableFunction runnable = () -> {
					String content = table.getName();
					if (!isEnd) {
						content += GlobalUtil.getLineSeparator();
					}
					model.getEvenLog().append(log(content));
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
				ThreadContainer.getInstance().log(runnable);
			}
		} else {
			UiUtil.setButtonEnable(model.getEnableButtons(), true);
		}
	}

	private static long getAllDataLength(DbDataSource dataSource) {
		TableInfoSupplier supplier = OperatorFactory.createTableInfoSupplier(dataSource);
		supplier.getSizeByConn();
		return 0;
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
