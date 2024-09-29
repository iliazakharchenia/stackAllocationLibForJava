package stack.collections.async;

import stack.core.abstracts.Terminator;
import stack.core.exceptions.DataHolderThreadInterruptedException;

/**
 * Object of that class can hold a byte[] data
 * in the embedded thread (stackHolder) without
 * need in active heap reference on byte[] data.
 * <p>
 * It holds a data in the 'sleeping'
 * thread stack and after method release()
 * execution it pushes a data from the stack to
 * the byte[] buffer and then returns it.
 *
 * @author Iliya Zakharchenia
 * @since 0.0.1
 * @see stack.collections.async.StackAsyncStringReceiver
 * @see stack.collections.async.StackAsyncIntegerReceiver
 */
public class StackAsyncByteReceiver extends Terminator {
    private boolean toRelease = false;
    private boolean toReturn = false;
    private byte[] buffer;
    private int bufferIndex = 0;
    private int bufferLength = 0;

    public StackAsyncByteReceiver(byte[] array) {
        bufferLength = array.length;
        append(array);
    }

    /**
     * Releases a data from the stack
     *
     */
    public byte[] release() {
        if (isInterruptedForever()) throw new DataHolderThreadInterruptedException();
        buffer = new byte[bufferLength];
        try {
            buffer = releaseData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    private byte[] releaseData() throws InterruptedException {
        toRelease = true;
        while (!toReturn) Thread.sleep(0, 10);
        return this.buffer;
    }

    private void append(byte[] arr) {
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

    private void addDataToStack(int index, byte[] arr) throws InterruptedException {
        if (index<0) {
            while (!toRelease && !this.toTerminate()) Thread.sleep(0, 10);
            if (this.toTerminate()) {
                this.interruptForever();
                return;
            }
            return;
        }

        byte num = arr[index];

        addDataToStack(index-1, arr);

        while (!toRelease && !this.toTerminate()) Thread.sleep(0, 10);
        if (this.toTerminate()) {
            this.interruptForever();
            return;
        }
        this.buffer[bufferIndex] = num;
        bufferIndex++;
    }
}
