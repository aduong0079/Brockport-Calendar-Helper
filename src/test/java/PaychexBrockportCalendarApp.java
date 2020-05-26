/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class PaychexBrockportCalendarApp extends DialogflowApp {

    public static void main(String[] args) {
        try {
            BrockportCalendar cal = new BrockportCalendar();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2019, Calendar.OCTOBER, 15);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            Date date = Date.from(OffsetDateTime.parse("2020-03-11T12:00:00-05:00", formatter).toInstant());

            Date eventDate = cal.getEventDate("finals", false);
            String eventName = cal.getEventName(date);

            System.out.println(eventDate);
            System.out.println(eventName);

        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PaychexBrockportCalendarApp.class);

    // Note: Do not store any state as an instance variable.
    // It is ok to have final variables where the variable is assigned a value in
    // the constructor but remains unchanged. This is required to ensure thread-
    // safety as the entry point (ActionServlet/ActionsAWSHandler) instances may
    // be reused by the server.

    @ForIntent("getdate")
    public ActionResponse getdate(ActionRequest request) throws IOException {
        String event = (String) request.getParameter("event");
        Tense tense = Tense.valueOf(((String) request.getParameter("tense")).toUpperCase());
        Date date = new BrockportCalendar().getEventDate(event, tense.equals(Tense.PAST));
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM d, yyyy");

        String response = "You asked about " + event + " with tense " + tense + ".\n";

        if (date == null) {
            response += "There are no events occurring with that name.";
        } else {
            response += "That occurs on " + dateFormat.format(date) + ".";
        }

        return getResponseBuilder(request).add(response).build();
    }

    @ForIntent("getevent")
    public ActionResponse getevent(ActionRequest request) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        Date date = Date.from(OffsetDateTime.parse((String) request.getParameter("date"), formatter).toInstant());
        String school = (String) request.getParameter("school");
        Tense tense = Tense.valueOf(((String) request.getParameter("tense")).toUpperCase());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMM d, yyyy");

        String event = new BrockportCalendar().getEventName(date);

        String response = "You asked about " + dateFormat.format(date) + " from " + school + " with tense " + tense + ".\n" +
                "The event is " + event + ".";

        return getResponseBuilder(request).add(response).build();
    }

    @ForIntent("getdaysuntilevent")
    public ActionResponse getdaysuntilevent(ActionRequest request) throws IOException {
        String event = (String) request.getParameter("event");
        String school = (String) request.getParameter("school");
        int days = new BrockportCalendar().getDaysUntilEvent(event);

        String response = "You asked about how many days there are until " + event + " at " + school + ".\n" +
                "There are " + days + " days.";

        return getResponseBuilder(request).add(response).build();
    }
}
