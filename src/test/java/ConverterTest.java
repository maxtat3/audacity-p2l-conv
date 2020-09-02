import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class ConverterTest {

	/**
	 * Playlist contains audio tracks in correct format: <br>
	 * Time(minutes:seconds) [TAB] Track name
	 */
	private static final String PLAYLIST_CORRECT = "src/test/resources/playlist0_correct";

	private static final String PLAYLIST_WITH_COMMENTS = "src/test/resources/playlist1_comments";

	public static final String PLAYLIST_WITH_LINE_WRONG_DATE_FORMAT = "src/test/resources/playlist2_lineInWrongDateFormat";


	/**
	 * Correct playlist.
	 */
	@Test
	public void testReadAudioTracks_correctPlaylist() throws DataFormatException {
		List<AudioTrack> tracks = new Converter().readAudioTracks(PLAYLIST_CORRECT);
		List<AudioTrack> expected = new ArrayList<>();
		expected.add(new AudioTrack("03:10", "1. Allegro in A major"));
		expected.add(new AudioTrack("00:15", "2. Adagio in F minor"));
		expected.add(new AudioTrack("00:25", "3. Allegro assai in A major"));
		Assert.assertEquals(expected, tracks);
	}

	@Test
	public void testReadAudioTracks_correctPlaylistWithComments() throws DataFormatException {
		List<AudioTrack> tracks = new Converter().readAudioTracks(PLAYLIST_WITH_COMMENTS);

		List<AudioTrack> expected = new ArrayList<>();
		expected.add(new AudioTrack("00:10", "1. abcd"));
		expected.add(new AudioTrack("05:15", "2. def"));
		expected.add(new AudioTrack("07:01", "3. ghk"));
		expected.add(new AudioTrack("05:15", "4. efi"));

		Assert.assertEquals(expected, tracks);
	}

	@Test (expected = DataFormatException.class)
	public void testReadAudioTracks_wrongDateFormat() throws DataFormatException {
		new Converter().readAudioTracks(PLAYLIST_WITH_LINE_WRONG_DATE_FORMAT);
	}


	@Test
	public void testCalculateTime_1track() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("00:15", "Track name"));

		new Converter().calculateTime(playlist, 0L);

		Assert.assertEquals(0, playlist.get(0).getStartTime());
		Assert.assertEquals(15, playlist.get(0).getEndTime());
	}

	@Test
	public void testCalculateTime_5tracks() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("00:50", "Track name 1"));
		playlist.add(new AudioTrack("01:07", "Track name 2"));
		playlist.add(new AudioTrack("05:51", "Track name 3"));
		playlist.add(new AudioTrack("01:17", "Track name 4"));
		playlist.add(new AudioTrack("03:30", "Track name 5"));

		new Converter().calculateTime(playlist, 0L);

		Assert.assertEquals(0, playlist.get(0).getStartTime());
		Assert.assertEquals(50, playlist.get(0).getEndTime());

		Assert.assertEquals(50, playlist.get(1).getStartTime());
		Assert.assertEquals(117, playlist.get(1).getEndTime());

		Assert.assertEquals(117, playlist.get(2).getStartTime());
		Assert.assertEquals(468, playlist.get(2).getEndTime());

		Assert.assertEquals(468, playlist.get(3).getStartTime());
		Assert.assertEquals(545, playlist.get(3).getEndTime());

		Assert.assertEquals(545, playlist.get(4).getStartTime());
		Assert.assertEquals(755, playlist.get(4).getEndTime());
	}

	@Test
	public void testCalculateTime_3tracksWithStartOffset() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("00:55", "Track name"));
		playlist.add(new AudioTrack("01:29", "Track name"));
		playlist.add(new AudioTrack("07:31", "Track name"));

		new Converter().calculateTime(playlist, 15L);

		Assert.assertEquals(15, playlist.get(0).getStartTime());
		Assert.assertEquals(70, playlist.get(0).getEndTime());

		Assert.assertEquals(70, playlist.get(1).getStartTime());
		Assert.assertEquals(159, playlist.get(1).getEndTime());

		Assert.assertEquals(159, playlist.get(2).getStartTime());
		Assert.assertEquals(610, playlist.get(2).getEndTime());
	}

	@Test
	public void testCalculateTime_3tracksLongDuration() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("30:15", "Track name"));
		playlist.add(new AudioTrack("55:17", "Track name"));
		playlist.add(new AudioTrack("29:11", "Track name"));

		new Converter().calculateTime(playlist, 0L);

		Assert.assertEquals(0, playlist.get(0).getStartTime());
		Assert.assertEquals(1815, playlist.get(0).getEndTime());

		Assert.assertEquals(1815, playlist.get(1).getStartTime());
		Assert.assertEquals(5132, playlist.get(1).getEndTime());

		Assert.assertEquals(5132, playlist.get(2).getStartTime());
		Assert.assertEquals(6883, playlist.get(2).getEndTime());
	}


	/**
	 * Line not contain any comment.
	 * But contain spaces in end this line.
	 */
	@Test
	public void testRemoveCommentFromLine_notContainComments(){
		String a = "0. abcd    ";
		String res = new Converter().removeComment(a);
		Assert.assertEquals("0. abcd", res);
	}

	/**
	 * Line contain comment type 1 (// - two slashes).
	 * Comment in same line thant the audio track
	 */
	@Test
	public void testRemoveCommentFromLine_containCommentTwoSlashes(){
		String a = "1. abcd    // separated by 4 spaces characters";
		String res = new Converter().removeComment(a);
		Assert.assertEquals("1. abcd", res);
	}

	/**
	 * Line contain comment type 2 (# - hash symbol).
	 * Comment in same line thant the audio track
	 */
	@Test
	public void testRemoveCommentFromLine_containCommentHash() {
		String b = "2. def    # other example of comment";
		String res = new Converter().removeComment(b);
		Assert.assertEquals("2. def", res);
	}

	/**
	 * Line contain comment type 1 and 2 (# and //).
	 * Sequence: # than // .
	 * Comment in same line thant the audio track
	 */
	@Test
	public void testRemoveCommentFromLine_containCommentHashThanTwoSlashes() {
		String b = "3. gfk    # mixed example // of comments";
		String res = new Converter().removeComment(b);
		Assert.assertEquals("3. gfk", res);
	}

	/**
	 * Line contain comment type 1 and 2 (# and //).
	 * Sequence: // than # .
	 * Comment in same line thant the audio track
	 */
	@Test
	public void testRemoveCommentFromLine_containCommentTwoSlashesThanHash() {
		String b = "4. qtu    // mixed example # of comments in reverse order";
		String res = new Converter().removeComment(b);
		Assert.assertEquals("4. qtu", res);
	}


	@Test
	public void testIsStartLineWithComment_inStartNotContainComment() {
		String s = "00:10\t1. abcd";
		Assert.assertFalse(new Converter().isStartLineWithComment(s));
	}

	@Test
	public void testIsStartLineWithComment_inStartTwoSlashes() {
		String s = "// comment in single line";
		Assert.assertTrue(new Converter().isStartLineWithComment(s));
	}

	@Test
	public void testIsStartLineWithComment_inStartHash() {
		String s = "# comment in single line";
		Assert.assertTrue(new Converter().isStartLineWithComment(s));
	}


	@Test
	public void testCheckCorrectPlaylistLine_lineLengthIs0() {
		String s = "";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("-1", res);
	}

	@Test
	public void testCheckCorrectPlaylistLine_lineContainSomeText() {
		String s = "abcdeuo";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertThat(res, CoreMatchers.containsString("must be separated bt TAB character"));
	}

	@Test
	public void testCheckCorrectPlaylistLine_lineCorrect() {
		String s = "07:01\t3. ghk";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("1", res);
	}


	@Test
	public void testCheckCorrectPlaylistLine_lineContainManyTabs() {
		String s = "07:01\t1. audio\ttrack name\t";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("1", res);
	}


	@Test
	public void testValidateTimeFormatMMSS_correctTime() {
		String s = "00:07";
		Assert.assertTrue(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_wrongSeconds() {
		String s = "03:71";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_wrongMinutes() {
		String s = "71:11";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_lessNumbersInSeconds() {
		String s = "03:5";
		Assert.assertTrue(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_lessNumbersInMinutes() {
		String s = "3:55";
		Assert.assertTrue(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_largerNumbersInSeconds() {
		String s = "03:551";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_largerNumbersInMinutes() {
		String s = "033:51";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_textInSeconds() {
		String s = "71:ae";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_textInMinutes() {
		String s = "at:11";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}

	@Test
	public void testValidateTimeFormatMMSS_wrongDelimiters() {
		String s = "05a17";
		Assert.assertFalse(new Converter().validateTimeFormatMMSS(s));
	}


	/*
	 * Note. In this test case call also {@link Converter#calculateTime(List, long)} which is tested in a separated tests.
	 */
	@Test
	public void testPrepareLabels() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("00:50", "Track name 1"));
		playlist.add(new AudioTrack("01:07", "Track name 2"));
		playlist.add(new AudioTrack("05:51", "Track name 3"));
		playlist.add(new AudioTrack("01:17", "Track name 4"));
		playlist.add(new AudioTrack("03:30", "Track name 5"));

		Converter conv = new Converter();
		conv.calculateTime(playlist, 0);
		String res = conv.prepareLabels(playlist);

		String expected = "0\t50\tTrack name 1\n" +
			"50\t117\tTrack name 2\n" +
			"117\t468\tTrack name 3\n" +
			"468\t545\tTrack name 4\n" +
			"545\t755\tTrack name 5\n";

		Assert.assertEquals(expected, res);
	}

	@Test
	public void testPrepareLabelsWithoutStartZeroInMMSS() {
		List<AudioTrack> playlist = new ArrayList<>();
		playlist.add(new AudioTrack("1:50", "Track name 1"));
		playlist.add(new AudioTrack("00:5", "Track name 2"));

		Converter conv = new Converter();
		conv.calculateTime(playlist, 0);
		String res = conv.prepareLabels(playlist);

		String expected = "0\t110\tTrack name 1\n" +
			"110\t115\tTrack name 2\n";

		Assert.assertEquals(expected, res);
	}
}