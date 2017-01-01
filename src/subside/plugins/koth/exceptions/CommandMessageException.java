package subside.plugins.koth.exceptions;

import lombok.Getter;
import subside.plugins.koth.utils.MessageBuilder;

public class CommandMessageException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = -1221177281034079047L;
    private @Getter String[] msg;

    public CommandMessageException(MessageBuilder builder) {
        this.msg = builder.build();
    }
    
    public CommandMessageException(String[] message){
        this.msg = new MessageBuilder(message).build();
    }
    
    public CommandMessageException(String message){
        this.msg = new MessageBuilder(message).build();
    }
    
}
