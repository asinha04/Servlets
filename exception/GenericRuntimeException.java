package org.kp.foundation.core.exception;

/**
 * @author Krassimir Boyanov on 1/10/18.
 */
public class GenericRuntimeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4917793033230522199L;
	public GenericRuntimeException(){
        super();
    }
    public GenericRuntimeException(final String cause){
        super(cause);
    }
    public GenericRuntimeException(Throwable throwable){
        super(throwable);
    }
    public GenericRuntimeException(String message, Throwable throwable){
        super(message, throwable);
    }
}
