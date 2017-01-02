package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class IllegalKothNameException extends CommandMessageException {

    /**
     * 
     */
    private static final long serialVersionUID = -3197963801074086569L;

    public IllegalKothNameException(KothHandler kothHandler, String koth){
        super(new MessageBuilder(Lang.KOTH_ERROR_ILLEGALKOTHNAME).koth(kothHandler, koth));
    }
}
