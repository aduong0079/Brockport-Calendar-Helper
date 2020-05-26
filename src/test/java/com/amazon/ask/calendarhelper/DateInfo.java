package com.amazon.ask.calendarhelper;

import lombok.Getter;

import java.util.Date;

@Getter
public class DateInfo {

    private final String name;
    private final Date date;
    private final Integer similarity;

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Integer getSimilarity() {
        return similarity;
    }

    DateInfo(String name, Date date, int similarity) {
        this.name = name;
        this.date = date;
        this.similarity = similarity;
    }
}