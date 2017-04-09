package cn.xfangfang.paperviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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
        paperView.setTextColor("#8a434343");
        paperView.setExtraInfo("haha");
        paperView.setPage(100);
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
//        paperView.setVerticalMode();
//        paperView.setExtraInfo("haha");
    }

}
