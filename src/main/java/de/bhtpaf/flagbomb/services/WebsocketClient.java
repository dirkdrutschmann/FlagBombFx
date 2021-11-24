package de.bhtpaf.flagbomb.services;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.bhtpaf.flagbomb.helper.BomberMan;
import de.bhtpaf.flagbomb.helper.classes.json.ExtendedItemJson;
import de.bhtpaf.flagbomb.helper.classes.json.BombermanJson;
import de.bhtpaf.flagbomb.helper.classes.json.ItemJson;
import de.bhtpaf.flagbomb.helper.classes.map.Grid;
import de.bhtpaf.flagbomb.helper.classes.map.items.Flag;
import de.bhtpaf.flagbomb.helper.classes.map.items.Gem;
import de.bhtpaf.flagbomb.helper.interfaces.eventListener.*;

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
    List<BomberManGeneratedListener> _BomberManGeneratedListeners = new ArrayList<>();
    List<GemGeneratedListener> _GemGeneratedListeners = new ArrayList<>();
    List<BomberManChangedListener> _BomberManChangedListeners = new ArrayList<>();
    List<GemCollectedListener> _GemCollectedListeners = new ArrayList<>();
    List<BombPlantedListener> _BombPlantedListeners = new ArrayList<>();
    List<GameOverSetListener> _GameOverSetListeners = new ArrayList<>();
    List<FlagCollectedListener> _FlagCollectedListeners = new ArrayList<>();
    List<FlagCapturedListener> _FlagCapturedListeners = new ArrayList<>();

    private URI _endpoint;

    Session userSession = null;

    public WebsocketClient(URI endpointURI) {
        _endpoint = endpointURI;
        _connect();
    }

    private void _connect()
    {
        try
        {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, _endpoint);
        }
        catch (Exception e)
        {
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
        System.out.println("RECEIVE: " + message);

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

            // Bomberman was sent
            else if (jObject.get("class").getAsString().equals("Bomberman"))
            {
                JsonObject jBombermann = jObject.get("objectValue").getAsJsonObject();
                JsonObject jFlag = jBombermann.get("ownedFlag").getAsJsonObject();

                BomberMan bomberMan = new BomberMan(
                    jBombermann.get("x").getAsInt(),
                    jBombermann.get("y").getAsInt(),
                    jBombermann.get("width").getAsInt(),
                    new Flag(
                            jFlag.get("x").getAsInt(),
                            jFlag.get("y").getAsInt(),
                            jFlag.get("flagSize").getAsInt(),
                            new GsonBuilder().create().fromJson(jFlag.get("color").getAsString(), Flag.Color.class),
                            jFlag.get("id").getAsString()
                    ),
                    jBombermann.get("userId").getAsInt()
                );

                for (BomberManGeneratedListener listener : _BomberManGeneratedListeners)
                {
                    listener.onBomberManGenerated(bomberMan);
                }
            }

            // New Gem
            else if(jObject.get("class").getAsString().equals("Gem"))
            {
                int x = jObject.get("objectValue").getAsJsonObject().get("square").getAsJsonObject().get("downLeft").getAsJsonObject().get("x").getAsInt();
                int y = jObject.get("objectValue").getAsJsonObject().get("square").getAsJsonObject().get("downLeft").getAsJsonObject().get("y").getAsInt();

                int width = jObject.get("objectValue").getAsJsonObject().get("square").getAsJsonObject().get("downRight").getAsJsonObject().get("x").getAsInt() - x;

                Gem gem = new Gem(
                    x,
                    y,
                    width,
                    jObject.get("objectValue").getAsJsonObject().get("imageIndex").getAsInt(),
                    jObject.get("objectValue").getAsJsonObject().get("itemId").getAsString()
                );

                for (GemGeneratedListener listener : _GemGeneratedListeners)
                {
                    listener.onGemGenerated(gem);
                }
            }

            // Gem collected
            else if (jObject.get("class").getAsString().equals("GemCollected"))
            {
                ItemJson gem = new GsonBuilder().create().fromJson(jObject.get("objectValue").getAsJsonObject(), ItemJson.class);

                for(GemCollectedListener listener : _GemCollectedListeners)
                {
                    listener.onGemCollected(gem);
                }
            }

            // Bomberman position changed
            else if(jObject.get("class").getAsString().equals("BombermanChanged"))
            {
                BombermanJson bomberman = new GsonBuilder().create().fromJson(jObject.get("objectValue").getAsJsonObject(), BombermanJson.class);

                for(BomberManChangedListener listener : _BomberManChangedListeners)
                {
                    listener.onBombermanChangedListener(bomberman);
                }
            }

            // Bomb planted
            else if (jObject.get("class").getAsString().equals("BombPlanted"))
            {
                ExtendedItemJson bombJson = new GsonBuilder().create().fromJson(jObject.get("objectValue").getAsJsonObject(), ExtendedItemJson.class);

                for (BombPlantedListener listener : _BombPlantedListeners)
                {
                    listener.onBombPlanted(bombJson);
                }
            }

            // Flag collected
            else if (jObject.get("class").getAsString().equals("FlagCollected"))
            {
                ExtendedItemJson flagJson = new GsonBuilder().create().fromJson(jObject.get("objectValue").getAsJsonObject(), ExtendedItemJson.class);

                for (FlagCollectedListener listener : _FlagCollectedListeners)
                {
                    listener.onFlagCollected(flagJson);
                }
            }

            // Flag captured
            else if (jObject.get("class").getAsString().equals("FlagCaptured"))
            {
                ExtendedItemJson flagJson = new GsonBuilder().create().fromJson(jObject.get("objectValue").getAsJsonObject(), ExtendedItemJson.class);

                for (FlagCapturedListener listener : _FlagCapturedListeners)
                {
                    listener.onFlagCaptured(flagJson);
                }
            }

            // GameOver set
            else if (jObject.get("class").getAsString().equals("GameOverSet"))
            {
                for (GameOverSetListener listener : _GameOverSetListeners)
                {
                    listener.onGameOverSet();
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

    public void addBomberManGeneratedListener(BomberManGeneratedListener listener)
    {
        _BomberManGeneratedListeners.add(listener);
    }

    public void addGemGeneratedListener(GemGeneratedListener listener)
    {
        _GemGeneratedListeners.add(listener);
    }

    public void addBomberManChangedListener(BomberManChangedListener listener)
    {
        _BomberManChangedListeners.add(listener);
    }

    public void addGemCollectedListener(GemCollectedListener listener)
    {
        _GemCollectedListeners.add(listener);
    }

    public void addBombPlantedListener(BombPlantedListener listener)
    {
        _BombPlantedListeners.add(listener);
    }

    public void addGameOverSetListener(GameOverSetListener listener)
    {
        _GameOverSetListeners.add(listener);
    }

    public void addFlagCollectedListener(FlagCollectedListener listener)
    {
        _FlagCollectedListeners.add(listener);
    }

    public void addFlagCapturedListener(FlagCapturedListener listener)
    {
        _FlagCapturedListeners.add(listener);
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        if (this.userSession == null)
        {
            _connect();
        }

        this.userSession.getAsyncRemote().sendText(message);

        System.out.println("SENT => " + message);
    }
}