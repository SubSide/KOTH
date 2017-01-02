package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class KothAlreadyExistException extends CommandMessageException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1271574000186783164L;

	public KothAlreadyExistException(KothHandler kothHandler, String koth){
		super(new MessageBuilder(Lang.KOTH_ERROR_ALREADYEXISTS).koth(kothHandler, koth));
	}
}
