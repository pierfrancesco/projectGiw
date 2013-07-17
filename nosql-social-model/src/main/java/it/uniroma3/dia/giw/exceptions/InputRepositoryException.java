package it.uniroma3.dia.giw.exceptions;

public class InputRepositoryException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public InputRepositoryException(final Exception e) {
    
        super(e);
    }
    
    public InputRepositoryException(final String message) {
    
        super(message);
    }
    
    public InputRepositoryException(final String message, final Exception e) {
    
        super(message, e);
    }
}