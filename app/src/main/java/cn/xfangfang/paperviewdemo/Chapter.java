package cn.xfangfang.paperviewdemo;

/**
 * Created by FANGs on 2017/2/9.
 */

class Chapter  {
    String url,name;

    Chapter(String url, String name){
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
