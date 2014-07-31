package hardcode.preferences;


public class SettingsEntry {	
	private PreferencesController.Type type;
	private String key;
	private String currentValue;
	private String title; 
	private String[] possibleValues;
	private boolean changed;
	
	public SettingsEntry(String key, PreferencesController.Type type, String currentValue, String title, String[] possibleValues) {
		super();
		this.type = type;
		this.key = key;
		this.currentValue = currentValue;
		this.title = title;
		this.possibleValues = possibleValues;
		this.changed = false;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
		this.changed = true;
	}

	public PreferencesController.Type getType() {
		return type;
	}
	
	public String getKey() {
		return key;
	}

	public String getTitle() {
		if(title != null) {
			return title;
		} else {
			return key;
		}
	}

	public String[] getPossibleValues() {
		return possibleValues;
	}
	
	public boolean isChanged() {
		return changed;
	}
}