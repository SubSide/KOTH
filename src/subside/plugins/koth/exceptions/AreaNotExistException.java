package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.MessageBuilder;

public class AreaNotExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5151644382896622615L;

	public AreaNotExistException(){
		super(new MessageBuilder(Lang.AREA_NOTEXIST).build());
	}
	
	public AreaNotExistException(String area){
		super(new MessageBuilder(Lang.AREA_NOTEXIST).area(area).build());
	}
}
