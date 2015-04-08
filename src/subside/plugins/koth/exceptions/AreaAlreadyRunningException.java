package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class AreaAlreadyRunningException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8423094669308851410L;
	public AreaAlreadyRunningException(){
		super(new MessageBuilder(Lang.AREA_ALREADYRUNNING).build());
	}
	
	public AreaAlreadyRunningException(String area){
		super(new MessageBuilder(Lang.AREA_ALREADYRUNNING).area(area).build());
	}
}
