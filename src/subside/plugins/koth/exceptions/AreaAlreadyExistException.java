package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class AreaAlreadyExistException extends CommandMessageException {

    /**
     * 
     */
    private static final long serialVersionUID = 2460236074387468684L;

    public AreaAlreadyExistException(){
        super(new MessageBuilder(Lang.AREA_ERROR_ALREADYEXISTS).build());
    }

    public AreaAlreadyExistException(String area){
        super(new MessageBuilder(Lang.AREA_ERROR_ALREADYEXISTS).area(area).build());
    }
}
