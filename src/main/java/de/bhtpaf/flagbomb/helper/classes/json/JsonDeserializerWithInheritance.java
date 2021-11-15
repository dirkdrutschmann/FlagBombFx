package de.bhtpaf.flagbomb.helper.classes.json;

import com.google.gson.*;
import de.bhtpaf.flagbomb.helper.classes.map.Coord;
import de.bhtpaf.flagbomb.helper.classes.map.Tile;
import de.bhtpaf.flagbomb.helper.classes.map.Wall;

import java.lang.reflect.Type;

public class JsonDeserializerWithInheritance<T> implements JsonDeserializer
{
    @Override
    public Tile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // simple Tile
        if (jsonObject.get("isDestroyable") == null)
        {
            return new Tile(
                new Coord(
                    jsonObject.getAsJsonObject("downLeft").get("x").getAsInt(),
                    jsonObject.getAsJsonObject("downLeft").get("y").getAsInt()
                ),
                jsonObject.get("width").getAsInt(),
                de.bhtpaf.flagbomb.helper.classes.map.Type.FREE
            );
        }

        return new Wall(
            new Coord(
                    jsonObject.getAsJsonObject("downLeft").get("x").getAsInt(),
                    jsonObject.getAsJsonObject("downLeft").get("y").getAsInt()
            ),
            jsonObject.get("width").getAsInt(),
            jsonObject.get("isDestroyable").getAsBoolean()
        );
    }
}
