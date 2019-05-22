package com.my.album.model;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AlbumPage {
	private Integer pageNumber;
	private String pageUrl;
	private List<String> imgUrlList;

	public AlbumPage(Integer pageNumber, String pageUrl) {
		super();
		this.pageNumber = pageNumber;
		this.pageUrl = pageUrl;
		this.imgUrlList = new ArrayList<>();
	}

	public void parsePage(IProgressMonitor monitor) throws IOException {
		
		URL url = new URL(pageUrl);
		Document document = Jsoup.parse(url, 10 * 1000);
		Elements coverEles = document.select(".cover");
		String log = "正在解析第"+pageNumber+"页. ";
		System.out.println(log);
		monitor.beginTask(log, coverEles.size());
		monitor.subTask(pageUrl);
		for (Element element : coverEles) {
			if(monitor.isCanceled())
				break;
			String imgUrl = element.select("img").attr("src").replace("view/photo/m/public", "view/photo/raw/public");
			imgUrlList.add(imgUrl);
			System.out.println(imgUrl);
			monitor.worked(1);
		}
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public List<String> getImgUrlList() {
		return imgUrlList;
	}
	
	@Override
	public String toString() {
		return "AlbumPage [pageNumber=" + pageNumber + ", pageUrl=" + pageUrl + "]";
	}
}
