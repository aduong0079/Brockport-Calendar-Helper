package com.amazon.ask.calendarhelper.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class HelpIntentHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "I can help you Find an event from a date, " +
                "find a Date from an event, " +
                "or tell you how far away the event is";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Calendar Helper", speechText)
                .withReprompt(speechText)
                .build();
    }

}
