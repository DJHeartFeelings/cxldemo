package tesseractOCR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.util.OS;
/**
 * tesseract 地址，http://code.google.com/p/tesseract-ocr/downloads/list
 * @author caixl
 *
 */
public class OCR {

	private final String LANG_OPTION = "-l";

	private final String EOL = System.getProperty("line.separator");

	public String recognizeText(File imageFile, String imageFormat)
			throws Exception {

		File tempImage = ImageIOHelper.createImage(imageFile, imageFormat);
	
		File outputFile = new File(imageFile.getParentFile(), "output");

		StringBuffer strB = new StringBuffer();

		List<String> cmd = new ArrayList<String>();

		if (OS.isWindowsXP()) {

			cmd.add("tesseract");


		} else if (OS.isLinux()) {

			cmd.add("tesseract");

		}

		cmd.add("");

		cmd.add(outputFile.getName());

		cmd.add(LANG_OPTION);

		cmd.add("chi_sim");

		cmd.add("eng");

		ProcessBuilder pb = new ProcessBuilder();

		pb.directory(imageFile.getParentFile());

		cmd.set(1, tempImage.getName());

		pb.command(cmd);

		pb.redirectErrorStream(true);

		Process process = pb.start();

		//tesseract.exe 1.jpg 1 -l chi_sim

		int w = process.waitFor();

		// delete temp working files

		tempImage.delete();

		if (w == 0) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(outputFile

					.getAbsolutePath()

					+ ".txt"), "UTF-8"));

			String str;

			while ((str = in.readLine()) != null) {

				strB.append(str).append(EOL);

			}

			in.close();

		} else {

			String msg;

			switch (w) {

			case 1:

				msg = "Errors accessing files. There may be spaces in your image's filename.";

				break;

			case 29:

				msg = "Cannot recognize the image or its selected region.";

				break;

			case 31:

				msg = "Unsupported image format.";

				break;

			default:

				msg = "Errors occurred.";

			}

			tempImage.delete();

			throw new RuntimeException(msg);

		}

		new File(outputFile.getAbsolutePath() + ".txt").delete();

		return strB.toString();

	}

}