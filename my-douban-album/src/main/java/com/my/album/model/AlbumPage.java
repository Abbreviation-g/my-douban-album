package com.my.album.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
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

		Document document = Album.getDocument(pageUrl);
		if (document == null) {
			return;
		}
		Elements picsEles = document.getElementsByClass("pics");
		if (picsEles.isEmpty()) {
			return;
		}
		
		Element picEle = picsEles.get(0);
		Elements children = picEle.children();
		
		String log = "正在解析第" + pageNumber + "页. ";
		System.out.println(log);
		monitor.beginTask(log, children.size());
		monitor.subTask(pageUrl);
		
		for (Element ele : children) {
			Elements imgEles = ele.getElementsByTag("img");
			if (imgEles.isEmpty()) {
				continue;
			}
			Element imgEle = imgEles.get(0);
			String href = imgEle.attr("src");
			href = href.replace("photo/photo", "photo/raw");
			href = href.replace("webp", "jpg");
			System.out.println(href);
			
			imgUrlList.add(href);
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
