package com.amazon.ask.calendarhelper.handlers;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class compareTester {
    public static void main(String[] args)
    {
        String stringOne = "wintersession II grades due from faculty";
        String stringTwo = "grades due";

        System.out.println(FuzzySearch.weightedRatio(stringOne, stringTwo));
        System.out.println(FuzzySearch.weightedRatio(stringOne, stringOne));
    }
}
