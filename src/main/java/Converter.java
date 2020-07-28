import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Converting human readable playlist to Audacity labels format.
 */
public class Converter {

	/**
	 * Read playlist file from FS.
	 * Format playlist: <br>
	 * Time(minutes:seconds) [TAB] Track name <br>
	 * <br>
	 * For example: <br>
	 * 03:10 \t 1. Allegro in A major <br>
	 * 01:15 \t 2. Adagio in F minor <br>
	 * 05:25 \t 3. Allegro assai in A major <br>
	 *
	 * @param file absolute path to playlist file contained audio tracks in human readable format.
	 * @return list of POJO audio tracks files
	 */
	public List<AudioTrack> readAudioTracks(String file) {
		List<AudioTrack> audioTracks = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] spl = line.split("\\t");
				audioTracks.add(new AudioTrack(spl[0], spl[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return audioTracks;
	}

	/**
	 * Calculate time range - start and end positions for all audio tracks.
	 * Nothing returned, but only set {@link AudioTrack#startTime} and {@link AudioTrack#endTime} in all audio tracks.
	 *
	 * @param tracks all audio tracks received from playlist file.
	 * @param offsetTime offset at start (in seconds).
	 */
	public void calculateTime(List<AudioTrack> tracks, long offsetTime) {
		long prevTrackStartTimeMs = offsetTime;

		for (AudioTrack track : tracks) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
				sdf.setTimeZone(TimeZone.getTimeZone("UTC"));    // pure time - started from 0 ms
				long currTrackDurMs = sdf.parse(track.getDuration()).getTime();    // end track time
				long currTrackDurSec = TimeUnit.MILLISECONDS.toSeconds(currTrackDurMs);

				long startTimePosSec = TimeUnit.MILLISECONDS.toSeconds(prevTrackStartTimeMs);
				long endTimePosSec = currTrackDurSec + startTimePosSec;

				track.setStartTime(startTimePosSec);
				track.setEndTime(endTimePosSec);

				prevTrackStartTimeMs += currTrackDurMs;

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	/**

	/**
	 * Remove comments from text line.
	 * Supported comment symbols: // and #
	 *
	 * @param line audio text line
	 * @return useful text
	 */
	public String removeComment(String line) {
		String COMMENT_TYPE_1 = "//";
		String COMMENT_TYPE_2 = "#";

		if (! line.contains(COMMENT_TYPE_1) && ! line.contains(COMMENT_TYPE_2)) return line.trim();

		String commentType = null;
		char chCmType1 = COMMENT_TYPE_1.toCharArray()[0];
		char chCmType2 = COMMENT_TYPE_2.toCharArray()[0];
		char[] lineChars = line.toCharArray();
		for (int i = 0; i < lineChars.length; i++) {
			char ch = lineChars[i];
			if (lineChars[i] == chCmType1 && lineChars[i+1] == chCmType1) {
				commentType = COMMENT_TYPE_1;
				break;
			}
			if (ch == chCmType2) {
				commentType = COMMENT_TYPE_2;
				break;
			}
		}

		if (commentType != null) line = line.substring(0, line.indexOf(commentType));

		return line.trim();
	}

	/**
	 * Save to file as Audacity labels format.
	 *
	 * @param tracks all audio tracks received from playlist file.
	 */
	public void saveAsAudacityLabels(List<AudioTrack> tracks) {
		String text = "";	// could be applied StringBuilder, but not necessary, low size text data processed.
		for (AudioTrack track : tracks) {
			text += track.getStartTime() + "\t"
				+ track.getEndTime() + "\t"
				+ track.getName() + "\n";
		}
		Util.writeToFile("audacity-labels", text);
	}

}
