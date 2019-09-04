package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.util.Consumer;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.DataLength;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.RunnableFunction;
import com.whimthen.intelliJ.transfer.utils.UiUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
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
			List<? extends DasTable> tables = model.getTables();
			if (Objects.nonNull(tables) && !tables.isEmpty()) {
				DbDataSource dataSource = DataSourceCache.get(model.getSourceConn());
				TableInfoSupplier supplier = OperatorFactory.createTableInfoSupplier(dataSource);
				// 所有表的数据量总和
				DataLength dataLength = supplier.getSizeFromTables(dataSource, tables);
//				supplier.getConnection(dataSource).ifPresent(LambdaExUtil.rethrowConsumer(connection -> {
//					if (connection.isClosed()) {
//						return;
//					}
//					PreparedStatement preparedStatement = connection.prepareStatement("SELECT TABLE_NAME tableName, AVG_ROW_LENGTH avgLength, DATA_LENGTH dataLength FROM information_schema.TABLES");
//					ResultSet         resultSet         = preparedStatement.executeQuery();
//					while (resultSet.next()) {
//						Object object = resultSet.getObject(1);
//						System.out.println(object);
//					}
//					System.out.println(resultSet);
//				}));

//				LocalDataSource localDataSource = DbImplUtil.getLocalDataSource(dataSource);
//				String          serialize       = localDataSource.getPasswordStorage().serialize();
//				System.out.println(serialize);
//				PersistenceConsoleProvider.Runner dataSourceRunner = DatabaseRunners.createDataSourceRunner(dataSource, () -> {
//
//				});
//				dataSourceRunner.run();
//
//				Consumer<DatabaseSession> consumer = s -> {
//					boolean connected = s.isConnected();
//					System.out.println(connected);
//				};
//				List<DatabaseSession> sessions = DatabaseSessionManager.getSessions(dataSource.getProject(), DbImplUtil.getLocalDataSource(dataSource));
//				JBIterable<PersistenceConsoleProvider.Runner> perSession = JBIterable.from(sessions).map((s) -> {
//					return DatabaseRunners.createSessionRunner(s, () -> {
//						consumer.consume(s);
//					}, false);
//				});
//				perSession.get(0).run();

				ThreadContainer.getInstance().run(e -> eventLogPane.append(log(GlobalUtil.getMessage(e))));
				for (int i = 0; i < tables.size(); i++) {
					DasTable table = tables.get(i);
					final boolean isEnd = i == tables.size() - 1;
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
					};
					ThreadContainer.getInstance().log(runnable);
				}
			} else {
				UiUtil.setButtonEnable(enableButtons, true);
			}
		});
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
