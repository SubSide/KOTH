package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class KothNotExistException extends CommandMessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5151644382896622615L;

	public KothNotExistException(KothHandler kothHandler, String koth){
		super(new MessageBuilder(Lang.KOTH_ERROR_NOTEXIST).koth(kothHandler, koth));
	}
}
