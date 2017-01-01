package subside.plugins.koth.exceptions;

import subside.plugins.koth.utils.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class KothAlreadyRunningException extends KothException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8423094669308851410L;

	public KothAlreadyRunningException(String koth){
		super(new MessageBuilder(Lang.KOTH_ERROR_ALREADYRUNNING).koth(koth));
	}
}
