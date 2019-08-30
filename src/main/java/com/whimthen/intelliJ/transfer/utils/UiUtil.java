package com.whimthen.intelliJ.transfer.utils;

import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.dataSource.DatabaseDriverImpl;
import com.intellij.database.dataSource.LocalDataSource;
import com.intellij.database.dataSource.url.template.UrlTemplate;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.view.ui.DataSourceManagerDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.tree.TreeUtil;
import icons.DatabaseIcons;

import javax.swing.JComboBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class UiUtil {

	public static final String selectConnection = "-- Please select Connection --";
	public static final String selectDataBase   = "-- Please select DataBase --";

	public static boolean isSelectText(String item) {
		return selectConnection.equals(item) || selectDataBase.equals(item);
	}

	public static CheckboxTree createRecordOptionsCheckboxTree() {
		CheckedTreeNode recordOptionsRoot         = new CheckedTreeNode("Insert records");
		CheckboxTree    recordOptionsCheckboxTree = createCheckboxTree(getStringRenderer(), recordOptionsRoot);
		addCheckedNode2RecordOptionsPaneRoot(recordOptionsRoot);
		recordOptionsCheckboxTree.setRootVisible(true);
		recordOptionsCheckboxTree.setEnabled(true);
		recordOptionsCheckboxTree.setVisible(true);
		recordOptionsCheckboxTree.setShowsRootHandles(false);
		TreeUtil.expandAll(recordOptionsCheckboxTree);
		return recordOptionsCheckboxTree;
	}

	public static CheckboxTree createTableOptionsCheckboxTree() {
		CheckedTreeNode tableOptionsRoot         = new CheckedTreeNode("Create tables");
		CheckboxTree    tableOptionsCheckboxTree = createCheckboxTree(getStringRenderer(), tableOptionsRoot);
		addCheckedNode2TableOptionsPaneRoot(tableOptionsRoot);
		tableOptionsCheckboxTree.setRootVisible(true);
		tableOptionsCheckboxTree.setEnabled(true);
		tableOptionsCheckboxTree.setVisible(true);
		tableOptionsCheckboxTree.setShowsRootHandles(false);
		TreeUtil.expandAll(tableOptionsCheckboxTree);
		tableOptionsCheckboxTree.setBackground(new JBColor(getOpacity0(), getOpacity0()));
		return tableOptionsCheckboxTree;
	}

	public static CheckboxTree createCheckboxTree(CheckboxTree.CheckboxTreeCellRenderer renderer, CheckedTreeNode root) {
		CheckboxTree checkboxTree = new CheckboxTree(renderer, root,
			new CheckboxTreeBase.CheckPolicy(true,
				true, true, true));
		checkboxTree.setBackground(new JBColor(new Color(0XECECEC), new Color(0XECECEC)));
		return checkboxTree;
	}

	public static void addCheckedNode2RecordOptionsPaneRoot(CheckedTreeNode tableOptionsRoot) {
		addStringCheckboxNode2Root("Lock target tables", tableOptionsRoot, false);
		addStringCheckboxNode2Root("Use transaction", tableOptionsRoot);
		addStringCheckboxNode2Root("Use complete insert statements", tableOptionsRoot, false);
		addStringCheckboxNode2Root("Use extended insert statements", tableOptionsRoot);
		addStringCheckboxNode2Root("Use delayed insert statements", tableOptionsRoot, false);
		addStringCheckboxNode2Root("Use hexadecimal format for BLOB", tableOptionsRoot);
	}

	public static void addCheckedNode2TableOptionsPaneRoot(CheckedTreeNode tableOptionsRoot) {
		addStringCheckboxNode2Root("Include indexes", tableOptionsRoot);
		addStringCheckboxNode2Root("Include foreign key constraints", tableOptionsRoot);
		addStringCheckboxNode2Root("Include engine/table type", tableOptionsRoot);
		addStringCheckboxNode2Root("Include character set", tableOptionsRoot);
		addStringCheckboxNode2Root("Include auto increment", tableOptionsRoot);
		addStringCheckboxNode2Root("Include other table options", tableOptionsRoot, false);
	}

	public static Color getOpacity0() {
		return new Color(48, 147, 253, 0);
	}

	public static void setJScrollVerticalBar(JScrollPane jScrollBar) {
		jScrollBar.setBorder(JBUI.Borders.empty());
		jScrollBar.setVerticalScrollBar(getScrollBar());
	}

	public static void addStringCheckboxNode2Root(String text, CheckedTreeNode root) {
		CheckedTreeNode newChild = new CheckedTreeNode(text);
		newChild.setChecked(true);
		newChild.setEnabled(true);
		root.add(newChild);
	}

	public static void addStringCheckboxNode2Root(String text, CheckedTreeNode root, boolean isChecked) {
		CheckedTreeNode newChild = new CheckedTreeNode(text);
		newChild.setChecked(isChecked);
		newChild.setEnabled(true);
		root.add(newChild);
	}

	public static CheckboxTree.CheckboxTreeCellRenderer getStringRenderer() {
		return new CheckboxTree.CheckboxTreeCellRenderer() {
			@Override
			public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.customizeRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
				if (!(value instanceof DefaultMutableTreeNode)) return;
				value = ((DefaultMutableTreeNode) value).getUserObject();

				if (value instanceof CharSequence) {
					getTextRenderer().append(value.toString());
				}
			}
		};
	}

	public static CheckboxTree.CheckboxTreeCellRenderer getTableRenderer() {
		return new CheckboxTree.CheckboxTreeCellRenderer() {
			@Override
			public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.customizeRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
				if (!(value instanceof DefaultMutableTreeNode)) return;
				value = ((DefaultMutableTreeNode) value).getUserObject();

				if (value instanceof CharSequence) {
					getTextRenderer().setIcon(DatabaseIcons.Schema);
					getTextRenderer().append(" ").append(value.toString());
				} else if (value instanceof DasTable) {
					getTextRenderer().setIcon(DatabaseIcons.Table);
					String comment = StringUtilRt.notNullize(((DasTable) value).getComment());
					getTextRenderer().append(((DasTable) value).getName()).append("    ")
									 .append(comment, SimpleTextAttributes.GRAY_ATTRIBUTES);
				}
			}
		};
	}

	public static String getTableRootNodeText(int selectionCount, int allCount) {
		return "Tables (" + selectionCount + " of " + allCount + " tables)";
	}

	public static JScrollBar getScrollBar() {
		JScrollBar jScrollBar = new JScrollBar();
		// 设置背景颜色
		jScrollBar.setBackground(new JBColor(new Color(0XECECEC), new Color(0XECECEC)));
		// 设置滚轮速度
		jScrollBar.setUnitIncrement(5);
		// 设置滚动条宽度
		jScrollBar.setPreferredSize(new Dimension(10, 0));
		return jScrollBar;
	}

	public static void addTables(List<? extends DasTable> tables, CheckedTreeNode root) {
		root.removeAllChildren();
		tables.forEach(table -> {
			CheckedTreeNode newChild = new CheckedTreeNode(table);
			newChild.setChecked(true);
			newChild.setEnabled(true);
			root.add(newChild);
		});
	}

	public static void showAddDataSourceDialog(Project project) {
		DbPsiFacade        facade   = DbPsiFacade.getInstance(project);
		DataSourceRegistry registry = new DataSourceRegistry(project);
		boolean            b        = DataSourceManagerDialog.showDialog(facade, registry);
		System.out.println(b);
	}

	public static void showAddDataSourceDialog(Project project, JComboBox comboBox) {
		DbPsiFacade        facade   = DbPsiFacade.getInstance(project);
//		DataSourceRegistry registry = new DataSourceRegistry(project);
//		registry.retainNewOnly();
//		new DatabaseViewActions.DataSourceFactory() {
//			@Override
//			public void create(@NotNull DbPsiFacade dbPsiFacade, @NotNull DataSourceManager<LocalDataSource> dataSourceManager, @NotNull LocalDataSource localDataSource) {
//
//			}
//		};
//		DatabaseCredentials credentialsStore = registry.getCredentialsStore();

		UrlTemplate        template        = new UrlTemplate("default", "jdbc:mysql://{host::localhost}?[:{port::3306}][/{database}?][\\?&lt;&amp;,user={user},password={password},{:identifier}={:identifier}&gt;]");
		DatabaseDriverImpl driver = new DatabaseDriverImpl(null, "MySQL for 5.1", "com.mysql.jdbc.Driver", template);
		driver.setSqlDialect("MySQL");
//		LocalDataSource    root   = LocalDataSource.create("MariaDB", "org.mariadb.jdbc.Driver", "jdbc:mariadb://localhost:3306", "root");
		LocalDataSource    localDataSource = LocalDataSource.fromDriver(driver, "jdbc:mysql://localhost:3306", false);
//		DataSourceManagerDialog.showDialog(project, localDataSource, null);

		DataSourceRegistry registry = new DataSourceRegistry(project);
		DataSourceManagerDialog.showDialog(facade, registry);


//		DataSourceManagerDialog.showDialog(facade, null, credentialsStore);
//		String       selectedItem = (String) comboBox.getSelectedItem();
//		DbDataSource dataSource   = null;
//		if (StringUtils.isNotNullOrEmpty(selectedItem)) {
//			dataSource = DataSourceCache.get(selectedItem);
//		}
//		if (Objects.nonNull(dataSource)) {
//			DbDataSource dataSource1 = dataSource.getDataSource();
//			DataSourceManagerDialog.showDialog(project, dataSource1, null);
//		} else {
//			showAddDataSourceDialog(project);
//		}
	}

}
