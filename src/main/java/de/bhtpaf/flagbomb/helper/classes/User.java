package de.bhtpaf.flagbomb.helper.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

public class User
{
    public int id;

    public String username;

    public String prename;

    public String lastname;

    public String email;

    public String password;

    public String secret;

    public Date lastLogon;

    public Date registrationOn;

    public boolean isAdmin;

    public JWT jwtToken;

    public String userImageBase64;

    public User()
    { }

    public User(User user)
    {
        id = user.id;
        username = user.username;
        prename = user.prename;
        lastname = user.lastname;
        email = user.email;
        password = user.password;
        secret = user.secret;
        lastLogon = user.lastLogon;
        registrationOn = user.registrationOn;
        isAdmin = user.isAdmin;
    }

    public String toJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static User createFromJson(String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(json, User.class);
    }
}
