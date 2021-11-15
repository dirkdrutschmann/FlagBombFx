package de.bhtpaf.flagbomb.helper.classes;

import com.google.gson.Gson;

public class JWT {
    public String token;

    public static JWT CreateFromJson(String json)
    {
        return new Gson().fromJson(json, JWT.class);
    }
}
