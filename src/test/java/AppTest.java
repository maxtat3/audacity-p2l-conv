import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class AppTest {

	/**
	 * Test that the help message contain a description argument 0.
	 */
	@Test
	public void testShowHelpContainArg0() {
		Assert.assertThat(App.showHelp(), CoreMatchers.containsString("arg 0"));
		Assert.assertThat(App.showHelp(), CoreMatchers.containsString("playlist"));
	}
}