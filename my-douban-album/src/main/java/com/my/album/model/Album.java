package com.my.album.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.my.album.util.DownloadUtil;

public class Album {

	private String urlStr;

	private String albumName;
	private List<AlbumPage> pages;

	public Album(String urlStr) {
		this.urlStr = urlStr;
		this.pages = new ArrayList<>();
	}

	public void parse(IProgressMonitor monitor) throws MalformedURLException, IOException {
		Document document = Jsoup.parse(new URL(urlStr), 5 * 1000);
		this.albumName = document.selectFirst("#content").select("h1").text();

		Element paginator = document.selectFirst(".paginator");
		Elements paginatorChildren = paginator.children();
		Integer pagesSize = Integer.parseInt(paginatorChildren.get(paginatorChildren.size() - 3).text());
		String log = "正在解析相册: " + this.albumName;
		monitor.beginTask(log, pagesSize);
		System.out.println(log);

		parseCurrentPage(urlStr, monitor);
	}

	private void parseCurrentPage(String currentPageUrl, IProgressMonitor monitor) throws IOException {
		if (monitor.isCanceled())
			return;

		URL url = new URL(currentPageUrl);
		Document document = Jsoup.parse(url, 10 * 1000);
		Element thisPageEle = document.selectFirst(".thispage");
		if (thisPageEle == null)
			return;

		Integer pageNumber = Integer.parseInt(thisPageEle.text());
		AlbumPage page = new AlbumPage(pageNumber, currentPageUrl);
		page.parsePage(monitor);
		this.pages.add(page);
		monitor.worked(1);

		Element nextUrlA = thisPageEle.nextElementSibling();
		if (nextUrlA.tagName().equals("a")) {
			parseCurrentPage(nextUrlA.attr("href"), monitor);
		}
	}

	public String getAlbumName() {
		return albumName;
	}

	public List<AlbumPage> getPages() {
		return pages;
	}

	public void downloadPages(IProgressMonitor monitor, File outputFolder) {
		for (AlbumPage albumPage : pages) {
			if (monitor.isCanceled())
				break;
			List<String> imgUrlList = albumPage.getImgUrlList();
			String log = "开始下载第" + albumPage.getPageNumber() + "页. ";
			monitor.beginTask(log, imgUrlList.size());
			for (String imgUrl : imgUrlList) {
				monitor.subTask(imgUrl);
				if (monitor.isCanceled())
					break;
				DownloadUtil.download(imgUrl, outputFolder);
				monitor.worked(1);
			}
			monitor.done();
		}
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		String urlStr = "https://movie.douban.com/subject/30353357/photos?type=S";

		Album album = new Album(urlStr);
		album.parse(null);
		album.getPages().forEach(System.out::println);
		System.out.println(album.getAlbumName());
	}
}
