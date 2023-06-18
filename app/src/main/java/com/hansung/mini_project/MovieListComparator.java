package com.hansung.mini_project;

import java.util.Collections;
import java.util.List;

// movie 객체 정렬 기준을 구현한 클래스
public class MovieListComparator {

    // 정렬 기준을 정의
    public static final int GENRE = 0;
    public static final int VOTE_POPULARITY = 1;
    public static final int VOTE_COUNT = 2;
    public static final int VOTE_AVERAGE = 3;

    // 매개변수로 받은 정렬값에 따라 다른 정렬 시행
    // 장르를 제외하고 내림차순으로 정렬
    public static void compare(List<Movie> movieList, int field) {
        if (field == GENRE) {
            Collections.sort(movieList, (a, b) -> a.getGenres().compareTo(b.getGenres()));
        }
        else if (field == VOTE_POPULARITY) {
            Collections.sort(movieList, (a, b) -> Double.compare(b.getPopularity(), a.getPopularity()));
        }
        else if (field == VOTE_COUNT) {
            Collections.sort(movieList, (a, b) -> Integer.compare(b.getVote_count(), a.getVote_count()));
        }
        else if (field == VOTE_AVERAGE) {
            Collections.sort(movieList, (a, b) -> Double.compare(b.getVote_average(), a.getVote_average()));
        }
    }
}
