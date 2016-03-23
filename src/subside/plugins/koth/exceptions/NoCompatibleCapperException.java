package subside.plugins.koth.exceptions;

import subside.plugins.koth.Lang;
import subside.plugins.koth.utils.MessageBuilder;

public class NoCompatibleCapperException extends CommandMessageException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2862733567569819426L;

	public NoCompatibleCapperException(){
		super(new MessageBuilder(Lang.KOTH_ERROR_NO_COMPATIBLE_CAPPER));
	}
}
