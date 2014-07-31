package hardcode.preferences;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

public class PreferencesModel {
	private Properties properties;
	private String filename;
	private boolean changed;
	
	public PreferencesModel(String filename) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.filename = filename;
		this.properties = loadPropertiesFile(filename);
		changed = false;
	}
	
	public Object setProperty(String key, String value) {
		changed = true;
		return properties.setProperty(key, value);
	}

	public Enumeration<String> keys() {
		@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>) properties.propertyNames();
		return e;
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public boolean isChanged() {
		return changed;
	}
	
	public PreferencesController.Type getTypeFor(String propertyName) {
		if(properties.containsKey(PreferencesController.PREFIX_TYPE + propertyName)) {
			String typeString = properties.getProperty(PreferencesController.PREFIX_TYPE + propertyName);
			PreferencesController.Type type = PreferencesController.Type.TEXT; //default
			try {
				type = PreferencesController.Type.valueOf(typeString);
			} catch (Exception e) {
				return PreferencesController.Type.TEXT;
			}
			return type;
		} else {
			return PreferencesController.Type.TEXT;
		}
	}
	
	public String getTitleFor(String propertyName) {
		if(properties.containsKey(PreferencesController.PREFIX_TITLE + propertyName)) {
			return properties.getProperty(PreferencesController.PREFIX_TITLE + propertyName);
		} else {
			return propertyName;
		}
	}
	
	public String[] getValuesFor(String propertyName) {
		if(properties.containsKey(PreferencesController.PREFIX_VALUES + propertyName)) {
			String[] values = properties.getProperty(PreferencesController.PREFIX_VALUES + propertyName).split(",");
			for(int i = 0; i < values.length; i++) {
				values[i] = values[i].trim();
			}
			return values;
		} else {
			return null;
		}
	}

	private Properties loadPropertiesFile(String filename) throws InvalidPropertiesFormatException, FileNotFoundException, IOException  {
		if(filename == null || filename.isEmpty()) {
			throw new FileNotFoundException("Filename was null or empty.");
		}
		
		File file = new File(filename);
		
		if(!file.exists()){
			throw new FileNotFoundException(filename);
		}
		
		Properties props = new Properties();
		FileInputStream fin = new FileInputStream(file);
		props.loadFromXML(fin);
		fin.close();
		
		return props;
	}	
	
	public void saveProperties() throws IOException {
		if(!changed) 
			return;
		
		if(filename == null || filename.isEmpty()) {
			throw new FileNotFoundException("Filename was null or empty.");
		}
		
		File file = new File(filename);
		
		if(!file.exists())
			throw new FileNotFoundException(filename);
		
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String comment = "Last changed: " + dateString;
		
		FileOutputStream fos = new FileOutputStream(file);
		properties.storeToXML(fos, comment);
		
		changed = false;
	}
}