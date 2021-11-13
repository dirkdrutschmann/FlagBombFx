package de.bhtpaf.pacbomb.services;

import de.bhtpaf.pacbomb.helper.interfaces.MessageHandler;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

@ClientEndpoint
public class WebsocketClient
{
    Session userSession = null;
    MessageHandler messageHandler;

    public WebsocketClient(URI endpointURI)
    {
        try
        {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            this.messageHandler = _getMessageHandler();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println(endpointURI.toString() + ": WebSocket init");
    }

    @OnOpen
    public void onOpen(Session userSession)
    {
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason)
    {
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message)
    {
        if (messageHandler != null)
        {
            messageHandler.handleMessage(message);
        }
    }

    private MessageHandler _getMessageHandler()
    {
        return new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                System.out.println(message);
            }
        };
    }

    public void sendMessage(String message)
    {
        this.userSession.getAsyncRemote().sendText(message);
    }
}
