package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class AnotherKothAlreadyRunningException extends KothException {


    /**
     * 
     */
    private static final long serialVersionUID = -3117081567539215250L;

    public AnotherKothAlreadyRunningException(){
        super(new MessageBuilder(Lang.KOTH_ERROR_ANOTHERALREADYRUNNING));
    }
}
