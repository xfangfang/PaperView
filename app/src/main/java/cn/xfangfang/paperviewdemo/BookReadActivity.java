package cn.xfangfang.paperviewdemo;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import cn.xfangfang.paperviewlibrary.PaperView;

public class BookReadActivity extends AppCompatActivity {

    PaperView paperView;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    RecyclerView recyclerView;

    SharedPreferences.Editor stateEditor;
    SharedPreferences stateReader;
    Book book;
    ArrayList<Chapter> chapters;
    int chapterPosition,page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_read);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
        initData();
    }

    private void initView(){
        paperView = (PaperView) findViewById(R.id.paperview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout = (LinearLayout) findViewById(R.id.tool);
        recyclerView = (RecyclerView) findViewById(R.id.recylerview_toc);
        LinearLayoutManager lm = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(lm);

        paperView.setFont("cube.ttf");


    }

    private static final String TAG = "BookReadActivity";

    private void initData(){
        book = (Book) getIntent().getSerializableExtra("book");
        stateEditor = getSharedPreferences(book.getName(), MODE_PRIVATE).edit();//保存设置信息（颜色、字号等）
        //从历史记录中读取颜色信息
        stateReader = getSharedPreferences(book.getName(), MODE_PRIVATE);
        chapterPosition = stateReader.getInt("chapterPosition",0);
        page = stateReader.getInt("page",0);

        new getTocTask().execute(book.getUrl());
    }

    class getTocTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            chapters = new ArrayList<>();
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements content = doc.select(".content");
            Elements catalogs = content.get(0).getElementsByTag("a");
            for (Element j :
                    catalogs) {
                chapters.add(new Chapter(j.attr("href"),j.text()));
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            paperView.setChapterName(chapters.get(chapterPosition).getName());
            AdapterChapterList chapterAdapter = new AdapterChapterList(chapters);
            recyclerView.setAdapter(chapterAdapter);
            chapterAdapter.setOnItemClickListener(new AdapterChapterList.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                    Chapter c = chapters.get(position);
                    paperView.setChapterName(c.getName());
                    page = 0;
                    chapterPosition = position;
                    new getChapterTask().execute(book.getUrl(),c.getUrl());
                    stateEditor.putInt("chapterPosition",chapterPosition).apply();
                    stateEditor.putInt("page",page).apply();
                }

                @Override
                public void onItemLongClick(View view, int chapterPosition) {
                    Log.e(TAG, "onItemClick: 长按一次" );
                }
            });
            paperView.setOnPageStateListener(new PaperView.onPageStateLinstener() {
                @Override
                public boolean getNextChapter() {
                    Snackbar.make(paperView,"取下一章",Snackbar.LENGTH_SHORT).show();

                    if(chapterPosition < chapters.size()-1) {
                        new getNextChapterStringTask().execute(book.getUrl(),chapters.get(chapterPosition+1).getUrl());
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean getPreChapter() {
                    Snackbar.make(paperView,"取上一章",Snackbar.LENGTH_SHORT).show();
                    if(chapterPosition >=1) {
//                        paperView.setPreChapterText("1\n2\n3\n4\n5\n"+chapterPosition);
                        new getPreChapterStringTask().execute(book.getUrl(),chapters.get(chapterPosition-1).getUrl());
                        return true;
                    }
                    return false;
                }

                @Override
                public void incChapter() {
                    chapterPosition++;
                    if(chapterPosition >= chapters.size()){
                        chapterPosition = chapters.size()-1;
                        paperView.setNextChapterText(null);
                    }
                    stateEditor.putInt("chapterPosition",chapterPosition).apply();
                    paperView.setChapterName(chapters.get(chapterPosition).getName());
                    paperView.setExtraInfo(String.valueOf(chapterPosition));
                }

                @Override
                public void decChapter() {
                    chapterPosition--;
                    if(chapterPosition < 0){
                        chapterPosition = 0;
                        paperView.setPreChapterText(null);
                    }
                    stateEditor.putInt("chapterPosition",chapterPosition).apply();
                    paperView.setChapterName(chapters.get(chapterPosition).getName());
                    paperView.setExtraInfo(String.valueOf(chapterPosition));
                }


                @Override
                public void isStartPoint() {
                    Snackbar.make(paperView,"正在加载上一章",Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void isEndPoint() {
                    Snackbar.make(paperView,"正在加载下一章",Snackbar.LENGTH_SHORT).show();
                }

                @Override
                public void centerClicked() {
                    Snackbar.make(paperView,"点击了中部",Snackbar.LENGTH_SHORT).show();
                    if(linearLayout.isShown()){
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEveryPageLoad(int currentPage, int wholePage) {
                    stateEditor.putInt("page",currentPage).apply();
                }
            });
            new getChapterTask().execute(book.getUrl(),chapters.get(chapterPosition).getUrl());
        }
    }

    class getChapterTask extends AsyncTask<String, Void, Boolean> {
        String chapter;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String chapterUrl = params[1];
            Document doc = null;
            try {
                doc = Jsoup.connect(url+chapterUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String temp = doc.toString().replaceAll("<br>","###");
            doc = Jsoup.parse(temp);

            Elements c = doc.getElementsByAttributeValue("width","100%");
            String res = c.get(c.size()-2).text();
            res = res.replaceAll("###","\n");
            chapter = res;
            return true;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            paperView.setText(chapter);
            paperView.setPage(page);
        }
    }

    class getNextChapterStringTask extends AsyncTask<String, Void, Boolean> {
        String chapter;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String chapterUrl = params[1];
            Document doc = null;
            try {
                doc = Jsoup.connect(url+chapterUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String temp = doc.toString().replaceAll("<br>","###");
            doc = Jsoup.parse(temp);

            Elements c = doc.getElementsByAttributeValue("width","100%");
            String res = c.get(c.size()-2).text();
            res = res.replaceAll("###","\n");
            chapter = res;
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            paperView.setNextChapterText(chapter);
        }
    }

    class getPreChapterStringTask extends AsyncTask<String, Void, Boolean> {
        String chapter;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = params[0];
            String chapterUrl = params[1];
            Document doc = null;
            try {
                doc = Jsoup.connect(url+chapterUrl).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String temp = doc.toString().replaceAll("<br>","###");
            doc = Jsoup.parse(temp);

            Elements c = doc.getElementsByAttributeValue("width","100%");
            String res = c.get(c.size()-2).text();
            res = res.replaceAll("###","\n");
            chapter = res;

//            chapter = getResources().getString(R.string.three_country);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressBar.setVisibility(View.GONE);
            paperView.setPreChapterText(chapter);
        }
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

    public void toc(View view){
        if(recyclerView.isShown()){
            recyclerView.setVisibility(View.INVISIBLE);
        }else{
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
