package subside.plugins.koth.exceptions;

import subside.plugins.koth.utils.MessageBuilder;

public class CommandMessageException extends KothException {
    /**
     * 
     */
    private static final long serialVersionUID = -1221177281034079047L;
    public CommandMessageException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CommandMessageException(String[] message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public CommandMessageException(MessageBuilder builder) {
        super(builder);
        // TODO Auto-generated constructor stub
    }
    
}
