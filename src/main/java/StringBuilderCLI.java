/**
 * Special StringBuilder used for added each line from new line.
 * For using at Command LIne Interface (CLI).
 */
public class StringBuilderCLI {

	private StringBuilder sb;

	public StringBuilderCLI(){
		sb = new StringBuilder();
	}

// TODO: 23.07.20 is it necessary this method ?
//	public void append(String str) {
//		sb.append(str != null ? str : "");
//	}

	/**
	 * Append string and add new line in end this string.
	 *
	 * @param str text string
	 */
	public void append(String str) {
		sb.append(str != null ? str : "").append(System.getProperty("line.separator"));
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}