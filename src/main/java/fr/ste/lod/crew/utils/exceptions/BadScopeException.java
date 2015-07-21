package fr.ste.lod.crew.utils.exceptions;

import fr.ste.lod.crew.utils.RdfModelUtils.Scope;

/**
 * When {@link Scope} enum type has an incorrect value
 * @author Christophe Gravier
 */
@SuppressWarnings("serial")
public class BadScopeException extends Exception {

	public BadScopeException(Scope scope) {
		super("Unexpected value for scope : "+scope);
	}
}
