package de.bhtpaf.flagbomb.helper.classes.webSocketData;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class WebSocketCommunicationObject
{
    @SerializedName(value = "class")
    public String className;
    public Object objectValue;

    public String toJson()
    {
        return new GsonBuilder().create().toJson(this);
    }
}
