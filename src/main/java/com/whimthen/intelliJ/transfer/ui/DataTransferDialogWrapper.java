package com.whimthen.intelliJ.transfer.ui;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBColor;
import com.intellij.util.containers.JBIterable;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.db.TransferOperator;
import com.whimthen.intelliJ.transfer.model.ConnectionType;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.StartType;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.tasks.ThreadContainer;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.PasswordUtil;
import com.whimthen.intelliJ.transfer.utils.UiUtil;
import icons.DatabaseIcons;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DataTransferDialogWrapper extends JDialog {

	private JPanel            contentPane;
	private JTabbedPane       optionsTabbedPane;
	private JScrollPane       dataBaseObjectsPane;
	private JScrollPane       eventLogScrollPane;
	private JComboBox<String> targetConnComboBox;
	private JComboBox<String> targetDbComboBox;
	private JComboBox<String> sourceConnComboBox;
	private JComboBox<String> sourceDbComboBox;
	private JButton           buttonOK;
	private JButton           buttonCancel;
	private JButton           startButton;
	private JButton           newDataSourceButton;
	private JButton           testTargetConnectionButton;
	private JButton           optionsStartButton;
	private JButton           testSourceConnectionButton;
	private JRadioButton      upperCaseRadioButton;
	private JRadioButton      lowerCaseRadioButton;
	private JLabel            tableOptionsLabel;
	private JLabel            recordOptionsLabel;
	private JLabel            otherOptionsLabel;
	private JLabel            progressValueLabel;
	private JCheckBox         convertObjectNameToCheckBox;
	private JCheckBox         continueOnErrorCheckBox;
	private JCheckBox         lockSourceTablesCheckBox;
	private JCheckBox         createTargetDatabaseIfNotExistCheckBox;
	private JCheckBox         useDDLFromShowCreateTableCheckBox;
	private JCheckBox         useSingleTransactionCheckBox;
	private JCheckBox         dropTargetObjectsBeforeCreateCheckBox;
	private JCheckBox         createTablesCheckBox;
	private JCheckBox         includeIndexesCheckBox;
	private JCheckBox         includeForeignKeyConstraintsCheckBox;
	private JCheckBox         includeEngineTableTypeCheckBox;
	private JCheckBox         includeCharacterSetCheckbox;
	private JCheckBox         includeAutoIncrementCheckbox;
	private JCheckBox         includeOtherTableOptionsCheckbox;
	private JCheckBox         includeTriggersCheckbox;
	private JCheckBox         insertRecordsCheckbox;
	private JCheckBox         lockTargetTablesCheckbox;
	private JCheckBox         useTransactionCheckbox;
	private JCheckBox         useCompleteInsertStatementsCheckbox;
	private JCheckBox         useExtendedInsertStatementsCheckbox;
	private JCheckBox         useDelayedInsertStatementsCheckbox;
	private JCheckBox         useBLOBCheckbox;
	private JTextField        sourceHostTextField;
	private JTextField        sourcePortTextField;
	private JTextField        sourceUserTextField;
	private JTextField        sourceDbTextField;
	private JTextField        targetHostTextField;
	private JTextField        targetPortTextField;
	private JTextField        targetUserTextField;
	private JTextField        targetDbTextField;
	private JPasswordField    sourcePwdTextField;
	private JPasswordField    targetPwdTextField;
	private JTextArea         eventLogArea;
	private JProgressBar      progressBar;

	private CheckedTreeNode          tableTreeRoot           = new CheckedTreeNode();
	private int                      tableTreeSelectionCount = 0;
	private List<? extends DasTable> tableList               = Collections.emptyList();
	private CheckboxTree             tableCheckboxTree;
	private List<DasTable> unSelectTables = new ArrayList<>();

	private DataTransferDialogWrapper(Project project) {
		setTitle("Data Transfer");
		contentPane.setPreferredSize(new Dimension(800, 740));
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		setMinimumSize(new Dimension(800, 765));
		Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize     = defaultToolkit.getScreenSize();
		setLocation(Double.valueOf(screenSize.getWidth()).intValue() / 2 - 400, Double.valueOf(screenSize.getHeight()).intValue() / 2 - 370);

		tableCheckboxTree = UiUtil.createCheckboxTree(UiUtil.getTableRenderer(), tableTreeRoot);
//		CheckedTreeNode viewsTreeRoot     = new CheckedTreeNode();
//		CheckboxTree    viewsCheckboxTree = UiUtil.createCheckboxTree(UiUtil.getTableRenderer(), viewsTreeRoot);

		addConnComboBoxItem();

		addDbComboBoxItem(sourceConnComboBox, sourceDbComboBox);
		sourceConnComboBox.addItemListener(e -> addDbComboBoxItem(sourceConnComboBox, sourceDbComboBox));
		addDbComboBoxItem(targetConnComboBox, targetDbComboBox);
		targetConnComboBox.addItemListener(e -> addDbComboBoxItem(targetConnComboBox, targetDbComboBox));

		startButton.addActionListener(startListener());
		startButton.setIcon(AllIcons.Actions.Execute);
		optionsStartButton.addActionListener(optionsStartListener());
		optionsStartButton.setIcon(AllIcons.Actions.Execute);
		testSourceConnectionButton.addActionListener(testConnectionListener(ConnectionType.SOURCE));
		testTargetConnectionButton.addActionListener(testConnectionListener(ConnectionType.TARGET));

		addTables2Tree(StartType.SELECT);
		sourceDbComboBox.addItemListener(e -> addTables2Tree(StartType.SELECT));

		GlobalUtil.onlyInputNumber(sourcePortTextField, targetPortTextField);

		UiUtil.addCheckboxClickShowMoreListener(tableCheckboxTree, tableTreeRoot);
		addCheckboxTreeListener(tableTreeRoot);
		tableCheckboxTree.setRootVisible(true);
		tableCheckboxTree.setEnabled(true);
		tableCheckboxTree.setShowsRootHandles(true);
		tableCheckboxTree.setVisible(true);
		dataBaseObjectsPane.setViewportView(tableCheckboxTree);
		UiUtil.setJScrollBar(dataBaseObjectsPane);
		UiUtil.setJScrollBar(eventLogScrollPane);

		newDataSourceButton.setIcon(DatabaseIcons.Add);
		newDataSourceButton.addActionListener(e -> UiUtil.showAddDataSourceDialog(project));

		JBColor jbColor = new JBColor(new Color(203, 217, 244, 80), new Color(203, 217, 244, 80));
		tableOptionsLabel.setBackground(jbColor);
		recordOptionsLabel.setBackground(jbColor);
		otherOptionsLabel.setBackground(jbColor);

		createTablesCheckBox.addActionListener(createTablesCheckboxListener());
		insertRecordsCheckbox.addActionListener(insertRecordsCheckboxListener());
		convertObjectNameToCheckBox.addActionListener(convertObjectNameToCheckboxListener());
		addJCheckboxActionListener(getCreateTablesJCheckbox(), createTablesAllSelectedListener());
		addJCheckboxActionListener(getInsertRecordsJCheckbox(), insertRecordsAllSelectedListener());

		TransferOperator.setOperator(eventLogArea, progressBar, getActionButtons(), progressValueLabel);

		buttonOK.addActionListener(e -> onOK());
		buttonCancel.addActionListener(e -> onCancel());

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(),
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	/**
	 * 创建Options Connection面板的两个Test connection按钮的监听事件
	 *
	 * @param connectionType 连接类型 -> 源数据源 | 目标数据源
	 * @return 监听事件
	 */
	private ActionListener testConnectionListener(ConnectionType connectionType) {
		return (ActionEvent e) -> {
			String host, port, user, password, database;
			if (connectionType.equals(ConnectionType.SOURCE)) {
				host = sourceHostTextField.getText();
				port = sourcePortTextField.getText();
				user = sourceUserTextField.getText();
				password = PasswordUtil.password(sourcePwdTextField);
				database = sourceDbTextField.getText();
			} else {
				host = targetHostTextField.getText();
				port = targetPortTextField.getText();
				user = targetUserTextField.getText();
				password = PasswordUtil.password(targetPwdTextField);
				database = targetDbTextField.getText();
			}
			DataBaseInfo info = new DataBaseInfo();
			info.setHost(host);
			info.setPort(port);
			info.setUser(user);
			info.setPassword(password);
			info.setDatabase(database);
			boolean isSuccess = TransferOperator.getInstance().testConnection(info);
			// TODO 暂时弹窗提示测试连接是否成功, 后续改为按钮前面显示, 更为直观
			if (!isSuccess) {
				Messages.showErrorDialog("The Database connect fail", "Test Connection Result");
			} else {
				Messages.showMessageDialog("Connect Successful!", "Test Connection Result", Messages.getInformationIcon());
			}

			selectTableTreePanel();
			UiUtil.resetTableSkip();
			addTables2Tree(StartType.OPTIONS);
		};
	}

	/**
	 * 创建Options Connection面板的Start按钮的监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener optionsStartListener() {
		return (ActionEvent e) -> {
			List<? extends DasTable> selectionTables = getSelectionTables(StartType.SELECT);
			if (Objects.isNull(selectionTables) || selectionTables.isEmpty()) {
				Messages.showWarningDialog("Please select transfer tables!", "Transfer Tables Is Empty");
				return;
			}
			List<JButton> actionButtons = getActionButtons();
			UiUtil.setButtonEnable(actionButtons, false);
			selectEventLogPanel();
			TransferModel model = new TransferModel();
			model.setType(StartType.OPTIONS);
			model.setSourceHost(sourceHostTextField.getText());
			model.setSourcePort(sourcePortTextField.getText());
			model.setSourceUser(sourceUserTextField.getText());
			model.setSourcePwd(sourcePwdTextField.getText());
			model.setSourceDb(sourceDbTextField.getText());
			model.setTargetHost(targetHostTextField.getText());
			model.setTargetPort(targetPortTextField.getText());
			model.setTargetUser(targetUserTextField.getText());
			model.setTargetPwd(targetPwdTextField.getText());
			model.setTargetDb(targetDbTextField.getText());
			model.setTables(selectionTables);
			setModelOtherProperties(model);
			TransferOperator.getInstance().transfer(model);
		};
	}

	private List<JButton> getActionButtons(JButton... buttons) {
		ArrayList<JButton> jButtons = new ArrayList<>();
		jButtons.add(optionsStartButton);
		jButtons.add(startButton);
		jButtons.add(buttonOK);
		jButtons.add(buttonCancel);
		if (buttons.length > 0) {
			jButtons.addAll(Arrays.asList(buttons));
		}
		return jButtons;
	}

	/**
	 * 创建Select Connection面板中的Start按钮的监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener startListener() {
		return (ActionEvent e) -> {
			List<? extends DasTable> selectionTables = getSelectionTables(StartType.SELECT);
			if (Objects.isNull(selectionTables) || selectionTables.isEmpty()) {
				Messages.showWarningDialog("Please select transfer tables!", "Transfer Tables Is Empty");
				return;
			}
			List<JButton> actionButtons = getActionButtons();
			UiUtil.setButtonEnable(actionButtons, false);
			selectEventLogPanel();
			TransferModel model = new TransferModel();
			model.setType(StartType.SELECT);
			model.setSourceConn((String) sourceConnComboBox.getSelectedItem());
			model.setTargetConn((String) targetConnComboBox.getSelectedItem());
			model.setSourceDb((String) sourceDbComboBox.getSelectedItem());
			model.setTargetDb((String) targetDbComboBox.getSelectedItem());
			model.setTables(selectionTables);
			setModelOtherProperties(model);
			TransferOperator.getInstance().transfer(model);
		};
	}

	/**
	 * 设置转换实体的其他字段值
	 *
	 * @param model 转换实体
	 */
	private void setModelOtherProperties(TransferModel model) {
		boolean createTablesCheckBoxSelected = createTablesCheckBox.isSelected();
		model.setCreateTables(createTablesCheckBoxSelected);
		if (createTablesCheckBoxSelected) {
			model.setIncludeIndexes(includeIndexesCheckBox.isSelected());
			model.setIncludeForeignKeyConstraints(includeForeignKeyConstraintsCheckBox.isSelected());
			model.setIncludeEngineTableType(includeEngineTableTypeCheckBox.isSelected());
			model.setIncludeCharacterSet(includeCharacterSetCheckbox.isSelected());
			model.setIncludeAutoIncrement(includeAutoIncrementCheckbox.isSelected());
			model.setIncludeOtherTableOptions(includeOtherTableOptionsCheckbox.isSelected());
			model.setIncludeTriggers(includeTriggersCheckbox.isSelected());
		}
		boolean insertRecordsCheckboxSelected = insertRecordsCheckbox.isSelected();
		model.setInsertRecords(insertRecordsCheckboxSelected);
		if (insertRecordsCheckboxSelected) {
			model.setLockTargetTables(lockTargetTablesCheckbox.isSelected());
			model.setUseTransaction(useTransactionCheckbox.isSelected());
			model.setUseCompleteInsertStatements(useCompleteInsertStatementsCheckbox.isSelected());
			model.setUseExtendedInsertStatements(useExtendedInsertStatementsCheckbox.isSelected());
			model.setUseDelayedInsertStatements(useDelayedInsertStatementsCheckbox.isSelected());
			model.setUseBLOB(useBLOBCheckbox.isSelected());
		}
		model.setLowerCase(lowerCaseRadioButton.isSelected());
		model.setUpperCase(upperCaseRadioButton.isSelected());
		model.setContinueOnError(continueOnErrorCheckBox.isSelected());
		model.setLockSourceTables(lockSourceTablesCheckBox.isSelected());
		model.setCreateTargetDatabaseIfNotExist(createTargetDatabaseIfNotExistCheckBox.isSelected());
		model.setUseDDLFromShowCreateTable(useDDLFromShowCreateTableCheckBox.isSelected());
		model.setUseSingleTransaction(useSingleTransactionCheckBox.isSelected());
		model.setDropTargetObjectsBeforeCreate(dropTargetObjectsBeforeCreateCheckBox.isSelected());
	}

	private List<? extends DasTable> getSelectionTables(StartType startType) {
		return getTables(startType).stream()
								   .filter(table -> !unSelectTables.contains(table))
								   .collect(Collectors.toList());
	}

	/**
	 * 创建名称转换的监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener convertObjectNameToCheckboxListener() {
		return (ActionEvent e) -> {
			boolean isEnable = false;
			if (((JCheckBox) e.getSource()).isSelected()) {
				isEnable = true;
			}
			lowerCaseRadioButton.setEnabled(isEnable);
			upperCaseRadioButton.setEnabled(isEnable);
		};
	}

	/**
	 * 创建Insert Records复选框的监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener insertRecordsCheckboxListener() {
		List<JCheckBox> insertRecordsJCheckboxes = getInsertRecordsJCheckbox();
		insertRecordsJCheckboxes.add(insertRecordsCheckbox);
		return (ActionEvent e) -> UiUtil.setCheckboxChecked(UiUtil.isChecked(e), insertRecordsJCheckboxes);
	}

	/**
	 * 创建Create tables监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener createTablesCheckboxListener() {
		List<JCheckBox> createTablesJCheckboxes = getCreateTablesJCheckbox();
		createTablesJCheckboxes.add(createTablesCheckBox);
		return (ActionEvent e) -> UiUtil.setCheckboxChecked(UiUtil.isChecked(e), createTablesJCheckboxes);
	}

	/**
	 * 添加监听事件到所有的复选框点击事件
	 *
	 * @param checkBoxes 复选框
	 * @param listener   监听事件
	 */
	private void addJCheckboxActionListener(List<JCheckBox> checkBoxes, ActionListener listener) {
		checkBoxes.forEach(checkbox -> checkbox.addActionListener(listener));
	}

	/**
	 * 创建Insert Records子复选框监听事件
	 *
	 * @return 监听事件
	 */
	private ActionListener insertRecordsAllSelectedListener() {
		return (ActionEvent e) -> {
			List<JCheckBox> insertRecordsJCheckboxes = getInsertRecordsJCheckbox();
			if (UiUtil.isAnyChecked(insertRecordsJCheckboxes)) {
				insertRecordsCheckbox.setSelected(true);
			} else if (UiUtil.isAllUnChecked(insertRecordsJCheckboxes)) {
				insertRecordsCheckbox.setSelected(false);
			}
		};
	}

	/**
	 * 创建Create tables子复选框的监听事件
	 * 子复选框至少又一个为选中时, 设置父复选框为选中状态
	 * 子复选框都没有选中时, 设置父复选框为未选中状态
	 *
	 * @return 监听事件
	 */
	private ActionListener createTablesAllSelectedListener() {
		return (ActionEvent e) -> {
			List<JCheckBox> createTablesJCheckboxes = getCreateTablesJCheckbox();
			if (UiUtil.isAnyChecked(createTablesJCheckboxes)) {
				createTablesCheckBox.setSelected(true);
			} else if (UiUtil.isAllUnChecked(createTablesJCheckboxes)) {
				createTablesCheckBox.setSelected(false);
			}
		};
	}

	/**
	 * 获取Insert Records下面的子复选框
	 *
	 * @return 复选框集合
	 */
	private List<JCheckBox> getInsertRecordsJCheckbox() {
		return new ArrayList<>(Arrays.asList(lockTargetTablesCheckbox, useTransactionCheckbox, useCompleteInsertStatementsCheckbox,
			useExtendedInsertStatementsCheckbox, useDelayedInsertStatementsCheckbox, useBLOBCheckbox));
	}

	/**
	 * 获取Options tabbedPane面板的Create tables下面的子复选框
	 *
	 * @return 复选框集合
	 */
	private List<JCheckBox> getCreateTablesJCheckbox() {
		return new ArrayList<>(Arrays.asList(includeIndexesCheckBox, includeForeignKeyConstraintsCheckBox, includeEngineTableTypeCheckBox,
			includeCharacterSetCheckbox, includeAutoIncrementCheckbox, includeOtherTableOptionsCheckbox, includeTriggersCheckbox));
	}

	private boolean isAnyChecked(List<JCheckBox> checkBoxes) {
		return checkBoxes.stream().anyMatch(AbstractButton::isSelected);
	}

	private boolean isAllUnChecked(List<JCheckBox> checkBoxes) {
		return checkBoxes.stream().noneMatch(AbstractButton::isSelected);
	}

	private boolean isTreeRoot = true, isCheckedAll = true;

	/**
	 * 复选框树的监听事件
	 *
	 * @param root 根节点
	 */
	private void addCheckboxTreeListener(CheckedTreeNode root) {
		tableCheckboxTree.addCheckboxTreeListener(new CheckboxTreeListener() {
			@Override
			public void nodeStateChanged(@NotNull CheckedTreeNode node) {
//				int     rowCount = tableList.size();
//				boolean isRoot   = true;
				if (!node.isRoot()) {
//					isTreeRoot = false;
//					boolean checked = node.isChecked();
//					if (checked) tableTreeSelectionCount++;
//					else tableTreeSelectionCount--;
//					if (tableTreeSelectionCount < 0)
//						tableTreeSelectionCount = 0;
//					if (tableTreeSelectionCount > rowCount) {
//						tableTreeSelectionCount = rowCount;
//						if (!checked) {
//							tableTreeSelectionCount--;
//						}
//					}
////					root.setUserObject(UiUtil.getTableRootNodeText(tableTreeSelectionCount, rowCount));
//					isRoot = false;
					DasTable table = (DasTable) node.getUserObject();
					if (node.isChecked()) {
						unSelectTables.remove(table);
					} else {
						unSelectTables.add(table);
					}
				}
//				else if (!isTreeRoot) {
//					isTreeRoot = true;
//					return;
//				}
//				if (isRoot) {
//					isTreeRoot = true;
//					if (!isCheckedAll) {
//						tableTreeSelectionCount = tableList.size();
//					} else {
//						tableTreeSelectionCount = 0;
//					}
////					root.setUserObject(UiUtil.getTableRootNodeText(tableTreeSelectionCount, rowCount));
//					isCheckedAll = !isCheckedAll;
//				}
//				tableCheckboxTree.updateUI();
//				isChildRoot = !isChildRoot;
			}
		});
	}

	/**
	 * 添加子节点到表树
	 */
	private void addTables2Tree(StartType startType) {
		List<? extends DasTable> tableList = getTables(startType);
		UiUtil.resetTableSkip();
		UiUtil.addTables(tableList, tableTreeRoot, true);
		tableTreeRoot.setUserObject(UiUtil.getTableRootNodeText(tableList.size()));
		tableCheckboxTree.updateUI();
	}

	/**
	 * 获取连接DB或Schema中所有表
	 *
	 * @param startType 运行模式
	 * @return 表集合
	 */
	private List<? extends DasTable> getTables(StartType startType) {
		List<? extends DasTable> tableList = null;
		if (StartType.SELECT.equals(startType)) {
			String db = (String) sourceDbComboBox.getSelectedItem();
			if (UiUtil.isSelectText(db)) {
				String conn = (String) sourceConnComboBox.getSelectedItem();
				if (StringUtils.isNotEmpty(conn)) {
					tableList = Objects.requireNonNull(DataSourceCache.getTablesByDataSourceName(conn).orElse(null));
				}
			} else if (StringUtils.isNotEmpty(db)) {
				tableList = DataSourceCache.getTables(db);
			}
			if (Objects.isNull(tableList)) {
				tableList = Collections.emptyList();
			}
			this.tableList = tableList;
		} else if (StartType.OPTIONS.equals(startType)) {
			if (Objects.isNull(this.tableList)) {
				tableList = Collections.emptyList();
			} else {
				tableList = this.tableList;
			}
		}
//		tableTreeSelectionCount = tableList.size();
		return tableList;
	}

	/**
	 * 设置数据源下拉框值
	 */
	private void addConnComboBoxItem() {
		DataSourceCache.getDataSources().forEach(dataSource -> {
			sourceConnComboBox.addItem(dataSource.getName());
			targetConnComboBox.addItem(dataSource.getName());
		});
	}

	/**
	 * 设置DataBase下拉框的值
	 *
	 * @param connComboBox 数据源的下拉框
	 * @param dbComboBox   DataBase下拉框
	 */
	private void addDbComboBoxItem(JComboBox<String> connComboBox, JComboBox<String> dbComboBox) {
		dbComboBox.removeAllItems();
		dbComboBox.addItem(UiUtil.SELECT_DATA_BASE);
		String sourceSelectConn = (String) connComboBox.getSelectedItem();
		if (StringUtils.isNotEmpty(sourceSelectConn)) {
			JBIterable<? extends DasNamespace> schemas = DataSourceCache.getSchemas(sourceSelectConn);
			GlobalUtil.nonNullConsumer(schemas, schema -> {
				schema.forEach(s -> dbComboBox.addItem(s.getName()));
			});
		}
	}

	/**
	 * 选中Options面板的最后一个
	 */
	private void selectEventLogPanel() {
		optionsTabbedPane.setSelectedIndex(optionsTabbedPane.getTabCount() - 1);
	}

	/**
	 * 选中Options TableTree面板
	 */
	private void selectTableTreePanel() {
		optionsTabbedPane.setSelectedIndex(0);
	}

	private void onOK() {
		String selectedItem  = (String) sourceConnComboBox.getSelectedItem();
		Object selectedItem1 = targetConnComboBox.getSelectedItem();
		Messages.showMessageDialog("source: " + selectedItem + ", target: " + selectedItem1, "Connection", Messages.getInformationIcon());
		ThreadContainer.getInstance().shutdown();
//		dispose();
	}

	private void onCancel() {
		dispose();
	}

	public static DataTransferDialogWrapper getInstance(Project project) {
		return new DataTransferDialogWrapper(project);
	}

}
