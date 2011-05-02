package org.tbull.util.dev;


/** Hmmm, what was the use case for this again...?
 *
 *
 *
 */
public class CantWorkLikeThisException extends RuntimeException {

    private static final long serialVersionUID = 8736137684087471585L;

    public CantWorkLikeThisException() {
    }

    public CantWorkLikeThisException(String message) {
        super(message);
    }

    public CantWorkLikeThisException(Throwable cause) {
        super(cause);
    }

    public CantWorkLikeThisException(String message, Throwable cause) {
        super(message, cause);
    }

}
