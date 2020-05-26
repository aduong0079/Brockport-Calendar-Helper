package com.amazon.ask.calendarhelper.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class FallBackIntentHandler implements RequestHandler{

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.FallbackIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "Sorry, I don't know that. Would you like to find a date from an event, " +
                "find an event from a date, " +
                "or find out how many days there are until the next event?";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Calendar Helper", speechText)
                .withReprompt(speechText)
                .build();
    }

}
