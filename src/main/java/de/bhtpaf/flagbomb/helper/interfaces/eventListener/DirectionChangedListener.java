package de.bhtpaf.flagbomb.helper.interfaces.eventListener;

import de.bhtpaf.flagbomb.helper.BomberMan;
import de.bhtpaf.flagbomb.helper.Dir;

public interface DirectionChangedListener
{
    void onDirectionChanged(BomberMan player, Dir oldDirection, Dir newDirection);
}
