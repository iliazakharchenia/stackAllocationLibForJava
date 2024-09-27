package stack.collections.async;

/**
 * Object of that class can hold an int[] data
 * in the embedded thread (stackHolder) without
 * need in active heap reference on char[] data.
 * <p>
 * It holds a data in the 'sleeping'
 * thread stack and after method release()
 * execution it pushes a data from the stack to
 * the StringBuffer field, then returns a string
 * from it.
 *
 * @author Iliya Zakharchenia
 * @since 0.0.1
 * @see stack.collections.async.StackAsyncStringReceiver
 * @see stack.collections.async.StackAsyncIntegerReceiver
 */
public class StackAsyncIntegerReceiver {
    private boolean toRelease = false;
    private boolean toReturn = false;
    private int[] buffer;
    private int bufferIndex = 0;
    private int bufferLength = 0;

    public StackAsyncIntegerReceiver(int[] array) {
        bufferLength = array.length;
        append(array);
    }

    /**
     * Releases a data from the stack
     *
     */
    public int[] release() {
        buffer = new int[bufferLength];
        try {
            buffer = releaseData();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return buffer;
    }

    private int[] releaseData() throws InterruptedException {
        toRelease = true;
        while (!toReturn) Thread.sleep(0, 10);
        return this.buffer;
    }

    private void append(int[] arr) {
        toRelease = false;

        // async process of adding to stack
        Thread stackHolder = new Thread(() -> {
            try {
                addIntsToStack(arr.length - 1, arr);

                toReturn = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stackHolder.start();
    }

    private void addIntsToStack(int index, int[] arr) throws InterruptedException {
        if (index<0) {
            while (!toRelease) Thread.sleep(0, 10);
            return;
        }

        int num = arr[index];

        addIntsToStack(index-1, arr);

        while (!toRelease) Thread.sleep(0, 10);
        this.buffer[bufferIndex] = num;
        bufferIndex++;
    }
}