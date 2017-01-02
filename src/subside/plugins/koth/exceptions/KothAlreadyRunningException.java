package subside.plugins.koth.exceptions;

import subside.plugins.koth.modules.KothHandler;
import subside.plugins.koth.modules.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class KothAlreadyRunningException extends KothException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8423094669308851410L;

	public KothAlreadyRunningException(KothHandler kothHandler, String koth){
		super(new MessageBuilder(Lang.KOTH_ERROR_ALREADYRUNNING).koth(kothHandler, koth));
	}
}
