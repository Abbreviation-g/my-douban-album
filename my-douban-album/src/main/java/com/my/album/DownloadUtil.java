package com.my.album;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DownloadUtil {

	public static void download(String urlStr, File outputFolder) {
		String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1, urlStr.length());
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Referer", "https://www.douban.com/");
			InputStream inputStream = connection.getInputStream();
			if (!outputFolder.exists())
				outputFolder.mkdirs();
			File imgFile = new File(outputFolder, fileName);
			FileOutputStream writer = new FileOutputStream(imgFile);
			byte[] data = new byte[1024];
			int n = 0;
			while ((n = inputStream.read(data)) != -1) {
				writer.write(data, 0, n);
			}
			inputStream.close();
			writer.close();
			
			System.out.println("Download "+ urlStr +" to " +imgFile +" done. ");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		// String urlStr =
		// "https://img1.doubanio.com/view/photo/raw/public/p2554280127.jpg";
		// String outputFolder = "C:\\Users\\guo\\Pictures\\Saved Pictures";
		// download(urlStr, outputFolder);

		String urlStr = "https://movie.douban.com/subject/27144865/photos?type=S";
		String outputPath = "C:\\Users\\guo\\Pictures\\Saved Pictures";
		ParsePage parsePage = new ParsePage(urlStr);

		File outputFolder = new File(outputPath, parsePage.getAlbumName());
		List<String> imgUrlList = parsePage.getImgUrlList();
		for (String urlStr0 : imgUrlList) {
			download(urlStr0, outputFolder);
		}
	}
}
