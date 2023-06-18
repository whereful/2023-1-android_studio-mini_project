package com.hansung.mini_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DynamicMovieList {

    // csv 파일에 존재하는 모든 movie 객체를 저장하는 리스트
    public List<Movie> movieList;

    // movieList에서 장르가 선택되었는지를 나타내는 hashMap
    public static HashMap<String, Boolean> genre_hashMap = new HashMap<String, Boolean>();

    // 앱 화면에 장르가 체크되었는지를 나타나기 위해 사용되는 배열
    public static final String[] genre = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Drama",
            "Family", "Fantasy", "History", "Mystery", "Science", "Thrill", "Western"};

    // 앱 화면에 장르가 체크되었는지를 나타나기 위해 사용되는 배열
    public static final boolean[] genre_boolean = {true, true, true, true, true, true,
            true, true, true, true, true, true, true};

    // 정렬 기준을 저장한 값
    public int standard;

    // 메인 화면에 출력되는 문자열들을 저장하는 arrayList
    public ArrayList<String> stringList = new ArrayList<String>();

    // 메인 화면에 출력되는 문자열에 대응되는 movie 객체를 저장하는 arrayList
    public List<Movie> string_target_movie_list = new ArrayList<Movie>();

    // 생성자 : 모든 장르가 선택되도록 설정
    public DynamicMovieList() {

        for (String g : genre) {
            genre_hashMap.put(g, true);
        }

    }

    // 메인 화면에 출력되는 문자열을 만들고 대응되는 movie들을 변경하는 메소드
    public void makeStringListAndModifyTargetMovieList() {

        // 문자열을 저장하는 arrayList가 정의되어 있으면 전부 삭제
        if (stringList != null) {
            stringList.clear();
        }

        // 문자열에 대응되는 movie들이 정의되어 있으면 전부 삭제
        if (string_target_movie_list != null) {
            string_target_movie_list.clear();
        }


        // 메인 화면에 출력되는 원소 개수를 저장하는 변수
        int count = 0;

        // 전체 movieList에 대해 순회
        for (int i = 0; i < 100; i += 1) {

            // 전체 개수가 20개 초과되면 종료
            if (count >= 20) {
                break;
            }

            // 장르가 선택되지 않으면 다음 movie 객체를 살펴봄
            if (genre_hashMap.get(movieList.get(i).getGenres()) == false) {
                continue;
            }


            // 정렬 기준에 따라 문자열 정의
            String s;
            if (standard == MovieListComparator.VOTE_POPULARITY) {
                s = (count + 1) + " : " + movieList.get(i).getOriginal_title() + "\n" +
                        "장르 : " + movieList.get(i).getGenres() + "\n" +
                        "인기 : " + (int)movieList.get(i).getPopularity();
            } else if (standard == MovieListComparator.VOTE_COUNT) {
                s = (count + 1) + " : " + movieList.get(i).getOriginal_title() + "\n" +
                        "장르 : " + movieList.get(i).getGenres() + "\n" +
                        "투표 개수 : " + (int)movieList.get(i).getVote_count();
            }
            else {
                s = (count + 1) + " : " + movieList.get(i).getOriginal_title() + "\n" +
                        "장르 : " + movieList.get(i).getGenres() + "\n" +
                        "평균 평점 : " + movieList.get(i).getVote_average();
            }


            // 정의한 문자열들을 arrayList에 저장
            stringList.add(s);

            // 문자열에 대응되는 movie 객체를 arrayList에 저장
            string_target_movie_list.add(movieList.get(i));

            // 원소가 추가되어 개수 증가
            count += 1;

        }
    }

    // 매개변수를 기준으로 설정하고 설정된 기준에 따라 전체 movieList를 정렬함
    public void movieListSort(int s) {
        this.standard = s;
        MovieListComparator.compare(movieList, standard);
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;

    }

    public ArrayList<String> getStringList() {
        return stringList;
    }

    // 장르가 선택되면 선택된 결과를 hashMap에 반영
    public static void selectGenre(String g, boolean b) {
        genre_hashMap.put(g, b);
    }

    public List<Movie> getString_target_movie_list() {
        return string_target_movie_list;
    }

    // 전체 movieList에서 평점이 가장 높은 movie 객체 반환
    public Movie findMaxVoteAverageMovie() {
        Movie target = movieList.get(0);

        for (Movie m : movieList) {
            if (target.getVote_average() < m.getVote_average()) {
                target = m;
            }
        }
        return target;
    }
}
