package com.my.album.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
			connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
			connection.setRequestProperty("Host", "movie.douban.com");
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
	
	public static void main(String[] args) throws IOException {
		String listLogFilePath = "G:\\女星\\欧美\\布丽·拉尔森 Brie Larson的图片\\list.log";
		File listLogFile = new File(listLogFilePath);
		File outputFolder = listLogFile.getParentFile();
		List<String> allLines = Files.readAllLines(listLogFile.toPath());
		for (String urlStr : allLines) {
			try {
				URL url  = new URL(urlStr);
				download(url.toString(), outputFolder);
			} catch (MalformedURLException e) {
				continue;
			}
		}
	}
}
