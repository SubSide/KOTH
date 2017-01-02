package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class AreaNotExistException extends CommandMessageException {

    /**
     * 
     */
    private static final long serialVersionUID = 7000103830855773310L;

    public AreaNotExistException(String area){
        super(new MessageBuilder(Lang.AREA_ERROR_NOTEXIST).area(area));
    }
}
