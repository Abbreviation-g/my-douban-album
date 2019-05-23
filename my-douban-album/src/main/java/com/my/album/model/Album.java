package com.my.album.model;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
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
		Document document = Jsoup.parse(createUrl(urlStr), 5 * 1000);
		this.albumName = document.selectFirst("#content").select("h1").text();

		Integer pagesSize = 1;
		Element paginator = document.selectFirst(".paginator");
		if (paginator != null) {
			Elements paginatorChildren = paginator.children();
			pagesSize = Integer.parseInt(paginatorChildren.get(paginatorChildren.size() - 3).text());
		}
		String log = "正在解析相册: " + this.albumName;
		monitor.beginTask(log, pagesSize);
		System.out.println(log);

		parseCurrentPage(urlStr, monitor);
	}

	private URL createUrl(String urlStr) throws MalformedURLException {
		try {
			URL url = new URL(urlStr);
			IPath urlPath = new Path(url.getPath());
			if (!url.getHost().equals("movie.douban.com") || urlPath.lastSegment().equals("photos")) {
				throw new MalformedURLException("请输入douban电影地址. 如https://movie.douban.com/celebrity/1052297/photos/");
			}
			return url;
		} catch (Exception e) {
			throw new MalformedURLException("请输入douban电影地址. 如https://movie.douban.com/celebrity/1052297/photos/");
		}
	}

	private void parseCurrentPage(String currentPageUrl, IProgressMonitor monitor) throws IOException {
		if (monitor.isCanceled())
			return;

		URL url = new URL(currentPageUrl);
		Document document = Jsoup.parse(url, 10 * 1000);
		Element thisPageEle = document.selectFirst(".thispage");
		Integer pageNumber = 1;
		if (thisPageEle != null) {
			pageNumber = Integer.parseInt(thisPageEle.text());
		}

		AlbumPage page = new AlbumPage(pageNumber, currentPageUrl);
		page.parsePage(monitor);
		this.pages.add(page);
		monitor.worked(1);

		if (thisPageEle != null) {
			Element nextUrlA = thisPageEle.nextElementSibling();
			if (nextUrlA.tagName().equals("a")) {
				parseCurrentPage(nextUrlA.attr("href"), monitor);
			}
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

	public void downloadPagesMultiThread(Shell parentShell, IProgressMonitor monitor, File outputFolder)
			throws InvocationTargetException, InterruptedException {

		monitor.beginTask("正在下载" + this.albumName, this.pages.size());
		for (AlbumPage albumPage : pages) {
			if (monitor.isCanceled())
				break;
			monitor.subTask(albumPage.getPageUrl());
			parentShell.getDisplay().asyncExec(() -> {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(new Shell(parentShell));
				try {
					dialog.run(true, true, (subMonitor) -> {
						List<String> imgUrlList = albumPage.getImgUrlList();
						String log = "开始下载第" + albumPage.getPageNumber() + "页. ";
						subMonitor.beginTask(log, imgUrlList.size());
						for (String imgUrl : imgUrlList) {
							subMonitor.subTask(imgUrl);
							if (subMonitor.isCanceled())
								break;
							DownloadUtil.download(imgUrl, outputFolder);
							subMonitor.worked(1);
						}
						subMonitor.done();
						monitor.worked(1);
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		monitor.done();
	}

}
