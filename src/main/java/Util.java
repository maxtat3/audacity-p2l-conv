/**
 * Created by max on 27.07.20.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * General utils methods used in this app.
 */
public class Util {

	/**
	 * Wrapper for System.out.println
	 *
	 * @param str text string
	 */
	public static void print(String str) {
		System.out.println(str);
	}

	/**
	 * Write text data to file in UTF-8 encoding.
	 * File name will be @link{#fileName} and current date in format Year-Month-Day_Hours-Minutes.
	 *
	 * @param fileName creating output file name
	 * @param text content written to file
	 */
	public static void writeToFile(String fileName, String text) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
