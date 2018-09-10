package com.ruanko.dao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.ruanko.dao.NewsDao;
import com.ruanko.model.News;
import com.ruanko.service.RSSService;

public class FileDaoImpl implements NewsDao {

	@Override
	public Boolean save(List<News> newsList) {
		boolean flag = false;
		File file = new File("NewsFiles/1.txt");
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(file, true);
			bw = new BufferedWriter(fw);
			String content = "";
			for (News news : newsList) {
				content = new RSSService().newsToString(news);
				bw.write(content);
			}
			bw.close();
			fw.close();
			flag=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}

}
