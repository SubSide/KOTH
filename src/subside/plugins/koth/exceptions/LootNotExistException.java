package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class LootNotExistException extends CommandMessageException {
    /**
     * 
     */
    private static final long serialVersionUID = 8865234325340269175L;

    public LootNotExistException(String loot){
        super(new MessageBuilder(Lang.LOOT_ERROR_NOTEXIST).loot(loot));
    }
}
