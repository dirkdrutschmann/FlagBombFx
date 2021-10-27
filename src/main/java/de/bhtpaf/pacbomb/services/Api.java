package de.bhtpaf.pacbomb.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.bhtpaf.pacbomb.helper.classes.User;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Api {
    private String _apiUrl;
    private CloseableHttpClient _client;

    public Api(String apiUrl)
    {
        _apiUrl = apiUrl;
        _client = HttpClients.createDefault();
    }

    public User registerUser(User user)
    {
        String path = _apiUrl + "/register";
        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(path);
        request.setEntity(entity);

        User newUser = null;

        try
        {
            CloseableHttpResponse response = _client.execute(request);
            System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(responseEntity.getContent());
                newUser = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create().fromJson(result, User.class);
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return newUser;
    }

    public boolean existsMail(String mail)
    {
        return _getBooleanGetRequest(_apiUrl + "/register/mail/" + mail);
    }

    public boolean existsUsername(String username)
    {
        return _getBooleanGetRequest(_apiUrl + "/register/user/" + username);
    }

    private String _getStringFromInputStream(InputStream inputStream)
    {
        try
        {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
            // StandardCharsets.UTF_8.name() > JDK 7
            return result.toString("UTF-8");
        }
        catch (Exception e)
        {
            return "";
        }

    }

    private boolean _getBooleanGetRequest(String path)
    {
        boolean returnValue = false;
        HttpGet httpGet = new HttpGet(path);
        try
        {
            CloseableHttpResponse response = _client.execute(httpGet);;
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                String content = _getStringFromInputStream(entity.getContent());

                returnValue = Boolean.parseBoolean(content);
            }

            EntityUtils.consume(entity);

            response.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnValue;
    }
}
