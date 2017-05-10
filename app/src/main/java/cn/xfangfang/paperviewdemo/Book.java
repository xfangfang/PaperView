package cn.xfangfang.paperviewdemo;

import java.io.Serializable;

/**
 * Created by FANGs on 2017/1/25.
 */

class Book implements Serializable{

    private int readPosition, readPositionPage,lastReadTime;
    //lastCid上次目录缓存位置
    private String name, author, url,lastChapterName;



    Book(String name, String author,
         String url) {
        this.name = name;
        this.author = author;
        this.url = url;
        this.lastChapterName = "暂未阅读";
        this.readPosition = 0;
        this.readPositionPage = 0;
    }


    @Override
    public String toString() {
        return this.name;
    }

    public int getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(int readPosition) {
        this.readPosition = readPosition;
    }

    public int getReadPositionPage() {
        return readPositionPage;
    }

    public void setReadPositionPage(int readPositionPage) {
        this.readPositionPage = readPositionPage;
    }

    public int getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(int lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLastChapterName() {
        return lastChapterName;
    }

    public void setLastChapterName(String lastPositionName) {
        this.lastChapterName = lastPositionName;
    }

}
