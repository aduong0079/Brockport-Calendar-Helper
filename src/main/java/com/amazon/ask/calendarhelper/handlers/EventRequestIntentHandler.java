package com.amazon.ask.calendarhelper.handlers;

import com.amazon.ask.calendarhelper.BrockportCalendar;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.amazon.ask.request.Predicates.intentName;

public class EventRequestIntentHandler implements IntentRequestHandler {

    public static void main(String[] args) throws IOException {
        String semester = "";
        BrockportCalendar cal = new BrockportCalendar(semester);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.DECEMBER, 16);
        Date testDate = calendar.getTime();
        String eventDates = cal.getCleanEventName(cal.getEventName(testDate));
        System.out.println(eventDates);
    }

    @Override
    public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
        return input.matches(intentName("EventRequestIntent").or(intentName("AMAZON.YesIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
        //given in yyyy-mm-dd
        Date date = new Date(0,0,0);
        String semester = "";
        String eventName = "";
        String title = "Calendar Response";

        final Slot dateSlot = intentRequest.getIntent().getSlots().get("Date");
        String eventDate = dateSlot.getValue().toLowerCase();

        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            BrockportCalendar brockportCalendar = new BrockportCalendar(semester);
            eventName = brockportCalendar.getCleanEventName(brockportCalendar.getEventName(date));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return input.getResponseBuilder()
                .withSpeech(eventName)
                .withSimpleCard(title, eventName)
                .withReprompt(eventName)
                .build();
    }


}
