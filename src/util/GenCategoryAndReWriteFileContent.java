package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fetch.CommonUtil;

public class GenCategoryAndReWriteFileContent {
	static FileFilter txtFilter = new FileFilter() {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith("txt");
		}
	};
	private static String directoryPath = "D:/cxl/my apk/__________others_________/穷人缺什么/assets";
	private static String gb2312 = "gb2312";
	private static String utf8 = "utf8";
	private static String txt_charset = utf8;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getTuJianList();
		//		getCatelogy();
		System.out.println("ok~~~~~~~~~~~~~~");
	}

	public static void getTuJianList() {
		File fileDir = new File(directoryPath);
		StringBuffer bf = null;
		;
		if (fileDir.isDirectory()) {
			for (File file : fileDir.listFiles(txtFilter)) {
				bf = new StringBuffer();
				FileInputStream fis = null;
				BufferedReader bufferedReader = null;
				InputStreamReader isReader = null;

				try {
					fis = new FileInputStream(file);
					bufferedReader = new BufferedReader(new InputStreamReader(fis, txt_charset));
					String contentString = "";
					String tempString = "";
					while (null != (contentString = bufferedReader.readLine())) {
						if (!"".equals(contentString)) {
							tempString = contentString;
						}
						bf.append(contentString + "\n");

					}
					String fileContent = bf.toString();
					fileContent = fileContent.substring(0, fileContent.lastIndexOf(tempString));
					CommonUtil.WriteFile(fileDir.getPath() + "/new/" + file.getName(), fileContent);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (bufferedReader != null) {
							bufferedReader.close();
						}
						if (fis != null) {
							fis.close();
						}
						if (isReader != null) {
							isReader.close();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		}

	}

	public static void getCatelogy() {
		File fileDir = new File("D:/cxl/my apk/__________others_________/穷人缺什么/assets/catalog.txt");
		int count = 0;
		FileInputStream fis = null;
		BufferedReader bufferedReader = null;
		InputStreamReader isReader = null;

		try {
			fis = new FileInputStream(fileDir);
			bufferedReader = new BufferedReader(new InputStreamReader(fis, txt_charset));
			String contentString = "";
			while (null != (contentString = bufferedReader.readLine())) {
				//						System.out.println("AllList.add(\""
				//								+ file.getName().substring(0, file.getName().lastIndexOf(".txt")) + "、" + contentString
				//								+ "\");");
				count++;
				System.out.println("MENU_List.add(new KeyValue(\"" + count + "\", \"" + contentString + "\"));");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (fis != null) {
					fis.close();
				}
				if (isReader != null) {
					isReader.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

}
