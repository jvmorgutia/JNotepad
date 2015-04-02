package com.jessemorgutia.JNotepad;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class JNotepad implements ActionListener, Printable {

	JFrame frame;
	JTextArea pad;
	JScrollPane notepad;
	JMenuBar menuBar;
	JLabel caretCount;
	String originalPad;
	JFontChooser fontChooser;
	JDialog finder;
	String finderString;
	// FOR older java support
	JTextField field;
	JRadioButton dn_b;
	JCheckBox m_case;
	JFileChooser chooser;

	// GLOBAL MENU ITEMS
	JCheckBoxMenuItem menu_statusbar;
	JMenuItem menu_goto;
	JMenuItem menu_copy;
	JMenuItem menu_cut;
	JMenuItem menu_delete;
	JMenuItem menu_paste;
	JPopupMenu popup;

	ImageIcon img;
	File image = new File("JNotepad.png");
	String filename;
	File file;
	boolean statusBarEnabled;
	PrinterJob printer;
	PageFormat pageFormat;

	public JNotepad(String filepath) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
		}

		pad = new JTextArea();
		finderString = "";
		pageFormat = new PageFormat();
		printer = PrinterJob.getPrinterJob();
		menuBar = new JMenuBar();
		fontChooser = new JFontChooser(pad);
		fontChooser.setDefaultName("Ariel");
		fontChooser.setDefaultSize(12);
		fontChooser.setDefaultStyle(Font.PLAIN);

		// Setting up the status bar (Ln and Col Counter)
		JPanel statusBar = new JPanel(new BorderLayout());
		JPanel statusPanel = new JPanel(new BorderLayout());
		caretCount = new JLabel("");
		statusBarEnabled = false;
		caretCount.setVisible(statusBarEnabled);
		caretCount.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
		statusPanel.add(caretCount);
		statusPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0,
				Color.LIGHT_GRAY));
		statusBar.add(statusPanel, BorderLayout.EAST);
		pad.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				updateCaretLocation();
				if (pad.getSelectedText() == null) {
					menu_copy.setEnabled(false);
					menu_cut.setEnabled(false);
					menu_delete.setEnabled(false);
				} else {
					menu_copy.setEnabled(true);
					menu_cut.setEnabled(true);
					menu_delete.setEnabled(true);
				}

			}
		});

		// Initialize the frame
		frame = new JFrame(filename + " - Notepad");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			public void windowClosing(WindowEvent we) {
				confirmClose(true);
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowOpened(WindowEvent e) {
			}

		});
		frame.setSize(900, 600);
		frame.setLocationRelativeTo(null);
		notepad = new JScrollPane(pad,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		initializeMenu();
		// Load icon image
		if (image.exists()) {
			img = new ImageIcon("JNotepad.png");
			frame.setIconImage(img.getImage());
			fontChooser.frame.setIconImage(img.getImage());
			System.out.println("Icon Image loaded successfully");
		} else {
			System.out.println("Icon Image not found");
		}

		frame.setLayout(new BorderLayout());
		frame.add(notepad, BorderLayout.CENTER);
		frame.add(statusBar, BorderLayout.SOUTH);
		frame.setJMenuBar(menuBar);
		// Initialize Fields
		boolean createFrame = true;
		if (filepath.equals("")) {
			filename = "Untitled";
			frame.setTitle(filename + " - Notepad");
			originalPad = "";
		} else {
			file = new File(filepath);
			if (file.isFile())
				filename = file.getName();
			createFrame = openFile(false); // false, does not ask for user input
			frame.setTitle(filename + " - Notepad");
			originalPad = pad.getText();
		}
		if (createFrame)
			frame.setVisible(true);
		else
			frame.dispose();
	}

	protected void updateCaretLocation() {
		int caret = pad.getCaretPosition();
		char[] test = pad.getText().toCharArray();
		int linecount = 1;
		int linecaret = 1;
		for (int i = 0; i < caret; i++) {
			if (test[i] == '\n') {
				linecount++;
				linecaret = 1;
			} else
				linecaret++;
		}
		caretCount.setText("Ln " + linecount + ", Col " + linecaret);
	}

	public void actionPerformed(ActionEvent ae) {
		switch (ae.getActionCommand().replace("...", "").trim()) {
		case "New":
			new JNotepad("");
			break;
		case "Open":
			openFile(true);
			break;
		case "Save":
			if (!pad.getText().equals(originalPad)
					&& !filename.equals("Untitled"))
				saveWork(file.getAbsolutePath());
			else
				saveAs();
			break;
		case "Save As":
			saveAs();
			break;
		case "Page Setup":
			pageFormat = printer.pageDialog(pageFormat);
			break;
		case "Print":
			printer.setPrintable(pad.getPrintable(null, null), pageFormat);
			try {
				if (printer.printDialog())
					printer.print();
			} catch (PrinterException e) {
				System.out.println("PRINTING ERROR");
			}
			break;
		case "Exit":
			confirmClose(true);
			break;
		case "Undo":
			break;
		case "Cut":
			pad.cut();
			break;
		case "Copy":
			pad.copy();
			break;
		case "Paste":
			pad.paste();
			break;
		case "Delete":
			if (pad.getSelectedText() != null)
				pad.setText(pad.getText().replace(pad.getSelectedText(), ""));
			break;
		case "Find":
			finderString = createFinderWindow();
			break;
		case "Find Next":
			if (finderString.equals(""))
				finderString = createFinderWindow();
			else {
				if (dn_b.isSelected() && m_case.isSelected())
					searchDown(pad.getText(), finderString);

				else if (dn_b.isSelected() && !m_case.isSelected())
					searchDown(pad.getText().toLowerCase(),
							finderString.toLowerCase());

				else if (!dn_b.isSelected() && m_case.isSelected())
					searchUp(pad.getText(), finderString);

				else
					searchUp(pad.getText().toLowerCase(),
							finderString.toLowerCase());

			}
			break;
		case "Replace":
			break;
		case "Go To":
			goToDialog();
			break;
		case "Select All":
			pad.selectAll();
			break;
		case "Time/Date":
			String time = new SimpleDateFormat().format(Calendar.getInstance()
					.getTime());

			if (!(pad.getSelectedText() == null))
				pad.setText(pad.getText().replace(pad.getSelectedText(), time));
			else
				pad.append(time);

			break;
		case "Font":
			fontChooser.frame.setVisible(true);
			break;
		case "Status Bar":
			if (!((JCheckBoxMenuItem) ae.getSource()).isSelected()) {
				caretCount.setText(null);
				caretCount.setVisible(false);
				statusBarEnabled = false;
			} else {
				updateCaretLocation();
				caretCount.setVisible(true);
				statusBarEnabled = true;
			}
			break;
		case "View Help":
			break;
		case "About Notepad":
			displayAbout();
			break;

		}

	}

	private String createFinderWindow() {

		field = new JTextField("", 22);
		if (pad.getSelectedText() != null)
			field.setText(pad.getSelectedText());
		finder = new JDialog(frame, "Find");
		finder.setModalityType(Dialog.ModalityType.MODELESS);
		finder.setSize(400, 150);
		finder.setLayout(new BorderLayout());
		finder.setLocationRelativeTo(frame);
		JPanel panel_west = new JPanel(new BorderLayout());
		JPanel buttons_east = new JPanel(new GridLayout(4, 1));
		JPanel fb_panel = new JPanel();
		JPanel cb_panel = new JPanel();
		JPanel panel_west_south = new JPanel(new BorderLayout());
		JPanel dir_panel = new JPanel();
		JPanel find_panel = new JPanel();

		JButton findbutton = new JButton("Find Next");
		JButton cancelbutton = new JButton(" Cancel ");

		JRadioButton up_b = new JRadioButton("Up");
		dn_b = new JRadioButton("Down");
		m_case = new JCheckBox("Match Case");
		ButtonGroup bg = new ButtonGroup();
		bg.add(up_b);
		bg.add(dn_b);
		dn_b.setSelected(true);
		finder.getRootPane().setDefaultButton(findbutton);
		findbutton.setMnemonic(KeyEvent.VK_F);
		m_case.setMnemonic(KeyEvent.VK_C);
		dn_b.setMnemonic(KeyEvent.VK_D);
		up_b.setMnemonic(KeyEvent.VK_U);

		fb_panel.add(findbutton);
		cb_panel.add(cancelbutton);
		buttons_east.add(fb_panel);
		buttons_east.add(cb_panel);

		findbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String s = pad.getText();
				String in = field.getText();

				if (dn_b.isSelected())
					if (m_case.isSelected())
						searchDown(s, in);
					else
						searchDown(s.toLowerCase(), in.toLowerCase());
				else if (m_case.isSelected())
					searchUp(s, in);
				else
					searchUp(s.toLowerCase(), in.toLowerCase());
			}

		});

		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				finder.dispose();
			}
		});

		dir_panel.setBorder(BorderFactory.createTitledBorder("Direction"));
		dir_panel.add(up_b);
		dir_panel.add(dn_b);
		find_panel.add(new JLabel("Find what: "));
		find_panel.add(field);
		panel_west_south.add(dir_panel, BorderLayout.EAST);
		panel_west_south.add(m_case, BorderLayout.WEST);
		panel_west.add(find_panel, BorderLayout.NORTH);
		panel_west.add(panel_west_south, BorderLayout.SOUTH);
		finder.add(panel_west);
		finder.add(buttons_east, BorderLayout.EAST);
		finder.setVisible(true);
		return field.getText();
	}

	private void searchUp(String s, String in) {
		int count;
		if (in != "")
			count = in.length() - 1;
		else {
			JOptionPane
					.showMessageDialog(frame, "Enter a string to search for");
			return;
		}
		try {
			boolean foundString = false;
			for (int i = pad.getCaretPosition() - 1; i > 0; i--) {
				if (s.charAt(i) == in.charAt(count)) {
					count--;
					if (-1 == count) {
						pad.setCaretPosition(i);
						pad.select(i, i + in.length());
						foundString = true;
						break;
					}

				} else {
					count = in.length() - 1;
				}
			}
			if (!foundString) {
				if (in.equals(""))
					JOptionPane.showMessageDialog(frame,
							"Enter a string to search for");
				else
					JOptionPane.showMessageDialog(frame, "Cannot Find " + in);

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(frame, "String not found");
		}

	}

	private void searchDown(String s, String in) {
		int count = 0;
		int maxcount;
		if (in != "")
			maxcount = in.length() - 1;
		else {
			JOptionPane
					.showMessageDialog(frame, "Enter a string to search for");
			return;
		}
		try {
			boolean foundString = false;
			for (int i = pad.getCaretPosition(); i < s.length(); i++) {
				if (s.charAt(i) == in.charAt(count)) {
					count++;
					if (maxcount == count) {
						pad.setCaretPosition(pad.getCaretPosition()
								- (s.length() - i));
						pad.select(i + 1 - maxcount, i + 1);
						foundString = true;
						break;
					}

				} else {
					count = 0;
				}
			}
			if (!foundString) {
				if (in.equals(""))
					JOptionPane.showMessageDialog(frame,
							"Enter a string to search for");
				else
					JOptionPane.showMessageDialog(frame, "Cannot Find \"" + in
							+ "\"");

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(frame, "String not found");
		}
	}

	private boolean openFile(boolean promptUser) {
		File newfile;
		if (!pad.getText().equals(originalPad) && promptUser) {
			confirmClose(false);// DOES NOT terminate program
		}

		if (promptUser) {
			chooser = new JFileChooser();
			FileNameExtensionFilter txt = new FileNameExtensionFilter(
					"Text Documents (*.txt)", "txt");
			FileNameExtensionFilter java = new FileNameExtensionFilter(
					"Java Files (*.java)", "java");
			chooser.setAcceptAllFileFilterUsed(true);
			chooser.setFileFilter(txt);
			chooser.addChoosableFileFilter(java);
			chooser.showOpenDialog(frame);
			if (chooser.getSelectedFile() != null)
				newfile = chooser.getSelectedFile();
			else {
				newfile = new File("FILE_NOT_VALID");
			}
		} else { // IF THE FILE HAS BEEN ENTERED VIA ARGS
			newfile = this.file;
		}

		if (newfile.exists()) { // if file does not exist
			try {
				FileReader read = new FileReader(newfile);
				pad.read(read, newfile.getName());
				originalPad = pad.getText();
				filename = newfile.getName();
				frame.setTitle(filename);
				file = newfile;

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, "ERROR OPENING FILE");
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			if (newfile.getName() != "FILE_NOT_VALID") {
				JOptionPane
						.showMessageDialog(
								frame,
								newfile.getName()
										+ "\nFile not found. \nCheck the file name and try again.");
				if (promptUser)
					openFile(true);
			}

			return false; // does not allow creation of window
		}
	}

	private void goToDialog() {
		String input = JOptionPane.showInputDialog(null, "Line Number:\n",
				"Go To Line", JOptionPane.PLAIN_MESSAGE);
		try {
			int entered = Integer.parseInt(input);
			int linenum = 1;
			boolean validEntry = false;
			char[] text = pad.getText().toCharArray();
			if (entered == 0 || entered == 1) {
				pad.setCaretPosition(0);
				validEntry = true;
			} else {
				for (int i = 1; i < pad.getText().length(); i++) {
					if (text[i] == '\n')
						linenum++;
					if (linenum == entered) {
						pad.setCaretPosition(i + 1);
						validEntry = true;
						break;
					}
				}
			}
			if (!validEntry)
				JOptionPane.showMessageDialog(null,
						"The line number is beyond the total number of lines",
						filename + " - Goto Line", JOptionPane.PLAIN_MESSAGE);

		} catch (NumberFormatException e) {
			if (input != null)
				JOptionPane.showMessageDialog(null, "Invalid line number");
		}

	}

	private void confirmClose(boolean terminateProgram) {
		if (pad.getText().equals(originalPad) && terminateProgram) {
			frame.dispose();
		} else {

			Object[] buttons = { " Save ", "   Don't Save   ", " Cancel " };
			int opt = JOptionPane.showOptionDialog(null,
					"Do you want to save changes to " + filename + "?",
					"Notepad", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[2]);

			switch (opt) {
			case 0:
				if (file == null) {
					saveAs();
				} else {
					saveWork(file.getAbsolutePath());
				}
				if (terminateProgram)
					frame.dispose();
				break;
			case 1:
				if (terminateProgram)
					frame.dispose();
				break;
			default:
				break;
			}
		}

	}

	private void saveWork(String path) {
		try {
			FileWriter saver = new FileWriter(path);
			pad.write(saver);
			saver.close();
			originalPad = pad.getText();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void saveAs() {

		chooser = new JFileChooser();
		FileNameExtensionFilter txt = new FileNameExtensionFilter(
				"Text Documents (*.txt)", "txt");
		FileNameExtensionFilter java = new FileNameExtensionFilter(
				"Java Files (*.java)", "java");
		chooser.setFileFilter(txt);
		chooser.addChoosableFileFilter(java);
		chooser.showSaveDialog(frame);

		chooser.getFileFilter().getDescription();

		if (!(chooser.getSelectedFile() == null)) {
			File newfile = chooser.getSelectedFile();

			if (newfile.exists()) {
				int replace = JOptionPane
						.showConfirmDialog(
								frame,
								newfile.getName()
										+ " already exists.\nDo you want to replace it?",
								"Confirm Save As", JOptionPane.YES_NO_OPTION);
				if (replace == 1)// NO
					saveAs();
				else if (replace == 0) {// YES
					saveWork(newfile.getAbsolutePath());
					filename = newfile.getName();
					frame.setTitle(filename + " - Notepad");
				} else {
					System.out.println("File save was canceled");
				}
			}

			else {
				saveWork(newfile.getAbsolutePath());
				filename = newfile.getName();
				frame.setTitle(filename + " - Notepad");
			}
		}
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		return 0;
	}

	public void displayAbout() {
		JDialog modal = new JDialog(frame, "About", true);
		modal.setLayout(new FlowLayout());
		modal.setSize(400, 100);
		modal.setLocationRelativeTo(frame);
		modal.add(new JLabel(
				" (C) JNotepad release version 0.9 - Jesse Morgutia "));
		modal.setVisible(true);
	}

	// ============INITIALIZE MENU=============
	public void initializeMenu() {
		popup = new JPopupMenu("Pop");
		pad.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					popup.show(e.getComponent(), e.getX(), e.getY());
			}

			public void mouseClicked(MouseEvent arg0) {
			}

			public void mouseEntered(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

		});
		JMenu menu_file = new JMenu("File");
		JMenu menu_edit = new JMenu("Edit");
		JMenu menu_format = new JMenu("Format");
		JMenu menu_view = new JMenu("View");
		JMenu menu_help = new JMenu("Help");

		JMenuItem menu_new = new JMenuItem("New", KeyEvent.VK_N);
		JMenuItem menu_open = new JMenuItem("Open...", KeyEvent.VK_O);
		JMenuItem menu_save = new JMenuItem("Save", KeyEvent.VK_S);
		JMenuItem menu_saveas = new JMenuItem("Save As...", KeyEvent.VK_A);
		JMenuItem menu_pagesetup = new JMenuItem("Page Setup...     ",
				KeyEvent.VK_U);
		JMenuItem menu_print = new JMenuItem("Print...", KeyEvent.VK_P);
		JMenuItem menu_exit = new JMenuItem("Exit", KeyEvent.VK_X);
		JMenuItem menu_undo = new JMenuItem("Undo", KeyEvent.VK_U);
		menu_cut = new JMenuItem("Cut", KeyEvent.VK_T);
		menu_copy = new JMenuItem("Copy", KeyEvent.VK_C);
		menu_paste = new JMenuItem("Paste", KeyEvent.VK_P);
		menu_delete = new JMenuItem("Delete", KeyEvent.VK_L);
		JMenuItem menu_find = new JMenuItem("Find", KeyEvent.VK_F);
		JMenuItem menu_findnext = new JMenuItem("Find Next", KeyEvent.VK_N);
		JMenuItem menu_replace = new JMenuItem("Replace...", KeyEvent.VK_R);
		JMenuItem menu_selectall = new JMenuItem("Select All", KeyEvent.VK_A);
		JMenuItem menu_timedate = new JMenuItem("Time/Date    ", KeyEvent.VK_D);
		JMenuItem menu_font = new JMenuItem("Font...", KeyEvent.VK_F);
		JMenuItem menu_viewhelp = new JMenuItem("View Help", KeyEvent.VK_H);
		JMenuItem menu_about = new JMenuItem("About Notepad", KeyEvent.VK_A);
		JCheckBoxMenuItem menu_wordwrap = new JCheckBoxMenuItem("Word Wrap");
		menu_goto = new JMenuItem("Go To...", KeyEvent.VK_G);
		menu_statusbar = new JCheckBoxMenuItem("Status Bar");

		menu_file.setMnemonic(KeyEvent.VK_F);
		menu_edit.setMnemonic(KeyEvent.VK_E);
		menu_format.setMnemonic(KeyEvent.VK_O);
		menu_view.setMnemonic(KeyEvent.VK_V);
		menu_help.setMnemonic(KeyEvent.VK_H);
		menu_statusbar.setMnemonic(KeyEvent.VK_S);
		menu_wordwrap.setMnemonic(KeyEvent.VK_W);
		menu_copy.setEnabled(false);
		menu_cut.setEnabled(false);
		menu_delete.setEnabled(false);

		// TODO All menu items that are not yet completed will be disabled.
		menu_undo.setEnabled(false);
		menu_replace.setEnabled(false);
		menu_viewhelp.setEnabled(false);

		menu_new.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		menu_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		menu_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		menu_print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
				ActionEvent.CTRL_MASK));
		menu_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
				ActionEvent.CTRL_MASK));
		menu_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				ActionEvent.CTRL_MASK));
		menu_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK));
		menu_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
				ActionEvent.CTRL_MASK));
		menu_delete.setAccelerator(KeyStroke
				.getKeyStroke(KeyEvent.VK_DELETE, 0));
		menu_find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
				ActionEvent.CTRL_MASK));
		menu_findnext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		menu_replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
				ActionEvent.CTRL_MASK));
		menu_goto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
				ActionEvent.CTRL_MASK));
		menu_selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				ActionEvent.CTRL_MASK));
		menu_timedate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));

		menu_new.addActionListener(this);
		menu_open.addActionListener(this);
		menu_save.addActionListener(this);
		menu_saveas.addActionListener(this);
		menu_pagesetup.addActionListener(this);
		menu_print.addActionListener(this);
		menu_exit.addActionListener(this);
		menu_undo.addActionListener(this);
		menu_cut.addActionListener(this);
		menu_copy.addActionListener(this);
		menu_paste.addActionListener(this);
		menu_delete.addActionListener(this);
		menu_find.addActionListener(this);
		menu_findnext.addActionListener(this);
		menu_replace.addActionListener(this);
		menu_goto.addActionListener(this);
		menu_selectall.addActionListener(this);
		menu_timedate.addActionListener(this);
		menu_wordwrap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (((JCheckBoxMenuItem) ae.getSource()).isSelected()) {
					caretCount.setText(null);
					caretCount.setVisible(false);
					menu_statusbar.setEnabled(false);
					notepad.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					pad.setLineWrap(true);
					menu_goto.setEnabled(false);
				} else {
					pad.setLineWrap(false);
					menu_statusbar.setEnabled(true);
					menu_goto.setEnabled(true);
					notepad.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
					if (statusBarEnabled) {
						updateCaretLocation();
						caretCount.setVisible(true);
					}

				}
			}
		});
		menu_font.addActionListener(this);
		menu_statusbar.addActionListener(this);
		menu_viewhelp.addActionListener(this);
		menu_about.addActionListener(this);

		menu_file.add(menu_new);
		menu_file.add(menu_open);
		menu_file.add(menu_save);
		menu_file.add(menu_saveas);
		menu_file.addSeparator();
		menu_file.add(menu_pagesetup);
		menu_file.add(menu_print);
		menu_file.addSeparator();
		menu_file.add(menu_exit);

		menu_edit.add(menu_undo);
		menu_edit.addSeparator();
		menu_edit.add(menu_cut);
		menu_edit.add(menu_copy);
		menu_edit.add(menu_paste);
		menu_edit.add(menu_delete);
		menu_edit.addSeparator();
		menu_edit.add(menu_find);
		menu_edit.add(menu_findnext);
		menu_edit.add(menu_replace);
		menu_edit.add(menu_goto);
		menu_edit.addSeparator();
		menu_edit.add(menu_selectall);
		menu_edit.add(menu_timedate);

		menu_format.add(menu_wordwrap);
		menu_format.add(menu_font);
		menu_view.add(menu_statusbar);
		menu_help.add(menu_viewhelp);
		menu_help.addSeparator();
		menu_help.add(menu_about);

		menuBar.add(menu_file);
		menuBar.add(menu_edit);
		menuBar.add(menu_format);
		menuBar.add(menu_view);
		menuBar.add(menu_help);

		popup.add(menu_cut);
		popup.add(menu_copy);
		popup.add(menu_paste);

	}

	public static void main(String[] args) {

		if (args.length == 0) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new JNotepad("");
				}
			});
		} else {
			for (int i = 0; i < args.length; i++) {
				new JNotepad(args[i].trim());
			}
		}
	}
}
