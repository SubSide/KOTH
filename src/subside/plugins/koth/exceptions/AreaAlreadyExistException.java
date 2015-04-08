package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class AreaAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1271574000186783164L;
	public AreaAlreadyExistException(){
		super(new MessageBuilder(Lang.AREA_ALREADYEXISTS).build());
	}

	public AreaAlreadyExistException(String area){
		super(new MessageBuilder(Lang.AREA_ALREADYEXISTS).area(area).build());
	}
}
