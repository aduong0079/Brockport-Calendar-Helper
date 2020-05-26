package com.amazon.ask.calendarhelper.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.amazon.ask.model.interfaces.display.*;
import com.amazon.ask.calendarhelper.BrockportCalendar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.amazon.ask.calendarhelper.DateInfo;

import static com.amazon.ask.request.Predicates.intentName;

public class DateRequestIntentHandler implements IntentRequestHandler {


    public static void main(String[] args) throws IOException {
//        System.out.println("HELLOOOOO");
//        BrockportCalendar cal = new BrockportCalendar();
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(2020, Calendar.MARCH, 29);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//        Date date = Date.from(OffsetDateTime.parse("2020-03-11T12:00:00-05:00", formatter).toInstant());
//
//        Date eventDate = cal.getEventDates("finals");
//        String eventName = cal.getEventName(date);
//
//        System.out.println(eventDate);
//        System.out.println(eventName);
        String semester = "fall";
        BrockportCalendar cal = new BrockportCalendar(semester);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.OCTOBER, 15);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        Date date = Date.from(OffsetDateTime.parse("2020-03-11T12:00:00-05:00", formatter).toInstant());

        List<DateInfo> eventDates = cal.getEventDates("final exams begins");
        //String eventName = cal.getEventName(date);
        List<DateInfo> exampleDate = new ArrayList<>();
        final List<Integer>[] highestSimilarity = new List[]{new ArrayList<>()};
        highestSimilarity[0].add(0);
        eventDates.forEach(dateInfo -> {
            if (dateInfo.getSimilarity() > highestSimilarity[0].get(0))
            {
                exampleDate.add(dateInfo);
                highestSimilarity[0] = Collections.singletonList(dateInfo.getSimilarity());
            }

        });
        System.out.println(exampleDate.get(0).getName());
        System.out.println(exampleDate.get(0).getDate());
        String finalDate = "";

    }

    @Override
    public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
        return input.matches(intentName("DateRequestIntent").or(intentName("AMAZON.YesIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {

        String event = "";
        String college = "";
        String semester = "";
        String title = "Calendar Response";
        final Slot eventSlot = intentRequest.getIntent().getSlots().get("Event");
        final Slot collegeSlot = intentRequest.getIntent().getSlots().get("College");
        final Slot semesterSlot = intentRequest.getIntent().getSlots().get("Semester");

        if (eventSlot != null && collegeSlot != null && semesterSlot != null) {
            event = eventSlot.getValue().toLowerCase();
            college = collegeSlot.getValue().toLowerCase();
            semester = semesterSlot.getValue().toLowerCase();
        }

       //Date date = null;
        List<DateInfo> dateList = null;

        String finalDate = "";
        if(college.equals("brockport"))
        {
            try {

                List<DateInfo> eventDates = new BrockportCalendar(semester).getEventDates(event);
                List<Date> allDates = new ArrayList<>();
                eventDates.forEach(dateInfo -> {
                    allDates.add(dateInfo.getDate());
                });
                for(int i = 0; i < allDates.size(); i++)
                {
                    //System.out.println(allDates.get(i));
                    Date testDate = allDates.get(i);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
                    String eventDate = dateFormat.format(testDate);
                    if (i != allDates.size()-1)
                    {
                        finalDate = finalDate + eventDate + " ";
                    }
                    else
                    {
                        finalDate = finalDate + eventDate;
                    }
                    //System.out.println(eventDate);
                }
                System.out.println(finalDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
        //String eventDate = dateFormat.format(finalDate);
        // Headless device
        return input.getResponseBuilder()
                .withSpeech(finalDate)
                .withSimpleCard(title, finalDate)
                .withReprompt(finalDate)
                .build();
    }


    /**
     * Helper method to create a body template 3
     * @param title the title to be displayed on the template
     * @param primaryText the primary text to be displayed on the template
     * @param secondaryText the secondary text to be displayed on the template
     * @param image  the url of the image
     * @return Template
     */
//    private Template getBodyTemplate3(String title, String primaryText, String secondaryText, Image image) {
//        return BodyTemplate3.builder()
//                .withImage(image)
//                .withTitle(title)
//                .withTextContent(getTextContent(primaryText, secondaryText))
//                .build();
//    }

    /**
     * Helper method to create the image object for display interfaces
     * @param imageUrl the url of the image
     * @return Image that is used in a body template
     */
    private Image getImage(String imageUrl) {
        List<ImageInstance> instances = getImageInstance(imageUrl);
        return Image.builder()
                .withSources(instances)
                .build();
    }

    /**
     * Helper method to create List of image instances
     * @param imageUrl the url of the image
     * @return instances that is used in the image object
     */
    private List<ImageInstance> getImageInstance(String imageUrl) {
        List<ImageInstance> instances = new ArrayList<>();
        ImageInstance instance = ImageInstance.builder()
                .withUrl(imageUrl)
                .build();
        instances.add(instance);
        return instances;
    }

    /**
     * Helper method that returns text content to be used in the body template.
     * @param primaryText
     * @param secondaryText
     * @return RichText that will be rendered with the body template
     */
    private TextContent getTextContent(String primaryText, String secondaryText) {
        return TextContent.builder()
                .withPrimaryText(makeRichText(primaryText))
                .withSecondaryText(makeRichText(secondaryText))
                .build();
    }

    /**
     * Helper method that returns the rich text that can be set as the text content for a body template.
     * @param text The string that needs to be set as the text content for the body template.
     * @return RichText that will be rendered with the body template
     */
    private RichText makeRichText(String text) {
        return RichText.builder()
                .withText(text)
                .build();
    }

}
