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
	//properties
	private Properties properties;
	//addition information for properties. May not be present.
	private Properties auxProperties;
	private String filename;
	private boolean changed;
	
	public PreferencesModel(String filename) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		this.filename = filename;
		this.properties = loadPropertiesFile(filename);
		try {
			//cut .xml, then add _aux.xml
			String auxFilename = filename.substring(0, filename.length() - 4) + PreferencesController.AUX;
			this.auxProperties = loadPropertiesFile(auxFilename);
		} catch (FileNotFoundException e) {
			this.auxProperties = null;
		}
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
		if (auxProperties == null) {
			return PreferencesController.Type.TEXT;
		} else if (!auxProperties.containsKey(propertyName + PreferencesController.TYPE)) {
			return PreferencesController.Type.TEXT;
		} else {
			String typeString = auxProperties.getProperty(propertyName + PreferencesController.TYPE);
			PreferencesController.Type type = PreferencesController.Type.TEXT; //default
			try {
				type = PreferencesController.Type.valueOf(typeString);
			} catch (Exception e) {
				System.out.println("Could not determine type of " + propertyName);
				//TODO: handle exception
			}
			return type;
		}
	}
	
	public String getTitleFor(String propertyName) {
		if (auxProperties == null) {
			return null;
		} else if (!auxProperties.containsKey(propertyName + PreferencesController.TITLE)) {
			return null;
		} else {
			return auxProperties.getProperty(propertyName + PreferencesController.TITLE); 
		}
	}
	
	public String[] getValuesFor(String propertyName) {
		if (auxProperties == null) {
			return null;
		} else if (!auxProperties.containsKey(propertyName + PreferencesController.VALUES)) {
			return null;
		} else {
			String[] values = auxProperties.getProperty(propertyName + PreferencesController.VALUES).split(",");
			for(int i = 0; i < values.length; i++) {
				values[i] = values[i].trim();
			}
			return values;	
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