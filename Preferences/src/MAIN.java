
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;

import embewee.preferences.PreferencesController;


public class MAIN {

	public static void main(String[] args) {
		//String filename= "/home/michael/Matthaeus-Arbeit/terminal-configs/pg200.xml";
		String filename= "/home/michael/Matthaeus-Arbeit/terminal-configs/";
		try {
			PreferencesController c = new PreferencesController("Eigenschaften", filename);
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
