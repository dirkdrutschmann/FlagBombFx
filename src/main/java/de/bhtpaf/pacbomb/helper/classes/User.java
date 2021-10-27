package de.bhtpaf.pacbomb.helper.classes;

import com.google.gson.Gson;

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
}
