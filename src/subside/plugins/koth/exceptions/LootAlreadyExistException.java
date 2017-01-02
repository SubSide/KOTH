package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class LootAlreadyExistException extends CommandMessageException {

    /**
     * 
     */
    private static final long serialVersionUID = -4010376264837245319L;

    public LootAlreadyExistException(String loot){
        super(new MessageBuilder(Lang.LOOT_ERROR_ALREADYEXISTS).loot(loot));
    }
}
