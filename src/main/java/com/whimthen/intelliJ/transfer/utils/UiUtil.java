package com.whimthen.intelliJ.transfer.utils;

import com.intellij.database.autoconfig.DataSourceRegistry;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.view.ui.DataSourceManagerDialog;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckboxTreeBase;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Consumer;
import com.intellij.util.ui.JBUI;
import icons.DatabaseIcons;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class UiUtil {

	public static final String SELECT_CONNECTION = "-- Please select Connection --";
	public static final String SELECT_DATA_BASE  = "-- Please select DataBase --";
	public static final String CLICK_TO_SEE_MORE = "double click to show more...";

	private static List<? extends DasTable> tableList;
	private static final int tableLimit = 50;
	private static int tableSkip = 0;

	/**
	 * 是否是选择提示语
	 *
	 * @param item 选择的文本
	 * @return true | false
	 */
	public static boolean isSelectText(String item) {
		return SELECT_CONNECTION.equals(item) || SELECT_DATA_BASE.equals(item);
	}

	public static void setButtonEnable(List<JButton> buttons, boolean isEnable) {
		buttons.forEach(button -> button.setEnabled(isEnable));
	}

	/**
	 * 判断至少有一个复选框为选中状态
	 *
	 * @param checkBoxes 复选框集合
	 * @return true | false
	 */
	public static boolean isAnyChecked(List<JCheckBox> checkBoxes) {
		return checkBoxes.stream().anyMatch(AbstractButton::isSelected);
	}

	/**
	 * 是否所有的复选框都没选中
	 *
	 * @param checkBoxes 复选框集合
	 * @return true | false
	 */
	public static boolean isAllUnChecked(List<JCheckBox> checkBoxes) {
		return checkBoxes.stream().noneMatch(AbstractButton::isSelected);
	}

	/**
	 * 设置所有的复选框选中状态
	 *
	 * @param isChecked 是否选中
	 * @param checkBoxes 复选框集合
	 */
	public static void setCheckboxChecked(boolean isChecked, List<JCheckBox> checkBoxes) {
		checkBoxes.forEach(checkBox -> checkBox.setSelected(isChecked));
	}

	/**
	 * 判断复选框是否选中
	 *
	 * @param e 事件
	 * @return true | false
	 */
	public static boolean isChecked(ActionEvent e) {
		boolean isChecked = true;
		if (!((JCheckBox) e.getSource()).isSelected()) {
			isChecked = false;
		}
		return isChecked;
	}

	/**
	 * 创建带有复选框的树
	 *
	 * @param renderer 树的渲染
	 * @param root 根节点
	 * @return 带有复选框的树
	 */
	public static CheckboxTree createCheckboxTree(CheckboxTree.CheckboxTreeCellRenderer renderer, CheckedTreeNode root) {
		CheckboxTree checkboxTree = new CheckboxTree(renderer, root,
			new CheckboxTreeBase.CheckPolicy(true,
				true, true, true));
		checkboxTree.setBackground(new JBColor(new Color(0XECECEC), new Color(0XECECEC)));
		return checkboxTree;
	}

	/**
	 * 将滚动条滑动到最下面, JScrollPane中包含JTextArea
	 *
	 * @param textArea JTextArea
	 */
	public static void scrollDown(JTextArea textArea) {
		textArea.selectAll();
		if (textArea.getSelectedText() != null) {
			textArea.setCaretPosition(textArea.getSelectedText().length());
			textArea.requestFocus();
		}
	}

	/**
	 * 获取透明色
	 *
	 * @return 透明色
	 */
	public static Color getOpacity0() {
		return new Color(255, 255, 255, 0);
	}

	/**
	 * 设置滚动条的样式
	 * 边框为0, 横向纵向
	 *
	 * @param scrollPane 滚动面板
	 */
	public static void setJScrollBar(JScrollPane scrollPane) {
		scrollPane.setBorder(JBUI.Borders.empty());
		scrollPane.setVerticalScrollBar(getScrollBar());
		scrollPane.setHorizontalScrollBar(getScrollBar());
	}

	/**
	 * 添加子节点到带有复选框的树根节点
	 * 默认选中
	 *
	 * @param text 显示文本
	 * @param root 根节点
	 */
	public static void addStringCheckboxNode2Root(String text, CheckedTreeNode root) {
		CheckedTreeNode newChild = new CheckedTreeNode(text);
		newChild.setChecked(true);
		newChild.setEnabled(true);
		root.add(newChild);
	}

	/**
	 * 添加子节点到带有复选框的树根节点
	 *
	 * @param text 显示内容
	 * @param root 根节点
	 * @param isChecked 是否选中
	 */
	public static void addStringCheckboxNode2Root(String text, CheckedTreeNode root, boolean isChecked) {
		CheckedTreeNode newChild = new CheckedTreeNode(text);
		newChild.setChecked(isChecked);
		newChild.setEnabled(true);
		root.add(newChild);
	}

	/**
	 * 获取表复选框树的渲染器
	 *
	 * @return 渲染器
	 */
	public static CheckboxTree.CheckboxTreeCellRenderer getTableRenderer() {
		return new CheckboxTree.CheckboxTreeCellRenderer() {
			@Override
			public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.customizeRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
				if (!(value instanceof DefaultMutableTreeNode)) return;
				value = ((DefaultMutableTreeNode) value).getUserObject();

				if (value instanceof CharSequence) {
					if (CLICK_TO_SEE_MORE.equals(value)) {
						getTextRenderer().setIcon(AllIcons.Actions.Lightning);
						getTextRenderer().append(" " + value, SimpleTextAttributes.GRAY_ATTRIBUTES);
					} else {
						getTextRenderer().setIcon(DatabaseIcons.Schema);
						getTextRenderer().append(" ").append(value.toString());
					}
				} else if (value instanceof DasTable) {
					getTextRenderer().setIcon(DatabaseIcons.Table);
					String comment = StringUtilRt.notNullize(((DasTable) value).getComment());
					getTextRenderer().append(((DasTable) value).getName()).append("    ")
									 .append(comment, SimpleTextAttributes.GRAY_ATTRIBUTES);
				}
			}
		};
	}

	/**
	 * 构造根节点显示文本
	 *
//	 * @param selectionCount 当前选中多少条表
	 * @param allCount 子节点个数
	 * @return 显示文本
	 */
	public static String getTableRootNodeText(int allCount) {
		return "Tables (" + allCount + " tables)";
	}

	/**
	 * 创建滚动条
	 *
	 * @return 滚动条
	 */
	public static JScrollBar getScrollBar() {
		JScrollBar jScrollBar = new JScrollBar();
		// 设置背景颜色
		jScrollBar.setBackground(new JBColor(new Color(0XECECEC), new Color(0XECECEC)));
		// 设置滚轮速度
		jScrollBar.setUnitIncrement(3);
		// 设置滚动条宽度
		jScrollBar.setPreferredSize(new Dimension(10, 10));
		return jScrollBar;
	}

	/**
	 * 向根节点添加子节点表
	 *
	 * @param tables 表集合
	 * @param root 根节点
	 */
	public static void addTables(List<? extends DasTable> tables, CheckedTreeNode root, boolean isRemove) {
		if (isRemove)
			root.removeAllChildren();
		else
			root.remove(root.getChildCount() - 1);
		tableList = tables;
		tables.stream().skip(tableSkip).limit(tableLimit).forEach(table -> {
			CheckedTreeNode newChild = new CheckedTreeNode(table);
			newChild.setChecked(true);
			newChild.setEnabled(true);
			root.add(newChild);
		});
		if (tables.size() > tableLimit + tableSkip) {
			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(CLICK_TO_SEE_MORE, false);
			root.add(treeNode);
		}
	}

	/**
	 * 重置复选框下面立标的数量
	 */
	public static void resetTableSkip() {
		tableSkip = 0;
	}

	/**
	 * 添加鼠标点击事件
	 *
	 * @param checkboxTree 复选框树
	 * @param root 根节点
	 */
	public static void addCheckboxClickShowMoreListener(CheckboxTree checkboxTree, CheckedTreeNode root) {
		UiUtil.addCheckboxTreeMouseDoubleClickListener(checkboxTree, e -> {
			Object lastPathComponent = ((CheckboxTree) e.getSource()).getSelectionModel().getSelectionPath().getLastPathComponent();
			if (Objects.nonNull(lastPathComponent) && lastPathComponent instanceof DefaultMutableTreeNode) {
				Object userObject = ((DefaultMutableTreeNode) lastPathComponent).getUserObject();
				if (UiUtil.CLICK_TO_SEE_MORE.equals(userObject)) {
					tableSkip += tableLimit;
					addTables(tableList, root, false);
				}
			}
		});
	}

	/**
	 * 添加复选框树的鼠标点击事件
	 *
	 * @param checkboxTree 复选框树
	 * @param consumer 消费者
	 */
	public static void addCheckboxTreeMouseDoubleClickListener(CheckboxTree checkboxTree, Consumer<MouseEvent> consumer) {
		checkboxTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					consumer.consume(e);
					checkboxTree.updateUI();
				}
			}
		});
	}

	/**
	 * 弹出添加数据源的对话框
	 * 默认使用MariaDb
	 *
	 * @param project 当前Project
	 */
	public static void showAddDataSourceDialog(Project project) {
		DbPsiFacade        facade   = DbPsiFacade.getInstance(project);
		DataSourceRegistry registry = new DataSourceRegistry(project);
		registry.setImportedFlag(false);
		// org.mariadb.jdbc.Driver jdbc:mariadb://localhost:3306 com.mysql.jdbc.Driver jdbc:mysql://localhost:3306
		registry.getBuilder().withDriverClass("org.mariadb.jdbc.Driver").withUrl("jdbc:mariadb://localhost:3306")
				.withUser("root").withDriverProperty("autoReconnect", "true").withDriverProperty("zeroDateTimeBehavior", "convertToNull")
				.withDriverProperty("tinyInt1isBit", "false").withDriverProperty("characterEncoding", "utf8")
				.withDriverProperty("characterSetResults", "utf8").withDriverProperty("yearIsDateType", "false").commit();
		DataSourceManagerDialog.showDialog(facade, registry);
		// TODO 刷新DataSource缓存
	}

}
