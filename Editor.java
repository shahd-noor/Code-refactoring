package edu.najah.cap;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class Editor extends JFrame implements ActionListener, DocumentListener {
	final int Dimension =500;
final int canselValue =1; //1 value if cancel is chosen
	final int approveValue=0; //value if approve (yes, ok) is chosen
	final int yesValue=0;//yes and no question
	final int  warningValue=2;//warning dialog
	public static  void main(String[] args) {
		new Editor();
	}

	public JEditorPane TP;//Text Panel
	private JMenuBar menu;//Menu
	private JMenuItem copy, paste, cut;
	public boolean changed = false;
	private File file;

	public Editor() {
		//Editor the name of our application
		super("Editor");
		TP = new JEditorPane();
		// center means middle of container.
		add(new JScrollPane(TP), "Center");
		TP.getDocument().addDocumentListener(this);

		menu = new JMenuBar();
		setJMenuBar(menu);
		BuildMenu();
		//The size of window
		setSize(Dimension,Dimension);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void BuildMenu() {
		buildFileMenu();
		buildEditMenu();
	}

	private void buildFileMenu() {
		JMenu file = new JMenu("File");
		file.setMnemonic('F');
		menu.add(file);
		JMenuItem n = new JMenuItem("New");
		n.setMnemonic('N');
		n.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		n.addActionListener(this);
		file.add(n);
		JMenuItem open = new JMenuItem("Open");
		file.add(open);
		open.addActionListener(this);
		open.setMnemonic('O');
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		JMenuItem save = new JMenuItem("Save");
		file.add(save);
		save.setMnemonic('S');
		save.addActionListener(this);
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		JMenuItem saveas = new JMenuItem("Save as...");
		saveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		file.add(saveas);
		saveas.addActionListener(this);
		JMenuItem quit = new JMenuItem("Quit");
		file.add(quit);
		quit.addActionListener(this);
		quit.setMnemonic('Q');
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
	}

	private void buildEditMenu() {
		JMenu edit = new JMenu("Edit");
		menu.add(edit);
		edit.setMnemonic('E');
		// cut
		cut = new JMenuItem("Cut");
		cut.addActionListener(this);
		cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		cut.setMnemonic('T');
		edit.add(cut);
		// copy
		copy = new JMenuItem("Copy");
		copy.addActionListener(this);
		copy.setMnemonic('C');
		copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		edit.add(copy);
		// paste
		paste = new JMenuItem("Paste");
		paste.setMnemonic('P');
		paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		edit.add(paste);
		paste.addActionListener(this);
		// find
		JMenuItem find = new JMenuItem("Find");
		find.setMnemonic('F');
		find.addActionListener(this);
		edit.add(find);
		find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		// select all
		JMenuItem sall = new JMenuItem("Select All");
		sall.setMnemonic('A');
		sall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		sall.addActionListener(this);
		edit.add(sall);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		if (action.equals("Quit")) {
			System.exit(0);
		} else if (action.equals("Open")) {
			loadFile();
		} else if (action.equals("Save")) {
			//Save file
			int answer = 0;
			if (changed) {
				// 0 means yes and no option, 2 Used for warning messages.
				answer = JOptionPane.showConfirmDialog(null, "The file has changed. You want to save it?", "Save file", 0, 2);
			}
			//1 value from class method if NO is chosen.
			if (answer != 1) {
				if (file == null) {
					saveAs("Save");
				} else {
					String text = TP.getText();
					System.out.println(text);
					try (PrintWriter writer = new PrintWriter(file);){
						if (!file.canWrite())
							throw new Exception("Cannot write file!");
						writer.write(text);
						changed = false;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else if (action.equals("New")) {
			//New file 
			if (changed) {
				//Save file 
				if (changed) {
					// 0 means yes and no option, 2 Used for warning messages.
					int answer= JOptionPane.showConfirmDialog(null, "The file has changed. You want to save it?", "Save file",
							0, 2);
					//1 value from class method if NO is chosen.
					if (answer == 1)
						return;
				}
				if (file == null) {
					saveAs("Save");
					return;
				}
				String text = TP.getText();
				System.out.println(text);
				try (PrintWriter writer = new PrintWriter(file);){
					if (!file.canWrite())
						throw new Exception("Cannot write file!");
					writer.write(text);
					changed = false;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			file = null;
			TP.setText("");
			changed = false;
			setTitle("Editor");
		} else if (action.equals("Save as...")) {
			saveAs("Save as...");
		} else if (action.equals("Select All")) {
			TP.selectAll();
		} else if (action.equals("Copy")) {
			TP.copy();
		} else if (action.equals("Cut")) {
			TP.cut();
		} else if (action.equals("Paste")) {
			TP.paste();
		} else if (action.equals("Find")) {
			FindDialog find = new FindDialog(this, true);
			find.showDialog();
		}
	}


	private void loadFile() {
		JFileChooser dialog = new JFileChooser(System.getProperty("user.home"));
		dialog.setMultiSelectionEnabled(false);
		try {
			int result = dialog.showOpenDialog(this);
			
			if (result == canselValue)
				return;
			if (result == approveValue) {
				if (changed){
					//Save file
					if (changed) {
						int answer = JOptionPane.showConfirmDialog(null, "The file has changed. You want to save it?", "Save file",
								yesValue, warningValue);
						if (answer == 1)// no option
							return;
					}
					if (file == null) {
						saveAs("Save");
						return;
					}
					String text = TP.getText();
					System.out.println(text);
					try (PrintWriter writer = new PrintWriter(file);){
						if (!file.canWrite())
							throw new Exception("Cannot write file!");
						writer.write(text);
						changed = false;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				file = dialog.getSelectedFile();
				//Read file 
				StringBuilder rs = new StringBuilder();
				try (	FileReader fr = new FileReader(file);		
						BufferedReader reader = new BufferedReader(fr);) {
					String line;
					while ((line = reader.readLine()) != null) {
						rs.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Cannot read file !", "Error !", 0);//0 means show Error Dialog
				}
				
				TP.setText(rs.toString());
				changed = false;
				setTitle("Editor - " + file.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//0 means show Error Dialog
			JOptionPane.showMessageDialog(null, e, "Error", 0);
		}
	}

	
	private void saveAs(String dialogTitle) {
		JFileChooser dialog = new JFileChooser(System.getProperty("user.home"));
		dialog.setDialogTitle(dialogTitle);
		int result = dialog.showSaveDialog(this);
		if (result != approveValue)
			return;
		file = dialog.getSelectedFile();
		try (PrintWriter writer = new PrintWriter(file);){
			writer.write(TP.getText());
			changed = false;
			setTitle("Editor - " + file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void saveAsText(String dialogTitle) {
		JFileChooser dialog = new JFileChooser(System.getProperty("user.home"));
		dialog.setDialogTitle(dialogTitle);
		int result = dialog.showSaveDialog(this);
		if (result != approveValue)
			return;
		file = dialog.getSelectedFile();
		try (PrintWriter writer = new PrintWriter(file);){
			writer.write(TP.getText());
			changed = false;
			setTitle("Save as Text Editor - " + file.getName());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changed = true;
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changed = true;
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		changed = true;
	}

}