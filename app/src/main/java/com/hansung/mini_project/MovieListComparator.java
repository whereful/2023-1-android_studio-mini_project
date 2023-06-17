package com.hansung.mini_project;

import java.util.Collections;
import java.util.List;

public class MovieListComparator {

    public static final int GENRE = 0;
    public static final int VOTE_POPULARITY = 1;
    public static final int VOTE_COUNT = 2;
    public static final int VOTE_AVERAGE = 3;

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
