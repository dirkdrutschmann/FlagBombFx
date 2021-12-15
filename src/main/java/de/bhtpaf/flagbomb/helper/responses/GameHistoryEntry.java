package de.bhtpaf.flagbomb.helper.responses;

import com.google.gson.GsonBuilder;
import java.util.Date;

public class GameHistoryEntry
{
    public Date requestedOn;

    public String opponent;

    public static GameHistoryEntry createFromJson(String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(json, GameHistoryEntry.class);
    }
}
