package com.ruanko.service;

import com.ruanko.model.Channel;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

public class UpdateThread extends Thread {
    private final static int TIMEOUT = 5000;
    private final static int BUFFERSIZE = 65536;
    private final static int DELAY_TIME =300*1000;

    private List<Channel> channelList;
    HttpURLConnection httpConn = null;

    public UpdateThread() {
        RSSService rssService = new RSSService();
        channelList = rssService.getChannelList();
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("正在更新······" + new Date());
            for (Channel channel:channelList) {
                System.out.println("更新：" + channel.getName());
                update(channel.getUrl(), channel.getFilePath());
            }
            try {
                sleep(DELAY_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(String urlPath, String filePath) {

        try {
            URL url = new URL(urlPath);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setConnectTimeout(TIMEOUT);

            int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK)
                return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(filePath);
        if (hasNewRSS(httpConn, file)) {
            System.out.println("现在更新");

            ByteBuffer buffer = download(httpConn);
            if (buffer != null)
                saveAs(buffer, file);
        } else
            System.out.println("RSS文件已更新完毕！");
    }

    private boolean hasNewRSS(HttpURLConnection httpConn, File file) {
        Boolean flag = false;
        long current = System.currentTimeMillis();
        long httpLastModified = httpConn.getHeaderFieldDate("Last-Modified", current);
        long fileLastModified = file.lastModified();
        if (httpLastModified > fileLastModified)
            flag = true;
        return flag;
    }

    private synchronized void saveAs(ByteBuffer buffer, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] bytes = buffer.array();
            out.write(bytes,0,buffer.limit());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer download(HttpURLConnection httpConn) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
        InputStream inputStream = null;
        try {
            inputStream = httpConn.getInputStream();

            ByteArrayOutputStream byteOutputStream=new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int num;
            while ((num = inputStream.read(bytes)) != -1) {
               byteOutputStream.write(bytes, 0, num);
            }
            byteOutputStream.flush();
            bytes = byteOutputStream.toByteArray();

            for (byte b : bytes) {
                buffer.put(b);
            }
            buffer.flip();
            byteOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
