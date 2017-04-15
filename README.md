# PaperView


### 简介

 PaperView 是一个快捷开发Android小说app必备的组件。能够大大减少开发人员开发阅读器所占用的时间。
 


### 功能

- 支持设置字体文件、字体大小、显示行数等
- 电池电量和时间监控
- 支持跳转页面
- 类ViewPager翻页动画，单击直接跳转无动画
- 支持中部区域单击监听

### 加入到工程中

- Gradle

      compile 'cn.xfangfang:paperviewlibrary:1.0.2'
      
- Maven

      <dependency>
        <groupId>cn.xfangfang</groupId>
        <artifactId>paperviewlibrary</artifactId>
        <version>1.0.2</version>
        <type>pom</type>
      </dependency>
  
  
### 简单使用

    paperView.setFont("cube.ttf");
    //设置存放在 app/src/main/assets 目录下的字体文件
    
    paperView.setChapterName("桃园三结义");
    paperView.setExtraInfo("haha");
    //设置章节名称和额外信息
    //额外信息位置在屏幕居中正下部可以用来显示缓存信息等
    
    paperView.setContentPadding(16);
    //设置文字距离屏幕边框的距离
    
    paperView.setBackgroundColor(0xC8E6C9);
    paperView.setContentTextColor("#002505");
    paperView.setInfoTextColor("#8a000000");
    //设置背景、小说文字和其他信息的文字颜色
    
    paperView.setText(getResources().getString(R.string.three_country));
    paperView.setTextLine(17);
    paperView.setTextSize(17);
    //设置小说一页显示的行数和文字大小

    paperView.setPage(0);
    //设置位置，小于等于0为首页，一个很大的数为尾页
    
    paperView.setOnPaperViewStateListener(new PaperLayout.StateListener() {
        @Override
        public void toStart() {
            Toast.makeText(getBaseContext(),"到头了",Toast.LENGTH_SHORT).show();
        }
    
        @Override
        public void toEnd() {
            Toast.makeText(getBaseContext(),"结束了",Toast.LENGTH_SHORT).show();
        }
    
        @Override
        public void centerClicked() {
            Toast.makeText(getBaseContext(),"点击了中部",Toast.LENGTH_SHORT).show();
        }
    });
    //用来监听阅读器状态，在一章开始和结束时触发
    //以及中间点击监听
    
### 说明
    字体文件来源于网络，仅供测试使用，如有侵权请站内联系（是可以站内联系的吧？）
    本组件仅在魅族Pro6s android 6.0测试通过，不能保证其他设备正常使用。
    
    
