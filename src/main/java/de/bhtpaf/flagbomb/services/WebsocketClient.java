package de.bhtpaf.flagbomb.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bhtpaf.flagbomb.helper.classes.map.Grid;
import de.bhtpaf.flagbomb.helper.interfaces.MapGeneratedListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.*;


/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
public class WebsocketClient {

    List<MapGeneratedListener> _MapGeneratedListeners = new ArrayList<>();


    Session userSession = null;

    public WebsocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message)
    {
        System.out.println(message);

        try
        {
            JsonObject jObject = JsonParser.parseString(message).getAsJsonObject();

            // Map was sent
            if (jObject.get("class").getAsString().equals("Grid"))
            {
                Grid grid = Grid.getFromJson(jObject.get("objectValue").toString());

                for (MapGeneratedListener listener : _MapGeneratedListeners)
                {
                    listener.onMapGenerated(grid);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session s, Throwable t) {
        t.printStackTrace();
    }

    public boolean isOpen()
    {
        return this.userSession != null && this.userSession.isOpen();
    }

    public void close()
    {
        if (isOpen())
        {
            try
            {
                this.userSession.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void addMapGeneratedListener(MapGeneratedListener listener)
    {
        _MapGeneratedListeners.add(listener);
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }
}