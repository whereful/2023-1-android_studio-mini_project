package com.hansung.mini_project;

import com.opencsv.bean.CsvBindByName;

// csv 파일에 대응되는 movie 객체 설정
public class Movie {

    @CsvBindByName
    int id;

    @CsvBindByName
    String genres;

    @CsvBindByName
    String imdb_id;

    @CsvBindByName
    String original_title;

    @CsvBindByName
    String overview;

    @CsvBindByName
    double popularity;

    @CsvBindByName
    double vote_average;

    @CsvBindByName
    int vote_count;

    @CsvBindByName
    String cast;

    public int getId() {
        return id;
    }

    public String getGenres() {
        return genres;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public String getCast() {
        return cast;
    }


}
