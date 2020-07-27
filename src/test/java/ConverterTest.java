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
}