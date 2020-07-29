import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.zip.DataFormatException;

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
	public List<AudioTrack> readAudioTracks(String file) throws DataFormatException{
		List<AudioTrack> audioTracks = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String correctPLLine = checkCorrectPlaylistLine(line);
				if (! correctPLLine.equals("1")) continue;
				if (isStartLineWithComment(line)) continue;

				line = removeComment(line);

				String[] spl = line.split("\\t");
				if (validateTimeFormatMMSS(spl[0])) {
					audioTracks.add(new AudioTrack(spl[0], spl[1]));
				} else {
					throw new DataFormatException("Audio track [" + line + "] is in the wrong time format.");
				}


			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return audioTracks;
	}

	/**
	 * Calculate time range - start and end positions for all audio tracks.
	 * Method nothing returned, but modified {@link AudioTrack#startTime}
	 * and {@link AudioTrack#endTime} in all audio tracks.
	 *
	 * @param tracks all audio tracks received from playlist file.
	 * @param offsetTime offset at start (in seconds).
	 */
	public void calculateTime(List<AudioTrack> tracks, long offsetTime) {
		long prevTrackStartTimeMs = offsetTime * 1000;

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
	 * Validate time for format mm:ss (minutes : seconds).
	 *
	 * @param time validation string given time
	 * @return <tt>true</tt> - correct format given in time var, otherwise <tt>false</tt>
	 */
	public boolean validateTimeFormatMMSS(String time) {
		boolean isValidLen = false;
		if (time.length() == 5) isValidLen = true;

		boolean isPresentDelimiter = false;
		if (time.toCharArray()[2] == ':') isPresentDelimiter = true;

		// https://www.regular-expressions.info/numericranges.html
		boolean isMatchTime = time.matches("(^[0-5]?[0-9]):([0-5]?[0-9]$)");

		return isValidLen && isPresentDelimiter && isMatchTime;
	}

	/**
	 * Check is playlist text line is correct.
	 * Checks:
	 * 1. Length. If line length == 0 return null.
	 * 2. Contain TAB. Line must be contain \\t character for separate track duration and name.
	 *
	 * @param line checking text line
	 * @return <tt>true</tt> line must be processing, otherwise <tt>false</tt>.
	 */
	public String checkCorrectPlaylistLine(String line) {
		if (line.length() == 0) return "-1";
		if (! line.contains("\t")) {
			return "In line [" + line + "] track duration and name must be separated bt TAB character.";
		}
		return "1";
	}


	public static final String COMMENT_TYPE_1 = "//";
	public static final String COMMENT_TYPE_2 = "#";

	/**
	 * Check is start line with comment.
	 *
	 * @param line checking text line.
	 * @return <tt>true</tt> line started with comment, otherwise <tt>false</tt>.
	 */
	public boolean isStartLineWithComment(String line) {
		return line.startsWith(COMMENT_TYPE_1) || line.startsWith(COMMENT_TYPE_2);
	}

	/**
	 * Remove comments from text line.
	 * Supported comment symbols: // and #
	 *
	 * @param line audio text line
	 * @return useful text
	 */
	public String removeComment(String line) {
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
