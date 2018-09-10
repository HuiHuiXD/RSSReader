package com.ruanko.service;

import com.ruanko.dao.*;
import com.ruanko.dao.impl.FileDaoImpl;
import com.ruanko.model.Channel;
import com.ruanko.model.News;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RSSService {
	private List<Channel> channelList = null;
	private org.jdom2.Document doc;
	private List<News> newsList;
	private NewsDao rssDao;

	public RSSService() {
		rssDao = new FileDaoImpl();
	}
	
	public List<Channel> getChannelList() {
		if (channelList == null) {
			channelList = new ArrayList<>();

			Channel channel0 = new Channel();
			channel0.setName("腾讯-国际要闻");
			channel0.setFilePath("NewsFiles/rss_newswj.xml");
			channel0.setUrl("http://news.qq.com/newsgj/rss_newswj.xml");

			Channel channel1 = new Channel();
			channel1.setName("腾讯-国内新闻");
			channel1.setFilePath("NewsFiles/rss_newsgn.xml");
			channel1.setUrl("http://news.qq.com/newsgn/rss_newsgn.xml");
			
			Channel channel2 = new Channel();
			channel2.setName("新浪-社会新闻");
			channel2.setFilePath("NewsFiles/focus15.xml");
			channel2.setUrl("http://rss.sina.com.cn/news/society/focus15.xml");


			channelList.add(channel0);
			channelList.add(channel1);
			channelList.add(channel2);
		}
		return channelList;
	}

	private Document load(String filePath) {
		SAXBuilder sb = new SAXBuilder(false);
		File fXml = new File(filePath);
		try {
			doc = sb.build(fXml);
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
		return doc;
	}

	private News itemToNews(Element item) {
		News news = new News();
		String title = item.getChildText("title").trim();
		String link = item.getChildText("link");
		String author = item.getChildText("author");
		String category = item.getChildText("category");
		String description = item.getChildText("description");
		String comments = item.getChildText("comments");
		String pubDate = item.getChildText("pubDate");
		String guid = item.getChildText("guid");

		news.setComments(comments);
		news.setLink(link);
		news.setAuthor(author);
		news.setTitle(title);
		news.setCategory(category);
		news.setDescription(description);
		news.setGuid(guid);
		news.setPubDate(pubDate);
		return news;
	}

	private List<News> parse(Document doc) {
		List<News> newsList = new ArrayList<>();
		News news = null;

		Element root = doc.getRootElement();
		Element eChannel = root.getChild("channel");
		List<Element> itemList = eChannel.getChildren("item");

		for (Element item : itemList) {
			news = itemToNews(item);
			newsList.add(news);
		}
		return newsList;
	}

	public List<News> getNewsList(String filePath) {
		Document doc = load(filePath);
		newsList = parse(doc);
		return newsList;
	}

	public String newsToString(News news) {
		String content = ("标题: " + news.getTitle() + "\r\n" + "链接：" + news.getLink() + "\r\n" + "作者：" + news.getAuthor()
				+ "\r\n" + "发布时间：" + news.getPubDate() + "\r\n"
				+ "----------------------------------------------------------------------\r\n" + news.getDescription()
				+ "\r\n" + "\r\n");
		return content;
	}
	
	public boolean save(List<News> newsList) {
		boolean flag = false;
		if(rssDao.save(newsList)) {
			flag=true;
		}
		return flag;
	}
}





