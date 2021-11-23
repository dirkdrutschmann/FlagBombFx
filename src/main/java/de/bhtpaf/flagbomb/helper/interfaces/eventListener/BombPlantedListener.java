package de.bhtpaf.flagbomb.helper.interfaces.eventListener;

import de.bhtpaf.flagbomb.helper.classes.json.BombJson;

public interface BombPlantedListener
{
    void onBombPlanted(BombJson bombJson);
}
