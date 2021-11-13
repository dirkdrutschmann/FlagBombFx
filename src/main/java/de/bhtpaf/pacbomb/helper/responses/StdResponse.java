package de.bhtpaf.pacbomb.helper.responses;

import com.google.gson.GsonBuilder;
import de.bhtpaf.pacbomb.helper.classes.User;

public class StdResponse
{
    public boolean success;

    public String message;

    public static StdResponse fromJson(String json)
    {
        return new GsonBuilder().create().fromJson(json, StdResponse.class);
    }
}
