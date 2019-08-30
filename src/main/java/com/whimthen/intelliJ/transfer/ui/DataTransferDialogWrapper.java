package com.whimthen.intelliJ.transfer.ui;

import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckboxTreeListener;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBColor;
import com.intellij.util.containers.JBIterable;
import com.whimthen.intelliJ.transfer.cache.DataSourceCache;
import com.whimthen.intelliJ.transfer.db.DataBaseOperator;
import com.whimthen.intelliJ.transfer.model.DataBaseInfo;
import com.whimthen.intelliJ.transfer.model.StartType;
import com.whimthen.intelliJ.transfer.model.TestConnectionType;
import com.whimthen.intelliJ.transfer.model.TransferModel;
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
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
import java.util.Enumeration;
import java.util.List;

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

	private CheckedTreeNode tableTreeRoot           = new CheckedTreeNode();
	private int             tableTreeSelectionCount = 0;

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

		CheckboxTree tableCheckboxTree = UiUtil.createCheckboxTree(UiUtil.getTableRenderer(), tableTreeRoot);
//		CheckedTreeNode viewsTreeRoot     = new CheckedTreeNode();
//		CheckboxTree    viewsCheckboxTree = UiUtil.createCheckboxTree(UiUtil.getTableRenderer(), viewsTreeRoot);

		addConnComboBoxItem();

		addDbComboBoxItem(sourceConnComboBox, sourceDbComboBox);
		sourceConnComboBox.addItemListener(e -> {
			sourceDbComboBox.removeAllItems();
			addDbComboBoxItem(sourceConnComboBox, sourceDbComboBox);
		});
		addDbComboBoxItem(targetConnComboBox, targetDbComboBox);
		targetConnComboBox.addItemListener(e -> {
			targetDbComboBox.removeAllItems();
			addDbComboBoxItem(targetConnComboBox, targetDbComboBox);
		});

		startButton.addActionListener(startListener());
		optionsStartButton.addActionListener(optionsStartListener());
		testSourceConnectionButton.addActionListener(testConnectionListener(TestConnectionType.SOURCE));
		testTargetConnectionButton.addActionListener(testConnectionListener(TestConnectionType.TARGET));

		addTables2Tree(tableCheckboxTree);
		sourceDbComboBox.addItemListener(e -> addTables2Tree(tableCheckboxTree));

		GlobalUtil.onlyInputNumber(sourcePortTextField, targetPortTextField);

		addCheckboxTreeListener(tableCheckboxTree, tableTreeRoot);
		tableCheckboxTree.setRootVisible(true);
		tableCheckboxTree.setEnabled(true);
		tableCheckboxTree.setShowsRootHandles(true);
		tableCheckboxTree.setVisible(true);
		dataBaseObjectsPane.setViewportView(tableCheckboxTree);
		UiUtil.setJScrollBar(dataBaseObjectsPane);
		UiUtil.setJScrollBar(eventLogScrollPane);

		newDataSourceButton.setIcon(DatabaseIcons.Dbms);
		newDataSourceButton.addActionListener(e -> UiUtil.showAddDataSourceDialog(project, sourceConnComboBox));

		JBColor jbColor = new JBColor(new Color(203, 217, 244, 80), new Color(203, 217, 244, 80));
		tableOptionsLabel.setBackground(jbColor);
		recordOptionsLabel.setBackground(jbColor);
		otherOptionsLabel.setBackground(jbColor);

		createTablesCheckBox.addActionListener(createTablesCheckboxListener());
		insertRecordsCheckbox.addActionListener(insertRecordsCheckboxListener());
		convertObjectNameToCheckBox.addActionListener(convertObjectNameToCheckboxListener());
		addJCheckboxActionListener(getCreateTablesJCheckbox(), createTablesAllSelectedListener());
		addJCheckboxActionListener(getInsertRecordsJCheckbox(), insertRecordsAllSelectedListener());

		progressBar.setValue(20);

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

	private ActionListener testConnectionListener(TestConnectionType connectionType) {
		return (ActionEvent e) -> {
			String host, port, user, password, database;
			if (connectionType.equals(TestConnectionType.SOURCE)) {
				host = sourceHostTextField.getText();
				port = sourcePortTextField.getText();
				user = sourceUserTextField.getText();
				password = sourcePwdTextField.getText();
				database = sourceDbTextField.getText();
			} else {
				host = targetHostTextField.getText();
				port = targetPortTextField.getText();
				user = targetUserTextField.getText();
				password = targetPwdTextField.getText();
				database = targetDbTextField.getText();
			}
			DataBaseInfo info = new DataBaseInfo();
			info.setHost(host);
			info.setPort(port);
			info.setUser(user);
			info.setPassword(password);
			info.setDatabase(database);
			boolean isSuccess = DataBaseOperator.testConnection(info);
			// TODO 暂时弹窗提示测试连接是否成功, 后续改为按钮前面显示, 更为直观
			if (!isSuccess) {
				Messages.showErrorDialog("The Database connect fail", "Test Connection Result");
			} else {
				Messages.showMessageDialog("Connect Successful!", "Test Connection Result", Messages.getInformationIcon());
			}
		};
	}

	private ActionListener optionsStartListener() {
		return (ActionEvent e) -> {
			selectEventLogPanel();
			TransferModel model = new TransferModel();
			model.setType(StartType.OPTIONS);
			model.setSourceHost(sourceHostTextField.getText());
			model.setSourcePort(sourcePortTextField.getText());
			model.setSourceUser(sourceUserTextField.getText());
			model.setSourcePwd(sourcePwdTextField.getText());
			model.setOptionsSourceDb(sourceDbTextField.getText());
			model.setTargetHost(targetHostTextField.getText());
			model.setTargetPort(targetPortTextField.getText());
			model.setTargetUser(targetUserTextField.getText());
			model.setTargetPwd(targetPwdTextField.getText());
			model.setOptionsTargetDb(targetDbTextField.getText());
			setModelOtherProperties(model);
			DataBaseOperator.transfer(model, eventLogArea);
		};
	}

	private ActionListener startListener() {
		return (ActionEvent e) -> {
			selectEventLogPanel();
			TransferModel model = new TransferModel();
			model.setType(StartType.SELECT);
			model.setSourceConn((String) sourceConnComboBox.getSelectedItem());
			model.setTargetConn((String) targetConnComboBox.getSelectedItem());
			model.setSourceDb((String) sourceDbComboBox.getSelectedItem());
			model.setTargetDb((String) targetDbComboBox.getSelectedItem());
			Enumeration    children = tableTreeRoot.children();
			List<DasTable> tables   = new ArrayList<>();
			while (children.hasMoreElements()) {
				CheckedTreeNode treeNode = (CheckedTreeNode) children.nextElement();
				if (treeNode.isChecked() && treeNode.isEnabled()) {
					DasTable table = (DasTable) treeNode.getUserObject();
					tables.add(table);
				}
			}
			model.setTables(tables);
			setModelOtherProperties(model);
			DataBaseOperator.transfer(model, eventLogArea);
		};
	}

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

	private ActionListener insertRecordsCheckboxListener() {
		List<JCheckBox> insertRecordsJCheckboxes = getInsertRecordsJCheckbox();
		insertRecordsJCheckboxes.add(insertRecordsCheckbox);
		return (ActionEvent e) -> setCheckboxChecked(isChecked(e), insertRecordsJCheckboxes);
	}

	private ActionListener createTablesCheckboxListener() {
		List<JCheckBox> createTablesJCheckboxes = getCreateTablesJCheckbox();
		createTablesJCheckboxes.add(createTablesCheckBox);
		return (ActionEvent e) -> setCheckboxChecked(isChecked(e), createTablesJCheckboxes);
	}

	private void addJCheckboxActionListener(List<JCheckBox> checkBoxes, ActionListener listener) {
		checkBoxes.forEach(checkbox -> checkbox.addActionListener(listener));
	}

	private ActionListener insertRecordsAllSelectedListener() {
		return (ActionEvent e) -> {
			List<JCheckBox> insertRecordsJCheckboxes = getInsertRecordsJCheckbox();
			if (isAnyChecked(insertRecordsJCheckboxes)) {
				insertRecordsCheckbox.setSelected(true);
			} else if (isAllUnChecked(insertRecordsJCheckboxes)) {
				insertRecordsCheckbox.setSelected(false);
			}
		};
	}

	private ActionListener createTablesAllSelectedListener() {
		return (ActionEvent e) -> {
			List<JCheckBox> createTablesJCheckboxes = getCreateTablesJCheckbox();
			if (isAnyChecked(createTablesJCheckboxes)) {
				createTablesCheckBox.setSelected(true);
			} else if (isAllUnChecked(createTablesJCheckboxes)) {
				createTablesCheckBox.setSelected(false);
			}
		};
	}

	private List<JCheckBox> getInsertRecordsJCheckbox() {
		return new ArrayList<>(Arrays.asList(lockTargetTablesCheckbox, useTransactionCheckbox, useCompleteInsertStatementsCheckbox,
			useExtendedInsertStatementsCheckbox, useDelayedInsertStatementsCheckbox, useBLOBCheckbox));
	}

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

	private void setCheckboxChecked(boolean isChecked, List<JCheckBox> checkBoxes) {
		checkBoxes.forEach(checkBox -> checkBox.setSelected(isChecked));
	}

	private boolean isChecked(ActionEvent e) {
		boolean isChecked = true;
		if (!((JCheckBox) e.getSource()).isSelected()) {
			isChecked = false;
		}
		return isChecked;
	}

	private void addCheckboxTreeListener(CheckboxTree checkboxTree, CheckedTreeNode root) {
		checkboxTree.addCheckboxTreeListener(new CheckboxTreeListener() {
			@Override
			public void nodeStateChanged(@NotNull CheckedTreeNode node) {
				if (!node.isRoot()) {
					boolean checked = node.isChecked();
					if (checked) tableTreeSelectionCount++;
					else tableTreeSelectionCount--;
					if (tableTreeSelectionCount < 0)
						tableTreeSelectionCount = 0;
					int rowCount = root.getChildCount();
					if (tableTreeSelectionCount > rowCount)
						tableTreeSelectionCount = rowCount;
					root.setUserObject(UiUtil.getTableRootNodeText(tableTreeSelectionCount, rowCount));
					checkboxTree.updateUI();
				}
			}
		});
	}

	private void addTables2Tree(CheckboxTree checkboxTree) {
		String db = (String) sourceDbComboBox.getSelectedItem();
		if (StringUtils.isNotEmpty(db)) {
			List<? extends DasTable> tables = DataSourceCache.getTables(db);
			tableTreeSelectionCount = tables.size();
			tableTreeRoot.setUserObject(UiUtil.getTableRootNodeText(tables.size(), tables.size()));
			UiUtil.addTables(tables, tableTreeRoot);
			checkboxTree.updateUI();
		}
	}

	private void addConnComboBoxItem() {
		DataSourceCache.getDataSources().forEach(dataSource -> {
			sourceConnComboBox.addItem(dataSource.getName());
			targetConnComboBox.addItem(dataSource.getName());
		});
	}

	private void addDbComboBoxItem(JComboBox<String> connComboBox, JComboBox<String> dbComboBox) {
		String sourceSelectConn = (String) connComboBox.getSelectedItem();
		if (StringUtils.isNotEmpty(sourceSelectConn)) {
			JBIterable<? extends DasNamespace> schemas = DataSourceCache.getSchemas(sourceSelectConn);
			GlobalUtil.nonNullConsumer(schemas, schema -> {
				schema.forEach(s -> dbComboBox.addItem(s.getName()));
			});
		}
	}

	private void selectEventLogPanel() {
		optionsTabbedPane.setSelectedIndex(optionsTabbedPane.getTabCount() - 1);
	}

	private void onOK() {
		String selectedItem  = (String) sourceConnComboBox.getSelectedItem();
		Object selectedItem1 = targetConnComboBox.getSelectedItem();
		Messages.showMessageDialog("source: " + selectedItem + ", target: " + selectedItem1, "Connection", Messages.getInformationIcon());
//		dispose();
	}

	private void onCancel() {
		dispose();
	}

	public static DataTransferDialogWrapper getInstance(Project project) {
		return new DataTransferDialogWrapper(project);
	}

}
