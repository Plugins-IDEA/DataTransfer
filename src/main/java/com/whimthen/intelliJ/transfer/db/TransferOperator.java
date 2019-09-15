package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
	private        BigDecimal          progressVal;
	private        BigDecimal          tableLength = new BigDecimal(0);
	private        BigDecimal          oneHundredPercent = new BigDecimal(100);

	private TransferOperator(JTextArea eventLogPane, JProgressBar progressBar, List<JButton> enableButtons, JLabel progressLabel) {
		this.eventLogPane = eventLogPane;
		this.progressBar = progressBar;
		this.enableButtons = enableButtons;
		this.progressLabel = progressLabel;
		this.exceptionHandler = e -> this.eventLogPane.append(log(GlobalUtil.getMessage(e), false));
	}

	/**
	 * 数据转移
	 *
	 * @param model 用户输入选择的可选项
	 */
	public void transfer(TransferModel model) {
		ex(() -> {
//			progressVal = BigDecimal.ZERO;
			tableLength = BigDecimal.ZERO;
			progressLabel.setText("0%");
			progressBar.setValue(0);
//			progressLabel.updateUI();
			List<? extends DasTable> tables = model.getTables();
			if (Objects.nonNull(tables) && !tables.isEmpty()) {
				DbDataSource      dataSource = DataSourceCache.get(model.getSourceConn());
				TableInfoSupplier supplier   = OperatorFactory.createTableInfoSupplier(dataSource);
				// 所有表的数据量总和
				DataLength dataLength = supplier.getSizeFromTables(dataSource, tables);

				ThreadContainer.getInstance().run(exceptionHandler);
				for (int i = 0; i < tables.size(); i++) {
					DasTable      table     = tables.get(i);
					String        tableName = table.getName();
					final boolean isEnd     = i == tables.size() - 1;
					RunnableFunction runnable = () -> {
						String content = table.getName();
						eventLogPane.append(log(content, isEnd));
						UiUtil.scrollDown(eventLogPane);
						try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						updateProgressFromTable(dataLength.getTotalLength(), dataLength.getTableLength().get(tableName), isEnd);
						if (isEnd) {
							UiUtil.setButtonEnable(enableButtons, true);
						}
					};
					ThreadContainer.getInstance().log(runnable);
				}
			} else {
				UiUtil.setButtonEnable(enableButtons, true);
			}
		});
	}

	private void updateProgressFromTable(BigDecimal total, SingleTableDataLength singleDataLength, boolean isEnd) {
		if (Objects.nonNull(total) && Objects.nonNull(singleDataLength) && Objects.nonNull(singleDataLength.getDataLength())) {
			BigDecimal singleTableLength   = singleDataLength.getDataLength();
			tableLength = tableLength.add(singleTableLength);
//			BigDecimal singleTableProgress = singleTableLength.divide(total, RoundingMode.HALF_UP);
//			progressVal = progressVal.add(singleTableProgress);
//			BigDecimal progressVal = tableLength.divide(total, RoundingMode.HALF_UP).setScale(20, RoundingMode.HALF_UP).multiply(oneHundredPercent);
			double progress = tableLength.doubleValue() / total.doubleValue() * 100;
//			if (progressVal.compareTo(BigDecimal.ZERO) < 0) {
//				progressVal = BigDecimal.ZERO;
//			}
//			if (progressVal.compareTo(oneHundredPercent) > 0 || isEnd) {
//				progressVal = oneHundredPercent;
//			}

			if (progress < 0) {
				progress = 0.0;
			}
			if (progress > 100 || isEnd) {
				progress = 100.0;
			}
//			progressLabel.setText(progressVal.setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
			progressLabel.setText(new BigDecimal(progress).setScale(2, RoundingMode.HALF_UP).toPlainString() + "%");
			progressBar.setValue((int) progress);
//			progressLabel.updateUI();
		}
	}

	private void updateProgressLabelValFromLine(BigDecimal total, SingleTableDataLength singleDataLength) {

	}

	private String log(String content, boolean isEnd) {
		String log = String.format("[%s] - ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))) + content;
		if (!isEnd) {
			log += GlobalUtil.getLineSeparator();
		}
		return log;
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
			exceptionHandler.accept(exception);
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
