
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TestBrockportMain
{

    public TestBrockportMain() {
    }
    public static void main(String[] args) throws IOException {
            BrockportCalendar cal = new BrockportCalendar();
            Calendar calendar = Calendar.getInstance();
            calendar.set(2019, Calendar.OCTOBER, 15);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            Date date = Date.from(OffsetDateTime.parse("2020-03-11T12:00:00-05:00", formatter).toInstant());

            Date eventDate = cal.getEventDate("finals", false);
            String eventName = cal.getEventName(date);

            System.out.println(eventDate);

            System.out.println(eventName);
    }
}
