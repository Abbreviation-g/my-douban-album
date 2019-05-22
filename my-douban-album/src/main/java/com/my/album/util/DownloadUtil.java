package com.my.album.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadUtil {

	public static void download(String urlStr, File outputFolder) {
		String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
		File imgFile = new File(outputFolder, fileName);
		if (imgFile.exists()) {
			System.out.println(imgFile + " already exists. ");
			return;
		}
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Referer", "https://www.douban.com/");
			InputStream inputStream = connection.getInputStream();
			if (!outputFolder.exists())
				outputFolder.mkdirs();
			FileOutputStream writer = new FileOutputStream(imgFile);
			byte[] data = new byte[1024];
			int n = 0;
			while ((n = inputStream.read(data)) != -1) {
				writer.write(data, 0, n);
			}
			inputStream.close();
			writer.close();

			System.out.println("Download " + imgFile + " is done. ");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
