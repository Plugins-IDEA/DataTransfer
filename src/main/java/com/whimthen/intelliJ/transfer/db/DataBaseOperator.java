package com.whimthen.intelliJ.transfer.db;

import com.intellij.database.console.DatabaseRunners;
import com.intellij.database.console.session.DatabaseSession;
import com.intellij.database.console.session.DatabaseSessionManager;
import com.intellij.database.dataSource.DatabaseConnection;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.connection.DGDepartment;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.RawConnectionConfig;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.script.PersistenceConsoleProvider;
import com.intellij.database.util.DbImplUtil;
import com.intellij.database.util.GuardedRef;
import com.intellij.util.Consumer;
import com.intellij.util.containers.JBIterable;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.RunnableFunction;
import com.whimthen.intelliJ.transfer.utils.UiUtil;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * DB操作类
 *
 * @author whimthen
 * @version 1.0.0
 */
public class DataBaseOperator {

	private static DataBaseOperator operator;
	private JTextArea eventLogPane;
	private JProgressBar progressBar;
	private Consumer<Throwable> exceptionHandler;

	private DataBaseOperator(JTextArea eventLogPane, JProgressBar progressBar) {
		this.eventLogPane = eventLogPane;
		this.progressBar = progressBar;
		this.exceptionHandler = e -> this.eventLogPane.append(log(GlobalUtil.getMessage(e)));
	}

	/**
	 * 数据转移
	 *
	 * @param model 用户输入选择的可选项
	 */
	public void transfer(TransferModel model) {
		List<? extends DasTable> tables = model.getTables();
		if (Objects.nonNull(tables) && !tables.isEmpty()) {
			DbDataSource dataSource = DataSourceCache.get(model.getSourceConn());
			// 所有表的数据量总和
			long         dataLength = getAllDataLength(dataSource);


			try {
				RawConnectionConfig            config     = dataSource.getConnectionConfig();
				DriverManager.getConnection(config.getUrl(), config.getName(), "dbDataSource.");
			} catch (SQLException e) {
				eventLogPane.append(log(GlobalUtil.getMessage(e)));
			} catch (Exception e) {
				e.printStackTrace();
			}
			LocalDataSource localDataSource = DbImplUtil.getLocalDataSource(dataSource);
			String          serialize       = localDataSource.getPasswordStorage().serialize();
			System.out.println(serialize);
			PersistenceConsoleProvider.Runner dataSourceRunner = DatabaseRunners.createDataSourceRunner(dataSource, () -> {

			});
			dataSourceRunner.run();

			Consumer<DatabaseSession> consumer = s -> {
				boolean connected = s.isConnected();
				System.out.println(connected);
			};
			List<DatabaseSession> sessions = DatabaseSessionManager.getSessions(dataSource.getProject(), DbImplUtil.getLocalDataSource(dataSource));
			JBIterable<PersistenceConsoleProvider.Runner> perSession = JBIterable.from(sessions).map((s) -> {
				return DatabaseRunners.createSessionRunner(s, () -> {
					consumer.consume(s);
				}, false);
			});
			perSession.get(0).run();

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
						UiUtil.setButtonEnable(model.getEnableButtons(), true);
					}
				};
				ThreadContainer.getInstance().log(runnable);
			}
		} else {
			UiUtil.setButtonEnable(model.getEnableButtons(), true);
		}
	}

	public Optional<Connection> getConnection(DbDataSource dataSource) {
		try {
			GuardedRef<DatabaseConnection> connectionGuardedRef = DbImplUtil.getDatabaseConnection(dataSource, DGDepartment.UNKNOWN);
			if (Objects.nonNull(connectionGuardedRef)) {
				DatabaseConnection databaseConnection = connectionGuardedRef.get();
				return Optional.of(databaseConnection.getJdbcConnection());
			}
		} catch (Exception e) {
			eventLogPane.append(log(GlobalUtil.getMessage(e)));
		}
		return Optional.empty();
	}

	private long getAllDataLength(DbDataSource dataSource) {
		TableInfoSupplier supplier = OperatorFactory.createTableInfoSupplier(dataSource);
		supplier.getSizeByConn();
		return 0;
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
		} catch (Exception exception) {
			exceptionHandler.consume(exception);
		}
	}

	public static void setOperator(JTextArea eventLogPane, JProgressBar progressBar) {
		if (Objects.isNull(operator)) {
			synchronized (DataBaseOperator.class) {
				if (Objects.isNull(operator)) {
					operator = new DataBaseOperator(eventLogPane, progressBar);
				}
			}
		}
	}

	public static DataBaseOperator getInstance() {
		if (Objects.isNull(operator)) {
			throw new UnsupportedOperationException("Please setOperator first!");
		}
		return operator;
	}

}
