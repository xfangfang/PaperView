package cn.xfangfang.paperviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cn.xfangfang.paperviewlibrary.PaperLayout;
import cn.xfangfang.paperviewlibrary.PaperView;

public class MainActivity extends AppCompatActivity {

    PaperView paperView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        paperView = (PaperView) findViewById(R.id.paper_view);
        paperView.setFont("cube.ttf");
        paperView.setChapterName("桃园三结义");
        paperView.setContentPadding(16);
        paperView.setBackgroundColor(0xffffff);
        paperView.setText(getResources().getString(R.string.three_country));
        paperView.setTextLine(17);
        paperView.setTextSize(17);
        paperView.setContentTextColor("#002505");
        paperView.setInfoTextColor("#8a000000");
        paperView.setExtraInfo("haha");
        paperView.setBackgroundColor(Color.parseColor("#C8E6C9"));
        paperView.setPage(0);
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
    }

}
