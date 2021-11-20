package de.bhtpaf.flagbomb.helper.interfaces.eventListener;

import de.bhtpaf.flagbomb.helper.classes.json.GemJson;
import de.bhtpaf.flagbomb.helper.classes.map.items.Gem;

public interface GemCollectedListener
{
    void onGemCollected(GemJson gem);
}
