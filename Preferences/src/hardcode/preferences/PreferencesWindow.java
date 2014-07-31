package hardcode.preferences;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class PreferencesWindow extends JFrame{
	
	private List<SettingsEntry> settingsEntries;
	
	private PreferencesWindow me;
	private JComboBox<String> propertiesComboBox;
	private JPanel centerPanel;
	private JButton saveButton;
	private BorderLayout contentPaneBorderLayoutMgr;
	private String title;
	
	public PreferencesWindow(String title, Vector<String> propertiesComboBoxEntries, String currentComboBoxEntry) {
		this.setTitle(title);
		this.title = title;
		settingsEntries = new LinkedList<SettingsEntry>();
		me = this;
		initFrame(propertiesComboBoxEntries, currentComboBoxEntry);
	}
	
	private void initFrame(Vector<String> propertiesComboBoxEntries, String currentComboBoxEntry) {
		contentPaneBorderLayoutMgr = new BorderLayout();
		getContentPane().setLayout(contentPaneBorderLayoutMgr);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
		topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		propertiesComboBox = new JComboBox<String>(propertiesComboBoxEntries);
		propertiesComboBox.getEditor().setItem(currentComboBoxEntry);
		topPanel.add(propertiesComboBox);
		getContentPane().add(topPanel, BorderLayout.PAGE_START);
		
		initCenterPanel();
		
		saveButton = new JButton("OK");

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		bottomPanel.add(saveButton);
		getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
	}
	
	private void initCenterPanel() {
		centerPanel = new JPanel();
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		GridLayout gridLayout = new GridLayout(0, 2, 5, 5);
		centerPanel.setLayout(gridLayout);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Clears the center part of the window, containing the actual settings, ad re-inits this part.
	 */
	public void clear() {
		settingsEntries.clear();
		getContentPane().remove(contentPaneBorderLayoutMgr.getLayoutComponent(BorderLayout.CENTER));
		initCenterPanel();
	}
	
	public void setPropertiesComboBoxChangedListener(ActionListener l) {
		propertiesComboBox.addActionListener(l);
	}
	
	public void setSaveSettingsListener(ActionListener l) {
		saveButton.addActionListener(l);
	}
	
	public void addSettingsElements(List<SettingsEntry> entries) {
		for(SettingsEntry entry : entries) {
			addSettingsElement(entry);
		}
	}
	
	public void addSettingsElement(SettingsEntry entry) {
		JLabel label;
		label = new JLabel(entry.getTitle() + ": ");
		
		JComponent editable = null;
		
		switch (entry.getType()) {
		case TEXT:
			JTextField textField = new JTextField(entry.getCurrentValue());
			TextFieldSettingChangedListener txtListener = new TextFieldSettingChangedListener(entry); 
			textField.getDocument().addDocumentListener(txtListener);
			editable = textField;
			break;
			
		case COMBO:
			JComboBox<String> comboBox = new JComboBox<String>(entry.getPossibleValues());
			comboBox.setEditable(true);
			comboBox.getEditor().setItem(entry.getCurrentValue());
			ComboBoxSettingChangedListener comboListener = new ComboBoxSettingChangedListener(entry);
			comboBox.addActionListener(comboListener);
			editable = comboBox;
			break;
		
		case FILECHOOSER_DIRECTORY:
			JButton dirChooseButton = new JButton(entry.getCurrentValue());
			FileChooserTextFieldSettingChangedListener fileChooserListener = new FileChooserTextFieldSettingChangedListener(entry, dirChooseButton); 
			dirChooseButton.addMouseListener(fileChooserListener);
			editable = dirChooseButton;
			break;
		
		default:
			JOptionPane.showMessageDialog(me, "Could not create configuration changer for entry " + entry.getKey(), title, JOptionPane.ERROR_MESSAGE);
			return;			
		}

		settingsEntries.add(entry);
		centerPanel.add(label);
		centerPanel.add(editable);
	}
	
	public List<SettingsEntry> getSettingsEntries() {
		return settingsEntries;
	}
	
	@Override
	public void setVisible(boolean b) {
		//set size and center window
		int height = settingsEntries.size() * 45 + 100;
		this.setSize(350, height); //FIXME find better formula
		this.setLocationRelativeTo(null);
		super.setVisible(b);
	}
	
	private class FileChooserTextFieldSettingChangedListener extends MouseAdapter {
		private SettingsEntry entry;
		private JButton dirChooseButton;
		
		public FileChooserTextFieldSettingChangedListener(SettingsEntry entry, JButton dirChooseButton) {
			this.entry = entry;
			this.dirChooseButton = dirChooseButton;
		}
		
		@Override
		public void mouseClicked(MouseEvent e){
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	
			int returnVal = fc.showOpenDialog(me);
			if(returnVal == JFileChooser.APPROVE_OPTION) { //clicked OK
				File dir = fc.getSelectedFile();
				if(!dir.isDirectory()) {
					JOptionPane.showMessageDialog(me, "No directory selected.", title, JOptionPane.ERROR_MESSAGE);
					return;
				}
				String dirString = dir.getAbsolutePath();
				entry.setCurrentValue(dirString);
				dirChooseButton.setText(dirString);
			}
        }
	}
	
	private class TextFieldSettingChangedListener implements DocumentListener {
		private SettingsEntry entry;
		
		public TextFieldSettingChangedListener(SettingsEntry entry) {
			this.entry = entry;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateEntry(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateEntry(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			//probably not thrown
		}
		
		private void updateEntry(DocumentEvent e) {
			Document doc = e.getDocument();
			String text = null;
			try {
				text = doc.getText(0, doc.getLength());
			} catch (BadLocationException ex) {
				JOptionPane.showMessageDialog(me, title, ex.getMessage(), JOptionPane.ERROR_MESSAGE);
			}
			if(text == null) {
				JOptionPane.showMessageDialog(me, title, "Could not update setting entry (null).", JOptionPane.ERROR_MESSAGE);
			}
			entry.setCurrentValue(text);
		}
	}
	
	private class ComboBoxSettingChangedListener implements ActionListener {
		private SettingsEntry entry;
		
		public ComboBoxSettingChangedListener(SettingsEntry entry) {
			this.entry = entry;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
			String text = (String) comboBox.getSelectedItem();
			entry.setCurrentValue(text);			
		}
	}
}