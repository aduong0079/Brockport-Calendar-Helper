package com.amazon.ask.calendarhelper;



import java.util.Date;

public class DateInfo {

    private final String name;
    private final Date date;
    private final Integer similarity;

    public String getName()
    {
        return name;
    }

    public Date getDate()
    {
        return date;
    }

    public Integer getSimilarity()
    {
        return similarity;
    }

    DateInfo(String name, Date date, int similarity)
    {
        this.name = name;
        this.date = date;
        this.similarity = similarity;
    }
//    DateInfo(String name, Date date)
//    {
//        this.name = name;
//        this.date = date;
//    }

    protected String getCleanEventName(){
        return name.replaceAll("Day \\d", "").replaceAll("[ ][(]\\d[)]", "");
    }
}