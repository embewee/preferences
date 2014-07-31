package hardcode.preferences;

public class Main {

	public static void main(String[] args) {
		String filename= "/path/to/configs/";
		
		if(args.length == 1) {
			filename = args[0];
		}
		
		try {
			new PreferencesController("Eigenschaften", filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
