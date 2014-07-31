package hardcode.preferences;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class PreferencesController {
	public final static String AUX = "_aux.xml"; //AUX + filename
	public final static String TYPE = "_type"; //propertyName + TYPE
	public final static String TITLE = "_title"; //propertyName + TITLE
	public final static String VALUES = "_values"; //propertyName + VALUES	
	
	enum Type {
		TEXT,
		COMBO,
		FILECHOOSER_DIRECTORY;
	}
	
	private PreferencesWindow window;
	private PreferencesModel model;
	private String title;
	
	private Vector<String> fileNames;
	private File directory;
	private File currentFile;
	
	
	public PreferencesController(final String title, String fileOrDirName) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.title = title;
		
		File file = new File(fileOrDirName);
		
		if(file.isFile()) {
			Vector<String> comboBoxEntries = new Vector<String>();
			comboBoxEntries.add(file.getName());
			controlFile(title, file, comboBoxEntries, file.getName());
		} else if(file.isDirectory()) {
			controlDirectory(title, file);
			this.directory = file;
		} else {
			JOptionPane.showMessageDialog(window, "Could not determine input file/directory.", title, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void controlDirectory(final String title, File directory) 
			throws InvalidPropertiesFormatException, FileNotFoundException, IOException{
		Vector<String> fileNamesVector = new Vector<String>();
		for (File fileEntry : directory.listFiles()) {
	        if (fileEntry.isFile()) {
	            String fileName = fileEntry.getName();
	            if(! fileName.endsWith(AUX)) {
	            	fileNamesVector.add(fileName);
	            }
	        }
	    }
		java.util.Collections.sort(fileNamesVector);	
		File firstFile = new File(directory.getAbsolutePath() + File.separator + fileNamesVector.get(0));
		
		fileNames = fileNamesVector;
		currentFile = firstFile;
		
		controlFile(title, firstFile, fileNamesVector, firstFile.getName());
	}
	
	private void controlFile(final String title, File file, Vector<String> comboBoxEntries, String currentcomboBoxEntry) 
			throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		model = new PreferencesModel(file.getAbsolutePath());		

		if (window == null) {
				window = new PreferencesWindow(title, comboBoxEntries, currentcomboBoxEntry);
				window.setPropertiesComboBoxChangedListener(new PropertiesComboBoxChangedListener());
		}
		
		window.addSettingsElements(preferencesToSettingsEntries());
		window.setSaveSettingsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				window.setVisible(false);
				try {
					saveSettings(window.getSettingsEntries());
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(window, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
				}
				window.dispose();
				return;
			}
		});
		window.setVisible(true);
	}
	
	private List<SettingsEntry> preferencesToSettingsEntries() {
		LinkedList<SettingsEntry> entryList = new LinkedList<SettingsEntry>();
		
		Enumeration<String> keys = model.keys();
		
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			SettingsEntry entry = new SettingsEntry(key, model.getTypeFor(key), model.getProperty(key), model.getTitleFor(key), model.getValuesFor(key));
			entryList.add(entry);
		}
		return entryList;
	}
	
	private void saveSettings(List<SettingsEntry> entries) throws IOException {
		for(SettingsEntry entry : entries) {
			if(entry.isChanged()) {
				model.setProperty(entry.getKey(), entry.getCurrentValue());
			model.saveProperties();
			}
		}
	}
	
	private class PropertiesComboBoxChangedListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
			String text = (String) comboBox.getSelectedItem();
			if(text.equals(currentFile.getName())) {
				//nop
			} else {
				File filename = new File(directory.getAbsolutePath() + File.separator + text); 
				window.setVisible(false);
				window.clear();
				try {
					controlFile(text, filename, null, null);
				} catch (InvalidPropertiesFormatException ex) {
					JOptionPane.showMessageDialog(window, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException ex) {
					JOptionPane.showMessageDialog(window, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(window, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
				}
				currentFile = new File(text);
			}
		}
	}
}