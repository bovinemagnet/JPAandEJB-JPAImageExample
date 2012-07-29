package au.com.sup.jpatest.exceptions;

public class NonexistentEntityException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public NonexistentEntityException(String message) {
        super(message);
    }
}
