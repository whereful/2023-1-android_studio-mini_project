package com.hansung.mini_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    TabHost tabHost;

    public static DynamicMovieList dynamicMovieList = new DynamicMovieList();

    RecyclerView recyclerView_main, recyclerView_search;
    LinearLayoutManager linearLayoutManager_main, linearLayoutManager_search;
    CustomAdapter customAdapter_main, customAdapter_search;

    WebView webView;
    final String[] menu = {"장르", "인기", "투표 개수", "평점"};



    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = getTabHost();


        TabHost.TabSpec tabSpecMain = tabHost.newTabSpec("MAIN").setIndicator("메인");
        tabSpecMain.setContent(R.id.tabMain);
        tabHost.addTab(tabSpecMain);

        TabHost.TabSpec tabSpecSearch = tabHost.newTabSpec("SEARCH").setIndicator("검색");
        tabSpecSearch.setContent(R.id.tabSearch);
        tabHost.addTab(tabSpecSearch);

        TabHost.TabSpec tabSpecDetail = tabHost.newTabSpec("DETAIL").setIndicator("상세");
        tabSpecDetail.setContent(R.id.tabDetail);
        tabHost.addTab(tabSpecDetail);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {


                if(s.equals("MAIN") || s.equals("DETAIL")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.

                    imm.hideSoftInputFromWindow(tabHost.getWindowToken(), 0);
                }
            }
        });

        // 첫 번째 탭 화면 구성 - movieList 설정
        tabHost.setCurrentTab(0);

        try {
            dynamicMovieList.setMovieList(makeMovieList());


            dynamicMovieList.movieListSort(MovieListComparator.VOTE_POPULARITY);
            dynamicMovieList.makeStringList();

            makeRecyclerView_main();

        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 세 번째 화면 구성 - 평점 가장 높은 영화 출력
        setDetailTab(MainActivity.dynamicMovieList.findMaxVoteAverageMovie());
        // ====================================


        // 첫 번째 화면 구성 - 버튼 구성
        Button button_standard = (Button) findViewById(R.id.button_standard);
        button_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("정렬하기");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setItems(menu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dynamicMovieList.movieListSort(i);
                        dynamicMovieList.makeStringList();

                        customAdapter_main.setTarget_movie_list(MainActivity.dynamicMovieList.string_target_movie_list);

                        customAdapter_main.notifyDataSetChanged();
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();


            }
        });

        Button button_genre = (Button) findViewById(R.id.button_genre);
        button_genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("장르 선택");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setMultiChoiceItems(DynamicMovieList.genre, DynamicMovieList.genre_boolean, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        DynamicMovieList.selectGenre(DynamicMovieList.genre[i], b);

//                        dynamicMovieList.movieListSort(i);
                        dynamicMovieList.makeStringList();

                        customAdapter_main.setTarget_movie_list(MainActivity.dynamicMovieList.string_target_movie_list);

                        customAdapter_main.notifyDataSetChanged();
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });

        // ===================

        // 두 번째 탭 화면 구성
        tabHost.setCurrentTab(1);

        AutoCompleteTextView movie_search = (AutoCompleteTextView) findViewById(R.id.movie_name);
        List<String> movie_name_array = new ArrayList<String>();
        for (Movie m : dynamicMovieList.movieList) {
            movie_name_array.add(m.getOriginal_title());
        }
        movie_name_array = movie_name_array.stream().distinct().collect(Collectors.toList());

        ArrayAdapter<String> movie_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                movie_name_array);
        movie_search.setAdapter(movie_adapter);


        AutoCompleteTextView actor_search = (AutoCompleteTextView) findViewById(R.id.actor_name);
        List<String> actor_name_array = new ArrayList<String>();
        for (Movie m : dynamicMovieList.movieList) {
            actor_name_array.add(m.getCast());
        }
        actor_name_array = actor_name_array.stream().distinct().collect(Collectors.toList());

        ArrayAdapter<String> actor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                actor_name_array);
        actor_search.setAdapter(actor_adapter);


        Button button_search = (Button) findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Movie> movies_filtered = new ArrayList<Movie>();

                String movie_name = movie_search.getText().toString();
                String actor_name = actor_search.getText().toString();


                for (Movie m : dynamicMovieList.movieList) {
                    if (movie_name.isEmpty() && actor_name.isEmpty()) {
                        continue;
                    }

                    else if (movie_name.isEmpty() == false && actor_name.isEmpty()) {
                        if (movie_name.equals(m.getOriginal_title())) {
                            movies_filtered.add(m);
                        }
                    }

                    else if (movie_name.isEmpty() && actor_name.isEmpty() == false) {
                        if (actor_name.equals(m.getCast())) {
                            movies_filtered.add(m);
                        }
                    }
                    else if (movie_name.isEmpty() == false && actor_name.isEmpty() == false) {
                        if (movie_name.equals(m.getOriginal_title()) && actor_name.equals(m.getCast())) {
                            movies_filtered.add(m);
                        }
                    }
                }

                MovieListComparator.compare(movies_filtered, MovieListComparator.VOTE_AVERAGE);

                int count = 0;
                ArrayList<String> movie_explain = new ArrayList<>();

                List<Movie> movie_explain_target = new ArrayList<Movie>();

                for (Movie m : movies_filtered) {

                    // 전체 개수가 5개 이상이면 종료
                    if (count >= 5) {
                        break;
                    }

                    String s;

                    s = "제목 : " + m.getOriginal_title() + "\n" +
                            "배우 : " + m.getCast() + "\n" +
                            "아이디 : " + m.getId() + "\n" +
                            "평점 : " + m.getVote_average() + "\n" +
                            "개요 : " + m.getOverview().substring(0, 45) + "...";

                    movie_explain.add(s);

                    movie_explain_target.add(m);

                    count += 1;

                }

                makeRecyclerView_search(movie_explain, movie_explain_target);
            }
        });


        // 세 번째 화면 구성 - 평점 저장
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        TextView ratingText = (TextView) findViewById(R.id.ratingNumber);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                ratingText.setText(Float.toString(ratingBar.getRating()));
            }
        });



        // 앱이 시작되면 메인 화면에서 구동되도록 함
        tabHost.setCurrentTab(0);



    }

    public List<Movie> makeMovieList() throws CsvValidationException, IOException{

        // res/raw/item.csv 파일을 불러오기 위해 해당 코드 작성
        InputStream is = getResources().openRawResource(R.raw.movies_metadata_modified);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

        // new FileReader("movies_metadata_modified_mac.csv")

        List<Movie> movies = new CsvToBeanBuilder<Movie>(reader)
                .withType(Movie.class)
                .build()
                .parse();

        return movies;
    }

    public void makeRecyclerView_main() {

        recyclerView_main = findViewById(R.id.recyclerView_main);

        //--- LayoutManager는 아래 3가지중 하나를 선택하여 사용 ---
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        //---------------------------------------------------------
        linearLayoutManager_main = new LinearLayoutManager((Context) this);
        recyclerView_main.setLayoutManager(linearLayoutManager_main);  // LayoutManager 설정


        customAdapter_main = new CustomAdapter(MainActivity.dynamicMovieList.getStringList(),
                MainActivity.dynamicMovieList.getString_target_movie_list());


        //===== [Click 이벤트 구현을 위해 추가된 코드] ==============
        customAdapter_main.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Movie data) {
                setDetailTab(data);
            }
        });
        //==========================================================


        recyclerView_main.setAdapter(customAdapter_main); // 어댑터 설정

    }

    public void makeRecyclerView_search(ArrayList<String> search_movie_list, List<Movie> search_movie_target_list) {

        recyclerView_search = findViewById(R.id.recyclerView_search);

        //--- LayoutManager는 아래 3가지중 하나를 선택하여 사용 ---
        // 1) LinearLayoutManager()
        // 2) GridLayoutManager()
        // 3) StaggeredGridLayoutManager()
        //---------------------------------------------------------
        linearLayoutManager_search = new LinearLayoutManager((Context) this);
        recyclerView_search.setLayoutManager(linearLayoutManager_search);  // LayoutManager 설정


        customAdapter_search = new CustomAdapter(search_movie_list, search_movie_target_list);


        //===== [Click 이벤트 구현을 위해 추가된 코드] ==============
        customAdapter_search.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Movie data) {
                setDetailTab(data);
            }
        });
        //==========================================================

        recyclerView_search.setAdapter(customAdapter_search); // 어댑터 설정

    }

    public void setDetailTab(Movie data) {

        tabHost.setCurrentTab(2);

        TextView detail_movie_title = (TextView) findViewById(R.id.detail_movie_title);
        detail_movie_title.setText("영화 제목 : " + data.getOriginal_title());

        TextView detail_movie_actor = (TextView) findViewById(R.id.detail_movie_actor);
        detail_movie_actor.setText("영화 배우 : " + data.getCast());

        TextView detail_movie_overview = (TextView) findViewById(R.id.detail_movie_overview);
        String s = data.getOverview().substring(0, 70) + "...";

        detail_movie_overview.setText("개요 : " + s);

        startWebView(data);

    }

    public void startWebView(Movie movie) {

        webView = (WebView) findViewById(R.id.webView);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // allow the js

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //화면이 계속 켜짐
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.imdb.com/title/" + movie.getImdb_id() + "/reviews?ref_=tt_urv");



    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){

            webView.goBack();

            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
}