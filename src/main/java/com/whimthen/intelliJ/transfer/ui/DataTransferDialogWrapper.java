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
import com.whimthen.intelliJ.transfer.utils.GlobalUtil;
import com.whimthen.intelliJ.transfer.utils.UiUtil;
import icons.DatabaseIcons;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class DataTransferDialogWrapper extends JDialog {

	private JPanel            contentPane;
	private JPanel            optionsPanel;
	private JPanel            selectionPane;
	private JPanel            recordOptionsPane;
	private JPanel            tableOptionsPane;
	private JTabbedPane       optionsTabbedPane;
	private JTabbedPane       dataSourceTabbedPane;
	private JScrollPane       dataBaseObjectsPane;
	private JComboBox<String> targetConnComboBox;
	private JComboBox<String> targetDbComboBox;
	private JComboBox<String> sourceConnComboBox;
	private JComboBox<String> sourceDbComboBox;
	private JButton           buttonOK;
	private JButton           buttonCancel;
	private JButton           startButton;
	private JButton           newDataSourceButton;
	private JButton           button1;
	private JButton           button2;
	private JRadioButton      upperCaseRadioButton;
	private JRadioButton      lowerCaseRadioButton;
	private JLabel            tableOptionsLabel;
	private JLabel            recordOptionsLabel;
	private JLabel            sourceHostLabel;
	private JLabel            sourceUserLabel;
	private JLabel            sourcePortLabel;
	private JLabel            sourcePwdLabel;
	private JLabel            sourceDbLabel;
	private JLabel            targetHostLabel;
	private JLabel            targetPortLabel;
	private JLabel            targetUserLabel;
	private JLabel            targetPwdLabel;
	private JLabel            targetDbLabel;
	private JCheckBox         convertObjectNameToCheckBox;
	private JCheckBox         continueOnErrorCheckBox;
	private JCheckBox         lockSourceTablesCheckBox;
	private JCheckBox         createTargetDatabaseIfCheckBox;
	private JCheckBox         useDDLFromSHOWCheckBox;
	private JCheckBox         useSingleTransactionCheckBox;
	private JCheckBox         dropTargetObjectsBeforeCheckBox;
	private JTextField        sourceHostTextField;
	private JTextField        sourcePortTextField;
	private JTextField        sourceUserTextField;
	private JTextField        sourcePwdTextField;
	private JTextField        sourceDbTextField;
	private JTextField        targetHostTextField;
	private JTextField        targetPortTextField;
	private JTextField        targetUserTextField;
	private JTextField        targetPwdTextField;
	private JTextField        targetDbTextField;
	private JTextArea         eventLogText;
	private JProgressBar      progressBar;

	private int tableTreeSelectionCount = 0;

	private DataTransferDialogWrapper(Project project) {
		setTitle("Data Transfer");
		contentPane.setPreferredSize(new Dimension(800, 700));
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		CheckedTreeNode tableTreeRoot     = new CheckedTreeNode();
		CheckboxTree    tableCheckboxTree = UiUtil.createCheckboxTree(UiUtil.getTableRenderer(), tableTreeRoot);
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

		addTables2Tree(tableTreeRoot, tableCheckboxTree);
		sourceDbComboBox.addItemListener(e -> addTables2Tree(tableTreeRoot, tableCheckboxTree));

		addCheckboxTreeListener(tableCheckboxTree, tableTreeRoot);
		tableCheckboxTree.setRootVisible(true);
		tableCheckboxTree.setEnabled(true);
		tableCheckboxTree.setShowsRootHandles(true);
		tableCheckboxTree.setVisible(true);
		dataBaseObjectsPane.setViewportView(tableCheckboxTree);
		UiUtil.setJScrollVerticalBar(dataBaseObjectsPane);

		newDataSourceButton.setIcon(DatabaseIcons.Dbms);
		newDataSourceButton.addActionListener(e -> UiUtil.showAddDataSourceDialog(project, sourceConnComboBox));

		Color color = new Color(48, 147, 253, 100);
		tableOptionsLabel.setBackground(new JBColor(color, color));

		tableOptionsPane.add(UiUtil.createTableOptionsCheckboxTree());
		recordOptionsPane.add(UiUtil.createRecordOptionsCheckboxTree());

		convertObjectNameToCheckBox.addActionListener(e -> {
			boolean isEnable = false;
			if (((JCheckBox) e.getSource()).isSelected()) {
				isEnable = true;
			}
			lowerCaseRadioButton.setEnabled(isEnable);
			upperCaseRadioButton.setEnabled(isEnable);
		});

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

	private void addTables2Tree(CheckedTreeNode root, CheckboxTree checkboxTree) {
		String db = (String) sourceDbComboBox.getSelectedItem();
		if (StringUtils.isNotEmpty(db)) {
			List<? extends DasTable> tables = DataSourceCache.getTables(db);
			tableTreeSelectionCount = tables.size();
			root.setUserObject(UiUtil.getTableRootNodeText(tables.size(), tables.size()));
			UiUtil.addTables(tables, root);
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

	private void onOK() {
		// add your code here
		optionsTabbedPane.setSelectedIndex(optionsTabbedPane.getTabCount() - 1);
		String selectedItem  = (String) sourceConnComboBox.getSelectedItem();
		Object selectedItem1 = targetConnComboBox.getSelectedItem();
		Messages.showMessageDialog("source: " + selectedItem + ", target: " + selectedItem1, "Connection", Messages.getInformationIcon());
//		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	public static DataTransferDialogWrapper getInstance(Project project) {
		return new DataTransferDialogWrapper(project);
	}

}
