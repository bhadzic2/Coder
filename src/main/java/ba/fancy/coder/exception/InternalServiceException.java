package ba.fancy.coder.exception;

/**
 * Class to describe internal service exceptions - should not be exposed to the caller
 */
public class InternalServiceException extends Exception {
    public InternalServiceException(String msg){
        super(msg);
    }
}
