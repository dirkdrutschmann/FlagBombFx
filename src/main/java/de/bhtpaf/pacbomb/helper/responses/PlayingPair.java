package de.bhtpaf.pacbomb.helper.responses;

import com.google.gson.GsonBuilder;
import de.bhtpaf.pacbomb.helper.classes.User;

import java.util.Date;

public class PlayingPair
{
    public String id;

    public User requestingUser;

    public User requestedUser;

    public Date requestTime;

    public PlayingPairStatus status;

    public static PlayingPair createFromJson(String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(json, PlayingPair.class);
    }
}
