package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class KothNotExistException extends CommandMessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5151644382896622615L;

	public KothNotExistException(String koth){
		super(new MessageBuilder(Lang.KOTH_ERROR_NOTEXIST).koth(koth));
	}
}
