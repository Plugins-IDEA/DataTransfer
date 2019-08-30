package com.whimthen.intelliJ.transfer.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.uiDesigner.core.AbstractLayout;
import com.intellij.util.ui.GridBag;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.whimthen.intelliJ.transfer.utils.PasswordUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.Vector;

/**
 * @author whimthen
 * @version 1.0.0
 */
public class TransferDialogWrapper extends DialogWrapper {

	public TransferDialogWrapper() {
		super(true);
		init();
		setTitle("Data Transfer");
	}

	private JPanel panel = new JPanel(new GridBagLayout());
	private JPanel jPanel = new JPanel();
	private JTextField textField = new JTextField();
	private JTextField userName = new JTextField();
	private JPasswordField password = new JPasswordField();

	@Nullable
	@Override
	protected JComponent createCenterPanel() {
		GridBag gb = new GridBag()
				.setDefaultInsets(JBUI.insets(0, 0, AbstractLayout.DEFAULT_VGAP, AbstractLayout.DEFAULT_HGAP))
				.setDefaultWeightX(1.0)
				.setDefaultFill(GridBagConstraints.HORIZONTAL);

		panel.setPreferredSize(new Dimension(400, 200));
		panel.setMinimumSize(new Dimension(400, 200));
		panel.setMaximumSize(new Dimension(500, 200));

		JComponent source = label("Source");
		source.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		source.setForeground(new JBColor(new Color(38, 174, 255), new Color(38, 174, 255)));
		JComponent target = label("Target");
		target.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
		target.setForeground(new JBColor(new Color(38, 174, 255), new Color(38, 174, 255)));
		panel.add(source, gb.nextLine().next().setColumn(0).setLine(0));
		panel.add(target, gb.next().setColumn(1).setLine(0));

		JComboBox<String> jComboBox = new JComboBox<String>(new Vector<String>(Arrays.asList("a", "b", "c")));
		panel.add(jComboBox);


		panel.add(label("mode"), gb.nextLine().next().setColumn(0).setLine(1));
		panel.add(textField, gb.next().setColumn(1));
		panel.add(label("userName"), gb.nextLine().next().weightx(0.1));
		panel.add(userName, gb.next());
		panel.add(label("password"), gb.nextLine().next().weightx(0.1));
		panel.add(password, gb.next());

		return panel;
	}

	@Override
	protected void doOKAction() {
		String text = this.textField.getText();
		String userName = this.userName.getText();
		Messages.showMessageDialog("text = " + text + ", userName = " +
						userName + ", password = " + PasswordUtil.password(this.password),
				"DoOkAction Result", Messages.getWarningIcon());
		dispose();
	}

	@Override
	public void doCancelAction() {
		dispose();
	}

	private JComponent label(String text) {
		JBLabel label = new JBLabel(text);
		label.setComponentStyle(UIUtil.ComponentStyle.REGULAR);
		label.setFontColor(UIUtil.FontColor.NORMAL);
		label.setBorder(JBUI.Borders.empty(0, 5, 2, 0));
		return label;
	}

	public static void main(String[] args) {
		new TransferDialogWrapper().show();
	}

}
