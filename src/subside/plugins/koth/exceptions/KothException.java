package subside.plugins.koth.exceptions;

import lombok.Getter;
import subside.plugins.koth.utils.MessageBuilder;

public class KothException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = -8961720585538226812L;
    private @Getter String[] msg;

    public KothException(MessageBuilder builder) {
        this.msg = builder.build();
    }
    
    public KothException(String[] message){
        this.msg = new MessageBuilder(message).build();
    }
    
    public KothException(String message){
        this.msg = new MessageBuilder(message).build();
    }
}
