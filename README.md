# PaperView


### 简介

 PaperView 是一个快捷开发Android小说app必备的组件。能够大大减少开发人员开发阅读器所占用的时间。
 
 
 ![shot](https://github.com/xfangfang/PaperView/blob/master/readme/shots.jpg?raw=true)


### 功能

- 支持设置字体文件、字体大小、显示行数等
- 电池电量和时间监控
- 支持跳转页面
- 类ViewPager翻页动画，单击直接跳转无动画
- 支持中部区域单击监听
- 更多特性，请见样例

### 加入到工程中

- Gradle

      compile 'cn.xfangfang:paperviewlibrary:1.0.3'
      
- Maven

      <dependency>
        <groupId>cn.xfangfang</groupId>
        <artifactId>paperviewlibrary</artifactId>
        <version>1.0.3</version>
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
    
    详情见DEMO
    
###更新

-  优化 降低翻页触发速度
-  优化 增加翻页动画时长
-  优化 最低支持至api9
-  优化 增大文字和上下工具栏的距离
-  优化 显示最小行数修改为三行
-  修复 在最后一页减小字体、行数等会显示首页
-  修复 在onCreat中设置文字，文字会重叠
-  修复 设置ContentPadding时，文字会重叠
-  修复 设置ContentPadding时，段尾超出界限
-  修复 多次设置文字，不跳转回首页
-  修复在低安卓版本上单击换页时显示为空
-  修复滑动并退回到原位时，被判定为单击
-  增加 设置前后两章切换的监听器
-  增加翻页触发速度设置
-  增加 翻页动画时长设置
-  增加 轻微滑动敏感度设置
-  增加 一个完整的小说阅读APP源码

### 说明
    字体文件及小说资源来自于网络，仅供测试使用，如有侵权请站内联系（是可以站内联系的吧？）
    本组件仅在魅族Pro6s android 6.0测试通过，不能保证其他设备正常使用。
    
    
