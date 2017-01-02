package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class AreaAlreadyExistException extends CommandMessageException {

    /**
     * 
     */
    private static final long serialVersionUID = 2460236074387468684L;

    public AreaAlreadyExistException(String area){
        super(new MessageBuilder(Lang.AREA_ERROR_ALREADYEXISTS).area(area));
    }
}
