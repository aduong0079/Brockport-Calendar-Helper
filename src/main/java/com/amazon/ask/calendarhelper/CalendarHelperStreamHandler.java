package com.amazon.ask.calendarhelper;

import com.amazon.ask.Skill;
import com.amazon.ask.Skills;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.calendarhelper.handlers.*;

public class CalendarHelperStreamHandler extends SkillStreamHandler {

    private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new DateRequestIntentHandler(),
                        new EventRequestIntentHandler(),
                        new DaysUntilRequestIntentHandler(),
                        new HelpIntentHandler(),
                        new LaunchRequestHandler(),
                        new SessionEndedRequestHandler(),
                        new FallBackIntentHandler())
                // Add your skill id below and uncomment to enable skill ID verification
                // .withSkillId("")
                .build();
    }

    public CalendarHelperStreamHandler() {
        super(getSkill());
    }

}
