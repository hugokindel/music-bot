package com.hugokindel.common.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class FormatUtil {
    public static long convertTimestamp(String timestamp) throws ParseException {
        DateFormat dateFormat = null;
        String[] splittedTimestamp = timestamp.split(":");
        int count = splittedTimestamp.length;
        String[] fixedTimestamp = new String[count];

        for (int i = 0; i < count; i++) {
            if (splittedTimestamp[i].length() == 1) {
                fixedTimestamp[i] = "0" + splittedTimestamp[i];
            } else if (splittedTimestamp[i].length() > 2 || splittedTimestamp[i].length() < 1) {
                throw new ParseException("Invalid timestamp, part is longer than 2 chars.", i);
            } else {
                fixedTimestamp[i] = splittedTimestamp[i];
            }
        }

        if (count == 1)
            dateFormat = new SimpleDateFormat("ss");
        else if (count == 2)
            dateFormat = new SimpleDateFormat("mm:ss");
        else if (count > 2)
            dateFormat = new SimpleDateFormat("HH:mm:ss");

        assert dateFormat != null;
        return dateFormat.parse(String.join(":", fixedTimestamp)).getTime() + TimeUnit.HOURS.toMillis(1);
    }
}
