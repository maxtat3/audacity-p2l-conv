/**
 * Main class of app.
 */
public class App {

	public static void main(String[] args) {
		if (args.length == 0) {
			Util.print("Not passed arguments");
			Util.print(showHelp());
			System.exit(1);
		}
	}

	public static String showHelp() {
		StringBuilderCLI sb = new StringBuilderCLI();

		sb.append("*********************************");
		sb.append("\tAudacity labels converter");
		sb.append("*********************************");
		sb.append("Java console app for converting human readable playlist to Audacity labels format.");
		sb.append("");
		sb.append("Arguments:");
		sb.append("If not passed  - show this help message and exit.");
		sb.append("arg 0 - path to input playlist file.");
		sb.append("");
		sb.append("Comments in playlist marked # or // symbols.");
		sb.append("");
		sb.append("Input playlist file contained normal human readable audio tracks.");
		sb.append("Format playlist:");
		sb.append("Time(minutes:seconds) [TAB] Track name");
		sb.append("");
		sb.append("For example:");
		sb.append("03:10\t1. Allegro in A major");
		sb.append("01:15\t2. Adagio in F♯ minor");
		sb.append("05:25\t3. Allegro assai in A major");

		return sb.toString();
	}
}
