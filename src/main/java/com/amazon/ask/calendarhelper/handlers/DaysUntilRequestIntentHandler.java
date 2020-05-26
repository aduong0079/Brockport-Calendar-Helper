package com.amazon.ask.calendarhelper.handlers;

import com.amazon.ask.calendarhelper.BrockportCalendar;
import com.amazon.ask.calendarhelper.DateInfo;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.slu.entityresolution.Resolution;
import com.amazon.ask.model.slu.entityresolution.Resolutions;
import com.amazon.ask.model.slu.entityresolution.Value;
import com.amazon.ask.model.slu.entityresolution.ValueWrapper;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;

public class DaysUntilRequestIntentHandler implements IntentRequestHandler
{
    public static void main(String[] args) throws IOException {
        String semester = "";
        LocalDate lDate = LocalDate.now();
        if(semester.equals(""))
        {

            int theMonth = lDate.getMonthValue();
            if(theMonth >= 1 && theMonth <= 5)
            {
                semester = "spring";
            }
            if(theMonth >= 7 && theMonth <= 12)
            {
                semester = "fall";
            }
        }
        BrockportCalendar cal = new BrockportCalendar(semester);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 15);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        Date date = Date.from(OffsetDateTime.parse("2020-03-11T12:00:00-05:00", formatter).toInstant());



        Scanner scanThing = new Scanner(System.in);
        String event = scanThing.nextLine();
        List<DateInfo> eventDates = cal.getEventDates(event);
        System.out.println(eventDates.get(0).getName());
        System.out.println(eventDates.get(1).getName());
        System.out.println(eventDates.get(2).getName());
        List<DateInfo> exampleDate = new ArrayList<>();
        final List<Integer>[] highestSimilarity = new List[]{new ArrayList<>()};
        highestSimilarity[0].add(0);
        System.out.println(eventDates.size());
        List<Integer> countList = new ArrayList<>();
        countList.add(0);
        eventDates.forEach(dateInfo -> {
            if (dateInfo.getSimilarity() > highestSimilarity[0].get(0))
            {
                System.out.println(highestSimilarity[0].get(0));
                if(countList.get(0) == 0)
                {
                    exampleDate.add(dateInfo);
                    System.out.println("In this if statement");
                }
                exampleDate.set(0, dateInfo);
                highestSimilarity[0] = Collections.singletonList(dateInfo.getSimilarity());


            }
            System.out.println(highestSimilarity[0].get(0));
            System.out.println(eventDates.get(countList.get(0)).getName());
            System.out.println(eventDates.get(countList.get(0)).getDate());
            System.out.println(eventDates.get(countList.get(0)).getSimilarity());
            countList.set(0, countList.get(0) + 1);

        });
        System.out.println("This is what was picked VVVVV");
        System.out.println(exampleDate.get(0).getName());
        System.out.println(exampleDate.get(0).getDate());
        String finalDate = "";
        String theEvent = exampleDate.get(0).getName();
        Date foundDate = exampleDate.get(0).getDate();
        LocalDate finalLocalDate = new java.sql.Date(foundDate.getTime()).toLocalDate();
        int daysUntil = (int) lDate.until(finalLocalDate, ChronoUnit.DAYS);
        if(daysUntil < 0)
        {
            System.out.println(theEvent + " has already passed");
        }
        else if(daysUntil == 0)
        {
            System.out.println(theEvent + " is today");
        }
        else if(daysUntil == 1)
        {
            System.out.println(theEvent + " is tomorrow");
        }
        else
        {
            System.out.println("There are " + daysUntil + " days" + " until " + theEvent);
        }

    }

    @Override
    public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
        return input.matches(intentName("DaysUntilRequestIntent").or(intentName("AMAZON.YesIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {

        String event = "";
        String semester = "";

        String myTempName= "";
        String myFinalEventValue = "";

        LocalDate lDate = LocalDate.now();
        if(semester.equals(""))
        {

            int theMonth = lDate.getMonthValue();
            if(theMonth >= 1 && theMonth <= 5)
            {
                semester = "spring";
            }
            if(theMonth >= 7 && theMonth <= 12)
            {
                semester = "fall";
            }
        }
        int tempMatch = 0;
        int fuzzyMatch = 0;

        String title = "Calendar Response";
        final Slot eventSlot = intentRequest.getIntent().getSlots().get("Event");

        event = eventSlot.getValue().toLowerCase();
        List<Resolution> myList;
        Resolutions res;
        res = eventSlot.getResolutions();
        myList = res.getResolutionsPerAuthority();
        List<List<ValueWrapper>> myValues = new ArrayList();

        for(int y = 0; y < myList.size(); y++)
        {
            myValues.add(myList.get(y).getValues());
        }
        if(myValues.get(0).size() > 1)
        {

            for(int z = 0; z < myValues.size(); z++)
            {
                List<ValueWrapper> myValueInner = myValues.get(z);

                for(int i = 0; i < myValueInner.size(); i++)
                {
                    Value myTempValue = myValueInner.get(i).getValue();
                    myTempName = myTempValue.getName();
                    fuzzyMatch = FuzzySearch.weightedRatio(event, myTempName);
                    if(fuzzyMatch > tempMatch)
                    {
                        tempMatch = fuzzyMatch;
                        myFinalEventValue = myTempName;
                    }
                }
            }
        }
        else
        {
            List<ValueWrapper> myValueInner = myList.get(0).getValues();
            Value myValue = myValueInner.get(0).getValue();
            myFinalEventValue = myValue.getName();
        }
        if(semester.equals("spring") && myFinalEventValue.equals("break"))
        {
            myFinalEventValue = "Spring Recess Begins";
        }

        String finalOutput = "";
        try {

            BrockportCalendar brockportCalendar = new BrockportCalendar(semester);
            List<DateInfo> eventDates = brockportCalendar.getEventDates(myFinalEventValue);
            List<DateInfo> exampleDate = new ArrayList<>();
            final List<Integer>[] highestSimilarity = new List[]{new ArrayList<>()};
            highestSimilarity[0].add(0);
            System.out.println(eventDates.size());
            List<Integer> countList = new ArrayList<>();
            countList.add(0);
            eventDates.forEach(dateInfo -> {
                if (dateInfo.getSimilarity() > highestSimilarity[0].get(0))
                {
                    System.out.println(highestSimilarity[0].get(0));
                    if(countList.get(0) == 0)
                    {
                        exampleDate.add(dateInfo);
                    }
                    exampleDate.set(0, dateInfo);
                    highestSimilarity[0] = Collections.singletonList(dateInfo.getSimilarity());


                }
                countList.set(0, countList.get(0) + 1);

            });

            Date foundDate = exampleDate.get(0).getDate();
            String theEvent = myFinalEventValue;
            theEvent = brockportCalendar.getCleanEventName(theEvent);

            // Headless device
            LocalDate finalLocalDate = new java.sql.Date(foundDate.getTime()).toLocalDate();
            int daysUntil = (int) lDate.until(finalLocalDate, ChronoUnit.DAYS);

            if(daysUntil < 0)
            {
                finalOutput = finalOutput + theEvent + " has already passed";
            }
            else if(daysUntil == 0)
            {
                finalOutput = finalOutput + theEvent + " is today";
            }
            else if(daysUntil == 1)
            {
                finalOutput = finalOutput + theEvent + " is tomorrow";
            }
            else
            {
                finalOutput = finalOutput + "There are " + daysUntil + " days" + " until " + theEvent;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



        return input.getResponseBuilder()
                .withSpeech(finalOutput)
                .withSimpleCard(title, finalOutput)
                .withReprompt(finalOutput)
                .build();
    }


}
