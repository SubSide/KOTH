package subside.plugins.koth.exceptions;

import lombok.Getter;

public class CommandMessageException extends RuntimeException {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1221177281034079047L;
    private @Getter String[] msg;

    public CommandMessageException(String[] msg) {
        this.msg = msg;
    }
}
