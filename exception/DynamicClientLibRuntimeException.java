package org.kp.foundation.core.exception;

/**
 * @author Rajesh Dwivedi on 10/18/17.
 */
public class DynamicClientLibRuntimeException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2220521179945057588L;
	public DynamicClientLibRuntimeException(){
        super();
    }
    public DynamicClientLibRuntimeException(final String cause){
        super(cause);
    }
    public DynamicClientLibRuntimeException(Throwable throwable){
        super(throwable);
    }
    public DynamicClientLibRuntimeException(String message, Throwable throwable){
        super(message, throwable);
    }
}
