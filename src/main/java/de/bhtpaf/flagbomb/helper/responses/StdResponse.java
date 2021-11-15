package de.bhtpaf.flagbomb.helper.responses;

import com.google.gson.GsonBuilder;

public class StdResponse
{
    public boolean success;

    public String message;

    public static StdResponse fromJson(String json)
    {
        return new GsonBuilder().create().fromJson(json, StdResponse.class);
    }
}
