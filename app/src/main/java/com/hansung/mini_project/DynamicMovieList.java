package com.hansung.mini_project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DynamicMovieList {

    public List<Movie> movieList;

    public static HashMap<String, Boolean> genre_hashMap = new HashMap<String, Boolean>();

    public static final String[] genre = {"Action", "Adventure", "Animation", "Comedy", "Crime", "Drama",
            "Family", "Fantasy", "History", "Mystery", "Science", "Thrill", "Western"};

    public static final boolean[] genre_boolean = {true, true, true, true, true, true,
            true, true, true, true, true, true, true};

    public int standard;
    public ArrayList<String> stringList = new ArrayList<String>();

    public List<Movie> string_target_movie_list = new ArrayList<Movie>();

    public DynamicMovieList() {

        for (String g : genre) {
            genre_hashMap.put(g, true);
        }

    }

    public void makeStringList() {

        if (stringList != null) {
            stringList.clear();
        }

        if (string_target_movie_list != null) {
            string_target_movie_list.clear();
        }


        int count = 0;
        for (int i = 0; i < 100; i += 1) {

//            System.out.println(movieList.get(i).getGenres());

            // 전체 개수가 20개 초과되면 종료
            if (count >= 20) {
                break;
            }

            if (genre_hashMap.get(movieList.get(i).getGenres()) == false) {
                continue;
            }

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

//            System.out.println(standard);
//            System.out.println(s);

            stringList.add(s);

            string_target_movie_list.add(movieList.get(i));

            count += 1;

        }
    }

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

    public static void selectGenre(String g, boolean b) {
        genre_hashMap.put(g, b);
    }

    public List<Movie> getString_target_movie_list() {
        return string_target_movie_list;
    }

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
