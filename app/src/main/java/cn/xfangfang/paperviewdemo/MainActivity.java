package cn.xfangfang.paperviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import cn.xfangfang.paperviewlibrary.PaperView;

public class MainActivity extends AppCompatActivity {

    private PaperView paperView;
    private RecyclerView recyclerView;

    private ArrayList<Book> books;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }

    private static final String TAG = "MainActivity";
    private void initData(){
        books = new ArrayList<>();
        books.add(new Book("呼啸山庄","艾米莉·勃朗特","http://www.readers365.com/World/001/"));
        books.add(new Book("雾都孤儿","查尔斯·狄更斯","http://www.readers365.com/World/004/"));
        books.add(new Book("鲁滨逊漂流记","丹尼尔·笛福","http://www.readers365.com/World/005/"));
        books.add(new Book("傲慢与偏见","简·奥斯汀","http://www.readers365.com/World/006/"));
        books.add(new Book("老人与海","海明威","http://www.readers365.com/World/031/"));
        books.add(new Book("麦田里的守望者","塞林格","http://www.readers365.com/World/033/"));
        books.add(new Book("生命中不能承受之轻","米兰·昆德拉","http://www.readers365.com/World/044/"));

    }
    private void initView(){
        recyclerView = (RecyclerView) findViewById(R.id.recylerview_book);
        LinearLayoutManager lm = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(lm);
        AdapterBookList bookadapter = new AdapterBookList(books);
        recyclerView.setAdapter(bookadapter);
        bookadapter.setOnItemClickListener(new AdapterBookList.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Book book = books.get(position);
                Intent intent = new Intent(MainActivity.this, BookReadActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.e(TAG, "onItemClick: 长按一次" );
            }
        });
    }

    public void setText(View view){
        paperView.setText(getResources().getString(R.string.three_country));
    }
    public void incLine(View view){
        paperView.setTextLine(paperView.getTextLine()+1);
    }
    public void decLine(View view){
        paperView.setTextLine(paperView.getTextLine()-1);
    }
    public void incTextSize(View view){
        paperView.setTextSize(paperView.getTextSize()+1);
    }
    public void decTextSize(View view){
        paperView.setTextSize(paperView.getTextSize()-1);
    }
    public void gotoPage(View view){
        paperView.setPage(5);
    }
    int padding = 16;
    public void setPadding(View view){
        paperView.setContentPadding(padding);
        padding+=5;
    }
}
