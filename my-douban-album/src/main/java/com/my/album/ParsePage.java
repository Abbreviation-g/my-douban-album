package com.my.album;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ParsePage {
	private List<String> imgUrlList = new ArrayList<>();

	private String albumName;

	public ParsePage(String firstPageUrl) throws IOException {
		recursionPage(firstPageUrl);
	}

	public List<String> getImgUrlList() {
		return imgUrlList;
	}

	public String getAlbumName() {
		return albumName;
	}

	private List<List<String>> getCutList(int size) {
		List<List<String>> subed = new ArrayList<>();
		int index = 0;
		do {
			int toIndex = index + size < imgUrlList.size() ? index + size : imgUrlList.size();
			subed.add(imgUrlList.subList(index, toIndex));
			index += size;
		} while (index < imgUrlList.size());

		return subed;
	}

	private void recursionPage(String currentPageUrl) throws IOException {
		URL url = new URL(currentPageUrl);
		Document document = Jsoup.parse(url, 10 * 1000);
		if (albumName == null) 
			analysisName(document);
		analysisPage(document);
		System.out.println("Analysis " + currentPageUrl);

		Element thisPageEle = document.selectFirst(".thispage");
		if (thisPageEle == null)
			return;
		Element nextUrlA = thisPageEle.nextElementSibling();
		if (nextUrlA.tagName().equals("a")) {
			recursionPage(nextUrlA.attr("href"));
		}
	}

	private void analysisPage(Document document) {
		document.select(".cover").forEach((e) -> imgUrlList
				.add(e.select("img").attr("src").replace("view/photo/m/public", "view/photo/raw/public")));
	}

	private void analysisName(Document document) {
		this.albumName = document.selectFirst("#content").child(0).text();
		System.out.println("Album name: "+ albumName);
	}

	public static void main(String[] args) throws IOException {
		String firstPageUrl = "https://movie.douban.com/subject/26266893/photos?type=S";
		ParsePage parsePage = new ParsePage(firstPageUrl);
		parsePage.imgUrlList.forEach(System.out::println);
		System.out.println(parsePage.getCutList(30).size());
		System.out.println(parsePage.getAlbumName());
		// https://img3.doubanio.com/view/photo/raw/public/p2547777865.jpg
		// https://img3.doubanio.com/view/photo/raw/public/p2547777865.jpg
	}
}
