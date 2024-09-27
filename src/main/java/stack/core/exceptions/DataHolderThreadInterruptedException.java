package stack.core.exceptions;

public class DataHolderThreadInterruptedException extends RuntimeException {
    public DataHolderThreadInterruptedException() {
        super("Thread holds data is interrupted by global interrupter");
    }

    public DataHolderThreadInterruptedException(String message) {
        super(message);
    }
}
