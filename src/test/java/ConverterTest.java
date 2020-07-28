import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ConverterTest {

	/**
	 * Playlist contains audio tracks in correct format: <br>
	 * Time(minutes:seconds) [TAB] Track name
	 */
	private static final String PLAYLIST_0 = "src/test/resources/playlist0_correct";

	/**
	 * Correct playlist.
	 */
	@Test
	public void testReadAudioTracks_correctPlaylist() {
		List<AudioTrack> tracks = new Converter().readAudioTracks(PLAYLIST_0);
		List<AudioTrack> expected = new ArrayList<>();
		expected.add(new AudioTrack("03:10", "1. Allegro in A major"));
		expected.add(new AudioTrack("00:15", "2. Adagio in F minor"));
		expected.add(new AudioTrack("00:25", "3. Allegro assai in A major"));
		Assert.assertEquals(expected, tracks);
	}

	@Test
	public void testCalculateTime() {
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
		Assert.assertEquals(false, new Converter().isStartLineWithComment(s));
	}

	@Test
	public void testIsStartLineWithComment_inStartTwoSlashes() {
		String s = "// comment in single line";
		Assert.assertEquals(true, new Converter().isStartLineWithComment(s));
	}

	@Test
	public void testIsStartLineWithComment_inStartHash() {
		String s = "# comment in single line";
		Assert.assertEquals(true, new Converter().isStartLineWithComment(s));
	}


	@Test
	public void testIsCorrectPlaylistLine_lineLengthIs0() {
		String s = "";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("-1", res);
	}

	@Test
	public void testIsCorrectPlaylistLine_lineContainSomeText() {
		String s = "abcdeuo";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertThat(res, CoreMatchers.containsString("must be separated bt TAB character"));
	}

	@Test
	public void testIsCorrectPlaylistLine_lineCorrect() {
		String s = "07:01\t3. ghk";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("1", res);
	}


	@Test
	public void testIsCorrectPlaylistLine_lineContainManyTabs() {
		String s = "07:01\t1. audio\ttrack name\t";
		String res = new Converter().checkCorrectPlaylistLine(s);
		Assert.assertEquals("1", res);
	}


}