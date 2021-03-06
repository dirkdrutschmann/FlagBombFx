package de.bhtpaf.flagbomb.services;

import com.google.gson.*;
import de.bhtpaf.flagbomb.helper.classes.JWT;
import de.bhtpaf.flagbomb.helper.classes.User;
import de.bhtpaf.flagbomb.helper.classes.map.Grid;
import de.bhtpaf.flagbomb.helper.requests.HttpGetWithEntity;
import de.bhtpaf.flagbomb.helper.responses.GameHistoryEntry;
import de.bhtpaf.flagbomb.helper.responses.PlayingPair;
import de.bhtpaf.flagbomb.helper.responses.StdResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
                newUser = User.createFromJson(result);
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

    public User loginUser(User user)
    {
        String path = _apiUrl + "/Login";
        JWT jwtToken = null;

        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(path);
        request.setEntity(entity);
        try
        {
            CloseableHttpResponse response = _client.execute(request);
            System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                jwtToken = JWT.CreateFromJson(_getStringFromInputStream(responseEntity.getContent()));
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Login failed
        if (jwtToken == null)
        {
            return null;
        }

        path = _apiUrl + "/User";
        HttpGet getRequest = new HttpGet(path);
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + jwtToken.token);

        User loggedInUser = null;
        try
        {
            CloseableHttpResponse response = _client.execute(getRequest);
            System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(responseEntity.getContent());
                loggedInUser = User.createFromJson(result);
                loggedInUser.jwtToken = jwtToken;
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return  loggedInUser;
    }

    public boolean logoutUser(User user)
    {
        String path = _apiUrl + "/Login/Logout";

        HttpPost request = new HttpPost(path);

        request.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        boolean retValue = false;

        try
        {
            CloseableHttpResponse response = _client.execute(request);
            System.out.println(path + ": " + response.getStatusLine());

            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                retValue = true;
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return retValue;
    }

    public StdResponse setUser(User user)
    {
        String path = _apiUrl + "/User/" + user.id;
        System.out.println(user.toJson());
        StringEntity entity = new StringEntity(user.toJson(), ContentType.APPLICATION_JSON);

        HttpPost request = new HttpPost(path);
        request.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);
        request.setEntity(entity);

        StdResponse returnValue = new StdResponse();

        try
        {
            CloseableHttpResponse response = _client.execute(request);

            HttpEntity responseEntity = response.getEntity();

            returnValue = StdResponse.fromJson(_getStringFromInputStream(responseEntity.getContent()));

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            returnValue.success = false;
            returnValue.message = e.getMessage();
        }

        return returnValue;
    }

    public List<User> getLoggedInUsers(User user)
    {
        String path = _apiUrl + "/User/All";
        List<User> loggedInUsers = null;

        HttpGet httpGet = new HttpGet(path);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        try
        {
            CloseableHttpResponse response = _client.execute(httpGet);
            System.out.println(path + ": " + response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity responseEntity = response.getEntity();
                String responseString = _getStringFromInputStream(responseEntity.getContent());

                JsonArray jsonArray = JsonParser.parseString(responseString).getAsJsonArray();

                loggedInUsers = new ArrayList<>();

                for (JsonElement e : jsonArray)
                {
                    loggedInUsers.add(User.createFromJson(e.toString()));
                }
            }

            response.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return loggedInUsers;
    }

    public String getUserImage(User user)
    {
        String path = _apiUrl + "/User/" + user.id + "/picture";

        HttpGet httpGet = new HttpGet(path);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        String pictureBase64 = null;

        try
        {
            CloseableHttpResponse response = _client.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();

            if (response.getStatusLine().getStatusCode() == 200)
            {
                pictureBase64 = _getStringFromInputStream(responseEntity.getContent());
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return pictureBase64;
    }

    public String getApiUrl()
    {
        return _apiUrl;
    }

    public String getWebSocketUrl(String pairId, int userId)
    {
        URI uri = URI.create(_apiUrl);
        return "ws://" + uri.getAuthority() + uri.getPath() + "/game/ws/" + pairId + "/" + Integer.toString(userId);
    }

    public StdResponse sendPlayRequest(User requestingUser, int requestedUserId, Grid mapConfig)
    {
        String path = _apiUrl + "/user/PlayRequest/" + requestedUserId;
        StringEntity entity = new StringEntity(mapConfig.toJson(), ContentType.APPLICATION_JSON);

        return _getStdResponseFromPost(requestingUser, path, entity);
    }

    public StdResponse acceptIncomingPlayRequest(User user, int requestingUserId)
    {
        String path = _apiUrl + "/User/AcceptPlayRequest/" + requestingUserId;
        return _getStdResponseFromPost(user, path);
    }

    public StdResponse rejectIncomingPlayRequest(User user, int requestingUserId)
    {
        String path = _apiUrl + "/User/RejectPlayRequest/" + requestingUserId;
        return _getStdResponseFromPost(user, path);
    }

    public List<PlayingPair> getIncomingPlayRequest(User user)
    {
        String path = _apiUrl + "/User/PlayRequest/Incoming";
        return _getPlayRequest(path, user);
    }

    public List<PlayingPair> getOutgoingPlayRequest(User user)
    {
        String path = _apiUrl + "/User/PlayRequest/Outgoing";
        return _getPlayRequest(path, user);
    }

    public List<GameHistoryEntry> getGameHistory(User user)
    {
        String path = _apiUrl + "/Game/GameHistory/" + Integer.toString(user.id);
        List<GameHistoryEntry> gameHistoryEntries = new ArrayList<>();

        HttpGet request = new HttpGet(path);
        request.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        try
        {
            CloseableHttpResponse response = _client.execute(request);
            System.out.println(path + ": " + response.getStatusLine());

            String resp = _getStringFromInputStream(response.getEntity().getContent());

            if (response.getStatusLine().getStatusCode() != 200)
            {
                if (resp != null)
                {
                    throw new Exception(StdResponse.fromJson(resp).message);
                }
            }
            else
            {
                JsonArray history = JsonParser.parseString(resp).getAsJsonArray();

                for (JsonElement e : history)
                {
                    gameHistoryEntries.add(GameHistoryEntry.createFromJson(e.toString()));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        return gameHistoryEntries;
    }

    public boolean isPlayingPartnerConnected(User user, PlayingPair pair)
    {
        String path = _apiUrl + "/Game/isPartnerConnected/" + pair.id;
        HttpGet getRequest = new HttpGet(path);

        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        StdResponse returnValue;

        try
        {
            CloseableHttpResponse response = _client.execute(getRequest);
            System.out.println(path + ": " + response.getStatusLine());

            returnValue = StdResponse.fromJson(_getStringFromInputStream(response.getEntity().getContent()));

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return returnValue.success;
    }

    public boolean existsMail(String mail)
    {
        return _getBooleanGetRequest(_apiUrl + "/register/mail/" + mail);
    }

    public boolean existsUsername(String username)
    {
        return _getBooleanGetRequest(_apiUrl + "/register/user/" + username);
    }

    public Grid getGrid(Grid initData, User user)
    {
        Grid grid = initData;
        String path = _apiUrl + "/Game/map";

        HttpGetWithEntity httpGet = new HttpGetWithEntity(path);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        StringEntity entity = new StringEntity(grid.toJson(), ContentType.APPLICATION_JSON);
        httpGet.setEntity(entity);

        try
        {
            CloseableHttpResponse response = _client.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();

            System.out.println("getGrid: " + response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(responseEntity.getContent());
                grid = Grid.getFromJson(result);
            }

            EntityUtils.consume(responseEntity);

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return grid;
    }

    public boolean setGameOverStatus(User user, PlayingPair pair)
    {
        String path = _apiUrl + "/Game/setGameOver/" + pair.id;
        return _getStdResponseFromPost(user, path).success;
    }

    private List<PlayingPair> _getPlayRequest(String path, User user)
    {
        HttpGet getRequest = new HttpGet(path);

        List<PlayingPair> playRequest = new ArrayList<>();

        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        try
        {
            CloseableHttpResponse response = _client.execute(getRequest);
            System.out.println(path + ": " + response.getStatusLine());

            if (response.getStatusLine().getStatusCode() == 200)
            {
                String result = _getStringFromInputStream(response.getEntity().getContent());

                JsonArray pairsJson = JsonParser.parseString(result).getAsJsonArray();

                for (JsonElement e : pairsJson)
                {
                    playRequest.add(PlayingPair.createFromJson(e.toString()));
                }
            }

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return playRequest;
    }

    private StdResponse _getStdResponseFromPost(User user, String path)
    {
        return _getStdResponseFromPost(user, path, null);
    }

    private StdResponse _getStdResponseFromPost(User user, String path, StringEntity entity) {

        StdResponse returnValue = new StdResponse();

        HttpPost postRequest = new HttpPost(path);
        postRequest.setHeader(HttpHeaders.AUTHORIZATION, "bearer " + user.jwtToken.token);

        if (entity != null)
        {
            postRequest.setEntity(entity);
        }

        try
        {
            CloseableHttpResponse response = _client.execute(postRequest);
            System.out.println(path + ": " + response.getStatusLine());

            returnValue = StdResponse.fromJson(_getStringFromInputStream(response.getEntity().getContent()));

            response.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            returnValue.success = false;
            returnValue.message = e.getMessage();
        }

        return returnValue;
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
