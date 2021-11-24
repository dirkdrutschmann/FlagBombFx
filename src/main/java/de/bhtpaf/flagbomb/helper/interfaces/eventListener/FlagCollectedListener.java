package de.bhtpaf.flagbomb.helper.interfaces.eventListener;

import de.bhtpaf.flagbomb.helper.classes.json.ExtendedItemJson;

public interface FlagCollectedListener
{
    void onFlagCollected(ExtendedItemJson flagJson);
}
