package jeext.controller.core;

import jeext.controller.core.annotations.WebMapping;

/**
 * <p>An {@link Enum} representing
 * the different possible HTTP methods
 * that a request can be made with/trough
 * <p>Used primarily in {@link WebMapping}s
 * to indicate which method they accept
 */
public enum HttpMethod {

	GET,
	POST,
	PUT,
	DELETE,
	
	CONNECT, // Do people even actually use these?
	HEAD,
	OPTIONS,
	TRACE;
	
}
