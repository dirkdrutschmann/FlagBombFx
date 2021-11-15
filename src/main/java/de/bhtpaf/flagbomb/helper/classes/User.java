package de.bhtpaf.flagbomb.helper.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
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
        userImageBase64 = user.userImageBase64;
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

    public void encodeImage(File file){
        String encodedFile = null;
        if(file != null){
            try {
                FileInputStream fileInputStreamReader = new FileInputStream(file);
                byte[] bytes = new byte[(int) file.length()];
                fileInputStreamReader.read(bytes);
                encodedFile = Base64.getEncoder().encodeToString(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        userImageBase64 = encodedFile;
    }
}
