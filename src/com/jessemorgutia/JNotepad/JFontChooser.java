package com.jessemorgutia.JNotepad;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class JFontChooser extends JFrame implements ListSelectionListener,
		ActionListener {

	JLabel sample;
	JFrame frame;
	String[] allFonts;
	JList<String> fontList;
	String name;
	int style;
	int size;
	String[] sty = { "Plain", "Italic", "Bold", "BoldItalic" };
	Integer[] sizearr = { 8, 10, 12, 14, 18, 20, 24 };
	JComboBox<String> fontStyles;
	JComboBox<Integer> fontSizes;
	JTextArea pad;

	public JFontChooser(JTextArea pad) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
		}
		this.pad = pad;
		frame = new JFrame();
		frame.setTitle("View Fonts");
		frame.setLayout(new GridLayout(2, 1));
		frame.setSize(500, 400);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);

		fontStyles = new JComboBox<String>(sty);
		fontSizes = new JComboBox<Integer>(sizearr);
		allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
		fontList = new JList<String>(allFonts);
		fontList.addListSelectionListener(this);
		fontStyles.addActionListener(this);
		fontSizes.addActionListener(this);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);

		JScrollPane fonts = new JScrollPane(fontList);
		fonts.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);

		JPanel panel = new JPanel();
		JPanel panel2 = new JPanel(new GridLayout(3, 1));
		JPanel panel3 = new JPanel();

		panel.add(fonts);
		panel.add(fontStyles);
		panel.add(fontSizes);

		sample = new JLabel("The quick brown fox jumps over the lazy dog"
				+ " 0123456789", SwingConstants.CENTER);
		sample.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		panel2.add(sample);
		// panel2.setBackground(Color.red);
		panel2.add(panel3);

		panel3.add(ok);
		panel3.add(cancel);

		frame.add(panel);
		frame.add(panel2);
		frame.setVisible(false);
	}

	public void valueChanged(ListSelectionEvent e) {
		changedValue();
	}

	void setDefaultSize(int i) {
		this.size = i;
		fontSizes.setSelectedItem(i);

	}

	void setDefaultStyle(int style) {
		this.style = style;
		fontStyles.setSelectedItem(style);

	}

	void setDefaultName(String font) {
		for (int i = 0; i < allFonts.length; i++) {
			if (font.equals(allFonts[i])) {
				name = font;
			} else {
				name = "Arial";
			}
			fontList.setSelectedValue(font, true);

		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand() == "Ok") {
			pad.setFont(new Font(name, style, size));
			frame.dispose();
			changedValue();
		}
		else {
			frame.dispose();
		}
		
	}

	public void changedValue() {
		name = (String) fontList.getSelectedValue();
		switch ((String) fontStyles.getSelectedItem()) {
		case "Bold":
			style = Font.BOLD;
			break;
		case "Plain":
			style = Font.PLAIN;
			break;
		case "Italic":
			style = Font.ITALIC;
			break;
		case "BoldItalic":
			style = Font.BOLD + Font.ITALIC;
			break;
		default:
			style = Font.PLAIN;
			break;

		}
		size = ((Integer) fontSizes.getSelectedItem()).intValue();
		sample.setFont(new Font(name, style, size));
	}
}