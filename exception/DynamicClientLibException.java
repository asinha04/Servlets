package org.kp.foundation.core.exception;

import java.io.Serializable;

/**
 * @author Rajesh Dwivedi on 10/17/17.
 */
public class DynamicClientLibException extends Exception implements Serializable{
    private static final long serialVersionUID = 176L;

    private String msg;
    private Exception ex;
    public DynamicClientLibException(){
        super();
    }
    public DynamicClientLibException(String msg){
        super(msg);
        this.msg = msg;
    }
    public DynamicClientLibException(Exception ex){
        super(ex);
        this.ex = ex;
    }
    public DynamicClientLibException(String msg, Exception ex){
        super(msg, ex);
        this.msg = msg;
        this.ex = ex;
    }

    public String getMsg() {
        return msg;
    }

    public Exception getEx() {
        return ex;
    }
}
