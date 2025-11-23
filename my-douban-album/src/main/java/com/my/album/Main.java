package com.my.album;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.my.album.ui.MainFrame;

public class Main {
	public static void main(String[] args) {
		// 可用链接
		// https://movie.douban.com/celebrity/1052297/photos/
		// https://movie.douban.com/subject/30353357/photos?type=S
		// 不可用
		// https://www.douban.com/photos/album/1638511432/
		
//		curl "https://m.douban.com/rexxar/api/v2/elessar/subject/27483837/photos?start=0&count=30&ck=Yw6T" 
		//--compressed 
		// -H "User-Agent: Mozilla/5.0 (Linux; Android 11; SAMSUNG SM-G973U) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/14.2 Chrome/87.0.4280.141 Mobile Safari/537.36" 
		// -H "Accept: application/json, text/plain, */*" -H "Accept-Language: zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2" 
		// -H "Accept-Encoding: gzip, deflate, br, zstd" -H "Referer: https://m.douban.com/personage/27483837/photos?start=300&sortby=like" 
		// -H "Sec-Fetch-Dest: empty" -H "Sec-Fetch-Mode: cors" -H "Sec-Fetch-Site: same-origin" -H "Connection: keep-alive" -H "Cookie: __utma=30149280.1088696641.1687050108.1742304459.1742563369.149; __utmz=30149280.1742217481.146.68.utmcsr=search.douban.com^|utmccn=(referral)^|utmcmd=referral^|utmcct=/movie/subject_search; douban-fav-remind=1; __utmv=30149280.12203; __gads=ID=1c47e5046e5c4fd4:T=1711710589:RT=1725782522:S=ALNI_MZU9k4fVP0Lf-veyEtRyLinYYPVSg; __gpi=UID=00000d74999239c0:T=1711710589:RT=1725782522:S=ALNI_MYgvX-d4VSQUi5a-RUUtgy3tXZRwA; bid=RgBuEBNxLok; ll=""108288""; _ga_YD7QXHZJ4Y=GS1.1.1725782530.4.1.1725782556.0.0.0; _ga=GA1.1.457886010.1724514199; FCNEC=^%^5B^%^5B^%^22AKsRol9uHQlfoL20u6PavroKG44s42CmhlT8OjAFkU_9GdXltQpHUXEG7rJenPH99EuyylcoCw2TMuNImfu8Cd6Ty9sEYAwDs2IoFNLhDuoyVlDum2pJ9VHrITfqqnChUh1mh8p83ToG-nUeCYONkdLRsdCx-38_pw^%^3D^%^3D^%^22^%^5D^%^5D; _cc_id=a655b529b94cbd656c3ddfc606460660; cto_bundle=kXyf7V81N09OWVBNSWluTDhYcFlYNHhQSFlTTUFUWll5diUyQmFNbXd0d3Rtdmk1R1VaUFZVSjVScHNXeHIlMkJUWjlTZk9kQ1NrJTJGWUFNYk5sNUROaW0zbEw5dEpaWU84b1olMkZqNmNEVzVWQzNidExiUERZaTJ0Z3JYTkh3QzZJUjFzRDU3JTJGMGo; cto_bidid=MW32jl9JTU9Gb2c4dEVjJTJCcWNiNWp6ZjdPVzFaWSUyQk9pNiUyRnlnS0Z2eThySlFhSWhCdnQlMkJrMHU5R1B1amJhVnZUUVJ4SUxlJTJCMzRsOU9CTDczJTJGZnh6UGR0UU10ZyUzRCUzRA; viewed=""2302310""; _vwo_uuid_v2=DBCC1147BFE1A3102F74B6605953A72E8^|6a1ad6845f775a025f605486d8c0fd84; __utmc=30149280; dbcl2=""122036804:bi+zrmA4ZG0""; ck=Yw6T; push_noty_num=0; push_doumail_num=0; ap_v=0,6.0; frodotk=""d3e31b81062e7b207ba95283128b7160""; talionusr=""eyJpZCI6ICIxMjIwMzY4MDQiLCAibmFtZSI6ICJcdTVlOTNcdTVlMDNcdTkxY2NcdTUxNGJcdTViZmNcdTZmMTQifQ=="""
		// 

		Display display = new Display();
		Shell shell = new Shell(display);

		MainFrame mainFrame = new MainFrame(shell);
		mainFrame.createContent();

		shell.pack();
		int x = (display.getClientArea().width - shell.getSize().x) / 2;
		int y = (display.getClientArea().height - shell.getSize().y) / 2;
		shell.setLocation(x, y);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
