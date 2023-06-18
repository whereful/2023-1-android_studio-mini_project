package com.hansung.mini_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
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

    // 전체 movieList를 저장하는 전역 객체 설정
    public static DynamicMovieList dynamicMovieList = new DynamicMovieList();

    // 메인 화면, 검색 화면에 나타나는 recyclerView 변수 설정
    RecyclerView recyclerView_main, recyclerView_search;

    // 메인 화면, 검색 화면에 나타나는 linearLayout 변수 설정
    LinearLayoutManager linearLayoutManager_main, linearLayoutManager_search;

    // 메인 화면, 검색 화면에 나타나는 customAdapter 변수 설정
    CustomAdapter customAdapter_main, customAdapter_search;

    // 상세 화면에 나타나는 웹뷰 설정
    WebView webView;

    // 메인 화면에서 나타나는 정렬 메뉴
    final String[] menu = {"장르", "인기", "투표 개수", "평점"};


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 메인 화면, 검색 화면, 상세 화면 구성
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


        // 탭이 바뀌었을 때 메인 화면이거나 상세 화면인 경우 검색창이 나타나지 않도록 설정
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                if(s.equals("MAIN") || s.equals("DETAIL")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

                    imm.hideSoftInputFromWindow(tabHost.getWindowToken(), 0);
                }
            }
        });

        // 첫 번째 탭 화면 구성 - dynamicMovieList 설정, 메인 화면에 recyclerView 설정
        tabHost.setCurrentTab(0);

        try {
            dynamicMovieList.setMovieList(makeMovieList());

            dynamicMovieList.movieListSort(MovieListComparator.VOTE_POPULARITY);
            dynamicMovieList.makeStringListAndModifyTargetMovieList();

            makeRecyclerView_main();

        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // 세 번째 화면 구성 - 평점 가장 높은 영화 출력
        setDetailTab(MainActivity.dynamicMovieList.findMaxVoteAverageMovie());
        // ====================================


        // 첫 번째 화면 구성 - 정렬 버튼 구성
        Button button_standard = (Button) findViewById(R.id.button_standard);
        button_standard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("정렬하기");
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setItems(menu, new DialogInterface.OnClickListener() {

                    // 정렬하기 버튼을 클릭하였을 때 전체 movieList를 정렬
                    // 나타나는 문자열과 movie 객체 정의
                    // customAdapter에서 사용하는 movie들 변경
                    // recyclerView에 변경 사항 반영
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dynamicMovieList.movieListSort(i);
                        dynamicMovieList.makeStringListAndModifyTargetMovieList();

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


            // 장르 선택 버튼을 클릭하였을 때 장르 선택 여부 변경
            // 나타나는 문자열과 movie 객체 정의
            // customAdapter에서 사용하는 movie들 변경
            // recyclerView에 변경 사항 반영

            @Override
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("장르 선택");
                dlg.setIcon(R.mipmap.ic_launcher);

                dlg.setMultiChoiceItems(DynamicMovieList.genre, DynamicMovieList.genre_boolean, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        DynamicMovieList.selectGenre(DynamicMovieList.genre[i], b);

                        dynamicMovieList.makeStringListAndModifyTargetMovieList();

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

        // 자동완성검색창 - 영화 정의
        AutoCompleteTextView movie_search = (AutoCompleteTextView) findViewById(R.id.movie_name);
        List<String> movie_name_array = new ArrayList<String>();
        for (Movie m : dynamicMovieList.movieList) {
            movie_name_array.add(m.getOriginal_title());
        }
        // 영화 제목 arrayList를 정의하고 중복 제거
        movie_name_array = movie_name_array.stream().distinct().collect(Collectors.toList());

        ArrayAdapter<String> movie_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                movie_name_array);
        movie_search.setAdapter(movie_adapter);


        // 자동완성검색창 - 배우 정의
        AutoCompleteTextView actor_search = (AutoCompleteTextView) findViewById(R.id.actor_name);
        List<String> actor_name_array = new ArrayList<String>();
        for (Movie m : dynamicMovieList.movieList) {
            actor_name_array.add(m.getCast());
        }
        // 영화 배우 arrayList를 정의하고 중복 제거
        actor_name_array = actor_name_array.stream().distinct().collect(Collectors.toList());

        ArrayAdapter<String> actor_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                actor_name_array);
        actor_search.setAdapter(actor_adapter);

        // 검색 버튼 이벤트 설정
        Button button_search = (Button) findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Movie> movies_filtered = new ArrayList<Movie>();

                // 입력된 영화 제목, 배우명을 문자열로 변경
                String movie_name = movie_search.getText().toString();
                String actor_name = actor_search.getText().toString();


                // 전체 movieList 순회
                for (Movie m : dynamicMovieList.movieList) {

                    // 입력된 값이 둘 다 없는 경우 다음으로 넘어감
                    if (movie_name.isEmpty() && actor_name.isEmpty()) {
                        continue;
                    }

                    // 영화 제목만 입력된 경우
                    else if (movie_name.isEmpty() == false && actor_name.isEmpty()) {

                        // 영화 제목에 대응되는 movie 객체를 저장
                        if (movie_name.equals(m.getOriginal_title())) {
                            movies_filtered.add(m);
                        }
                    }

                    // 영화 배우만 입력된 경우
                    else if (movie_name.isEmpty() && actor_name.isEmpty() == false) {

                        // 배우명에 대응되는 movie 객체 저장
                        if (actor_name.equals(m.getCast())) {
                            movies_filtered.add(m);
                        }
                    }

                    // 둘 다 입력된 경우
                    else if (movie_name.isEmpty() == false && actor_name.isEmpty() == false) {

                        // 듈 전부 대응되는 movie 객체 저장
                        if (movie_name.equals(m.getOriginal_title()) && actor_name.equals(m.getCast())) {
                            movies_filtered.add(m);
                        }
                    }
                }

                // 저장한 movie 객체들을 영화 평점 순으로 정렬
                MovieListComparator.compare(movies_filtered, MovieListComparator.VOTE_AVERAGE);

                int count = 0;
                ArrayList<String> movie_explain = new ArrayList<>();

                List<Movie> movie_explain_target = new ArrayList<Movie>();

                // 저장한 movie 객체들 중에서 최대 5개만 선택되도록 함
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

                // 저장한 movie 객체들과 문자열들을 바탕으로 recyclerView를 만듬
                makeRecyclerView_search(movie_explain, movie_explain_target);
            }
        });


        // 세 번째 화면 구성 - 평점 저장
        // 평점이 변경되면 텍스트 화면도 변경되도록 함
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

    // csv 파일에서 movie 객체들을 조회하여 저장한 후 리스트로 반환
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

        //--- LayoutManager 중 아래 LinearLayoutManager를 사용
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

        //--- LayoutManager 중 아래 LinearLayoutManager를 사용
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

    // 상세 화면 설정
    public void setDetailTab(Movie data) {

        tabHost.setCurrentTab(2);

        // 영화 제목 출력
        TextView detail_movie_title = (TextView) findViewById(R.id.detail_movie_title);
        detail_movie_title.setText("영화 제목 : " + data.getOriginal_title());

        // 배우명 출력
        TextView detail_movie_actor = (TextView) findViewById(R.id.detail_movie_actor);
        detail_movie_actor.setText("영화 배우 : " + data.getCast());

        // 개요 출력
        TextView detail_movie_overview = (TextView) findViewById(R.id.detail_movie_overview);
        String s = data.getOverview().substring(0, 70) + "...";

        detail_movie_overview.setText("개요 : " + s);

        // 버튼 클릭 시 평점이 저장된 웹 화면으로 이동
        Button detail_movie_review_button = (Button)findViewById(R.id.link_button);
        detail_movie_review_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWebBrowser(data);
            }
        });

        // api 24에서 오류 발생, api 34의 경우 정상적으로 작동
//        startWebView(data);
    }

    // movie에 대응되는 평점들이 저장된 사이트로 이동하는 메소드
    public void startWebBrowser(Movie movie) {

        String url = "https://www.imdb.com/title/" + movie.getImdb_id() + "/reviews?ref_=tt_urv";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }

    // api 24에서 오류 발생, api 34의 경우 정상적으로 작동

//    private class WebViewClientClass extends WebViewClient {
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return true;
//        }
//    }
//
//    public void startWebView(Movie movie) {
//        webView = (WebView) findViewById(R.id.webView);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        webView.loadUrl("https://www.imdb.com/title/" + movie.getImdb_id() + "/reviews?ref_=tt_urv");
//
//        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new WebViewClientClass());
//
//    }
//
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//            webView.goBack();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}