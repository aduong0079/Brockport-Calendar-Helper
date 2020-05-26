package com.amazon.ask.calendarhelper;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import me.xdrop.fuzzywuzzy.FuzzySearch;


public class BrockportCalendar {
    private static final String WEBSITE = "https://www.brockport.edu/academics/calendar/";

    private static final double DATE_SIMILARITY_THRESHOLD = 0.20;
    private final HashMap<String, Date> CALENDAR = new HashMap<>();
    private List<DateInfo> dates = new ArrayList<>();
    private static final int MAX_DATES = 3;

    /**
     * Initializes a connection with the Brockport calendar website, and retrieves and stores all dates and events.
     *
     * @throws IOException If the website connection cannot be succesfully established.
     */
    public BrockportCalendar(String semester) throws IOException {
        Document doc = Jsoup.connect(WEBSITE).get();
        Elements events = doc.getElementsByClass("ev");
        Elements dates = doc.getElementsByClass("date");

        // Since some dates have multiple events, create multiple key-value pairs with the same date, appending "Day X"
        // to the name, with X being the Xth occurrence of that event.

        for (int[] x = {0}; x[0] < dates.size(); x[0]++) {
            Iterable<Date> dateList = formatDate(dates.get(x[0]).text());

            dateList.forEach(date -> {
                String eventName = events.get(x[0]).text();

                if (CALENDAR.containsKey(eventName)) {
                    String duplicate = eventName + " Day 2";

                    for (int y = 2; CALENDAR.containsKey(duplicate.substring(0,duplicate.length()-1)+ y); y++){
                        duplicate = duplicate.substring(0, duplicate.length() - 1) + (y + 1);
                    }

                    eventName = duplicate;
                }
                if(semester.equals("spring"))
                {

                    if(isInSpring(date))
                    {
                        CALENDAR.put(eventName, date);
                    }
                }
                else if(semester.equals("fall"))
                {
                    if(isInFall(date))
                    {
                        CALENDAR.put(eventName, date);
                    }
                }
                else
                {
                    CALENDAR.put(eventName, date);
                }


            });
        }
    }

    /**
     * Formats a string of all date and time variants that the Brockport calendar uses into a format parsable by
     * {@link Date}. If a date range is detected, multiple strings will be formatted.
     *
     * The following are examples of formats that are understood:
     * July 4, 2020
     * August 23, 2019, Friday
     * September 26 – 28, 2019
     * October 14 & 15, 2019, Monday & Tuesday
     * August 26, 2019, Monday, 8 AM
     * April 10, 2020, Friday, 9 AM – 5 PM
     *
     * @param dateString A date and time string in one of the expected input formats.
     * @return A list of parsed dates.
     * @throws InputMismatchException If the given date and time string is not in a recognized
     */
    private ArrayList<Date> formatDate(String dateString) throws InputMismatchException {
        List<String> dateSplit = Arrays.asList(dateString.split(" "));
        SimpleDateFormat dateFormat;
        ArrayList<Date> dates = new ArrayList<>();

        try {
            switch (dateSplit.size()) {
                case 3:
                    // e.g. July 4, 2020
                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
                    dates.add(dateFormat.parse(dateString));
                    break;
                case 4:
                    // e.g. August 23, 2019, Friday
                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy, EEEEE");
                    dates.add(dateFormat.parse(dateString));
                    break;
                case 5:
                    // e.g. September 26 – 28, 2019
                    //only added at the start date for now, only one event of this form so not a huge deal
                    dateString = dateSplit.get(0) + " " + dateSplit.get(1) + ", " + dateSplit.get(4);
                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
                    dates.add(dateFormat.parse(dateString));
                    break;
                case 8:
                    // e.g. October 14 & 15, 2019, Monday & Tuesday
                    dateFormat = new SimpleDateFormat("MMMMM d yyyy");
                    Date startDate = dateFormat.parse(String.format("%s %s %s", dateSplit.get(0),
                                                                                dateSplit.get(1),
                                                                                dateSplit.get(4)));

                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
                    Date endDate = dateFormat.parse(String.format("%s %s %s", dateSplit.get(0),
                                                                              dateSplit.get(3),
                                                                              dateSplit.get(4)));

                    while (!startDate.after(endDate)) {
                        dates.add(startDate);
                        startDate = DateUtils.addDays(startDate, 1);
                    }

                    break;
                case 6:
                    // e.g. August 26, 2019, Monday, 8 AM
                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy, EEEEE, hh a");
                    dates.add(dateFormat.parse(dateString));
                    break;
                case 9:
                    // e.g. April 10, 2020, Friday, 9 AM – 5 PM
                    // Current method: chop off the second time and run it through like normal.
                    dateSplit = dateSplit.subList(0, 3);
                    dateString = String.join(" ", dateSplit);
                    dateFormat = new SimpleDateFormat("MMMMM d, yyyy");
                    dates.add(dateFormat.parse(dateString));
                    break;
                default:
                    throw new InputMismatchException("Input " + dateString + " not in expected format.");
            }
        } catch (ParseException e) {
        } catch (InputMismatchException e) {
            throw new InputMismatchException("Input " + dateString + " not in expected format.");
        }

        return dates;
    }

    /**
     * Retrieves the {@link Date} for an event. Since multiple events with the same name can occur,
     *
     * @param eventName The event name.
     * @return The {@link Date} for an event.
     *         null if no event is found.
     */
    public List<DateInfo> getEventDates(String eventName) {
        // Remove all non-alphanumeric characters from the event name.
        eventName = eventName.toLowerCase().replaceAll("[^a-z0-9]", "");

        // Iterate through every key-value pair and compare the event name similarity to the event in the current loop
        // state.
        for (Map.Entry<String, Date> entry : CALENDAR.entrySet()) {
            // Remove all non-alphanumeric characters from the current event.
            String event = entry.getKey();
            String tempEvent = entry.getKey().toLowerCase().replaceAll("[^a-z0-9]", "");
            Date tempDate = entry.getValue();
            insertDate(new DateInfo(event,
                    tempDate,
                    FuzzySearch.weightedRatio(eventName, tempEvent)));
        }

        return dates;
    }

    /**
     * Retrieves the event name for a given {@link Date}, considering only the date.
     *
     * @param eventDate The {@link Date} to consider.
     * @return The name of the event.
     *         null if no event is found.
     */
    public String getEventName(Date eventDate) {
        List<String> eventList = new ArrayList<>();
        String builtString = "";
        // Iterate through every key-value pair and compare the current date to eventDate. If they are the same date,
        // return it.
        CALENDAR.forEach((currEventName, date) -> {
            if (DateUtils.isSameDay(date, eventDate)) {
                System.out.println(currEventName);

                eventList.add(currEventName);
            }
        });
        if(eventList.size() > 1)
        {
            for(int i = 0; i < eventList.size(); i++)
            {
                if(i != eventList.size() - 1)
                {
                    builtString = builtString + eventList.get(i) + " and\n";
                }
                else
                {
                    builtString = builtString + eventList.get(i);
                }
            }
        }
        else
        {
            builtString = eventList.get(0);
        }


        return builtString;


    }

    public String getCleanEventName(String name){
        return name.replaceAll("Day \\d", "").replaceAll("[ ][(]\\d[)]", "");
    }

    boolean isInSpring(Date testDate) {
        int theMonth = testDate.getMonth();
        if(theMonth >= 0 && theMonth <= 4)
        {
            return true;
        }
        return false;
    }

    boolean isInFall(Date testDate) {
        int theMonth = testDate.getMonth();
        if(theMonth >= 7 && theMonth <= 11)
        {
            return true;
        }
        return false;
    }
    public int getDaysUntilEvent(String event) {
        LocalDate eventDate = getEventDates(event).get(dates.size() - 1)
                .getDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return (int) LocalDate.now().until(eventDate, ChronoUnit.DAYS);
    }

    private void insertDate(DateInfo dateInfo) {
        dates.sort(Comparator.comparing(o -> ((DateInfo) o).getSimilarity()).reversed());

        if (dateInfo.getSimilarity() >= DATE_SIMILARITY_THRESHOLD) {
            if (dates.size() < MAX_DATES) {
                dates.add(dateInfo);
            } else {
                int lastIndex = dates.size() - 1;

                if (dateInfo.getSimilarity().compareTo(dates.get(lastIndex).getSimilarity()) >= 0) {
                    dates.set(lastIndex, dateInfo);
                }
            }
        }

        dates.sort(Comparator.comparing(o -> ((DateInfo) o).getSimilarity()).reversed());
    }
}
