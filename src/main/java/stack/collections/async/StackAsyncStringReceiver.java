package stack.collections.async;

import stack.core.abstracts.Terminator;
import stack.core.exceptions.DataHolderThreadInterruptedException;

/**
 * Object of that class can hold a char[] data
 * in the embedded thread (stackHolder)
 * without need in active heap reference on
 * char[] data.
 * <p>
 * It holds a data in the 'sleeping'
 * thread stack and after method release()
 * execution it pushes a data from the stack to
 * the StringBuffer field, then returns a string
 * from it.
 *
 * @author Iliya Zakharchenia
 * @since 0.0.1
 * @see stack.collections.async.StackAsyncByteReceiver
 * @see stack.collections.async.StackAsyncIntegerReceiver
 */
public class StackAsyncStringReceiver extends Terminator {
    private boolean toRelease = false;
    private boolean toReturn = false;
    private final StringBuffer sb = new StringBuffer(0);

    public StackAsyncStringReceiver(String string) {
        append(string);
    }

    /**
     * Releases a data from the stack
     *
     */
    public String release() {
        if (isInterruptedForever()) throw new DataHolderThreadInterruptedException();
        String data = null;
        try {
            data = releaseData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    private String releaseData() throws InterruptedException {
        toRelease = true;
        while (!toReturn) Thread.sleep(0, 10);
        return this.sb.toString();
    }

    private void append(String str) {
        var arr = str.toCharArray();
        toRelease = false;

        // async process of adding to stack
        Thread stackHolder = new Thread(() -> {
            try {
                addDataToStack(arr.length - 1, arr);

                toReturn = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stackHolder.start();
    }

    private void addDataToStack(int index, char[] arr) throws InterruptedException {
        if (index<0) {
            while (!toRelease && !this.toTerminate()) Thread.sleep(0, 10);
            if (this.toTerminate()) {
                this.interruptForever();
                return;
            }
            return;
        }

        char ch = arr[index];

        addDataToStack(index-1, arr);

        while (!toRelease && !this.toTerminate()) Thread.sleep(0, 10);
        if (this.toTerminate()) {
            this.interruptForever();
            return;
        }
        this.sb.append(ch);
    }
}
