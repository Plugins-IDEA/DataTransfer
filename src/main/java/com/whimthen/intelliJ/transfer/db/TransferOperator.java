package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.util.Consumer;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.DataLength;
import com.whimthen.intelliJ.transfer.model.SingleTableDataLength;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.RunnableFunction;
import com.whimthen.intelliJ.transfer.utils.UiUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import java.math.BigDecimal;
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
public class TransferOperator {

	private static TransferOperator    operator;
	private        JTextArea           eventLogPane;
	private        JProgressBar        progressBar;
	private        Consumer<Throwable> exceptionHandler;
	private        List<JButton>       enableButtons;
	private        JLabel              progressLabel;
	private        double          progressVal;

	private TransferOperator(JTextArea eventLogPane, JProgressBar progressBar, List<JButton> enableButtons, JLabel progressLabel) {
		this.eventLogPane = eventLogPane;
		this.progressBar = progressBar;
		this.enableButtons = enableButtons;
		this.progressLabel = progressLabel;
		this.exceptionHandler = e -> this.eventLogPane.append(log(GlobalUtil.getMessage(e)));
	}

	/**
	 * 数据转移
	 *
	 * @param model 用户输入选择的可选项
	 */
	public void transfer(TransferModel model) {
		ex(() -> {
			progressVal = 0;
			progressLabel.setText(progressVal + "%");
			progressLabel.updateUI();
			List<? extends DasTable> tables = model.getTables();
			if (Objects.nonNull(tables) && !tables.isEmpty()) {
				DbDataSource      dataSource = DataSourceCache.get(model.getSourceConn());
				TableInfoSupplier supplier   = OperatorFactory.createTableInfoSupplier(dataSource);
				// 所有表的数据量总和
				DataLength dataLength = supplier.getSizeFromTables(dataSource, tables);

				ThreadContainer.getInstance().run(e -> eventLogPane.append(log(GlobalUtil.getMessage(e))));
				for (int i = 0; i < tables.size(); i++) {
					DasTable      table     = tables.get(i);
					String        tableName = table.getName();
					final boolean isEnd     = i == tables.size() - 1;
					RunnableFunction runnable = () -> {
						String content = table.getName();
						if (!isEnd) {
							content = content.replace(GlobalUtil.getLineSeparator(), "");
						}
						eventLogPane.append(log(content));
						UiUtil.scrollDown(eventLogPane);
						try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (isEnd) {
							UiUtil.setButtonEnable(enableButtons, true);
						}
						updateProgressFromTable(dataLength.getTotalLength(), dataLength.getTableLength().get(tableName));
					};
					ThreadContainer.getInstance().log(runnable);
				}
			} else {
				UiUtil.setButtonEnable(enableButtons, true);
			}
		});
	}

	private void updateProgressFromTable(BigDecimal total, SingleTableDataLength singleDataLength) {
		if (Objects.nonNull(total) && Objects.nonNull(singleDataLength)) {
			BigDecimal tableLength = singleDataLength.getDataLength();
//			BigDecimal singleTableProgress = tableLength.divide(total, BigDecimal.ROUND_DOWN);
			double progress = tableLength.doubleValue() / total.doubleValue() * 100;
			progressVal += progress;
			progressLabel.setText(new BigDecimal(progressVal).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "%");
//			progressLabel.updateUI();
		}
	}

	private void updateProgressLabelValFromLine(BigDecimal total, SingleTableDataLength singleDataLength) {

	}

	private String log(String content) {
		return String.format("[%s] - ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + content + GlobalUtil.getLineSeparator();
	}

	/**
	 * 测试输入的是否可以连接
	 *
	 * @param info 连接信息
	 * @return true | false
	 */
	public boolean testConnection(DataBaseInfo info) {
		return false;
	}

	private void ex(RunnableFunction runnableFunction) {
		try {
			runnableFunction.run();
		} catch (Throwable exception) {
			exceptionHandler.consume(exception);
			UiUtil.setButtonEnable(enableButtons, true);
		}
	}

	public static void setOperator(JTextArea eventLogPane, JProgressBar progressBar, List<JButton> enableButtons, JLabel progressLabel) {
		if (Objects.isNull(operator)) {
			synchronized (TransferOperator.class) {
				if (Objects.isNull(operator)) {
					operator = new TransferOperator(eventLogPane, progressBar, enableButtons, progressLabel);
				}
			}
		}
	}

	public static TransferOperator getInstance() {
		if (Objects.isNull(operator)) {
			throw new UnsupportedOperationException("Please setOperator first!");
		}
		return operator;
	}

}
