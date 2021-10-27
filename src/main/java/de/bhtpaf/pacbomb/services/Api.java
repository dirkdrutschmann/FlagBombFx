package de.bhtpaf.pacbomb.services;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
