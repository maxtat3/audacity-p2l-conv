import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Main class of app.
 */
public class App {

	public static final String VER = "1.0";

	public static void main(String[] args) {
		if (args.length == 0) {
			Util.print(showHelp());
			System.exit(1);
		}

		Converter conv = new Converter();
		try {
			List<AudioTrack> audioTracks = conv.readAudioTracks(args[0]);
			conv.calculateTime(audioTracks, 0);
			conv.saveAsAudacityLabels(audioTracks);
		} catch (DataFormatException e) {
			System.exit(1);
		}
	}

	public static String showHelp() {
		StringBuilderCLI sb = new StringBuilderCLI();

		sb.append("*************************************");
		sb.append("\tAudacity labels converter" + " v" + VER);
		sb.append("*************************************");
		sb.append("Tiny Java console app for converting human readable playlist to Audacity labels format.");
		sb.append("Allowed comments in playlist. Comments may be marked # or // symbols.");
		sb.append("");
		sb.append("Arguments:");
		sb.append("If not passed  - show this help message and exit.");
		sb.append("arg 0 - path to input playlist file.");
		sb.append("");
		sb.append("");
		sb.append("Input playlist file contained audio tracks.");
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
