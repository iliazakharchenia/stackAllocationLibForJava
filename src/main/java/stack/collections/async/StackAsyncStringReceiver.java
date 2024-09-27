package stack.collections.async;

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
 * @see stack.collections.async.StackAsyncStringReceiver
 */
public class StackAsyncStringReceiver {
    private boolean toRelease = false;
    private boolean toReturn = false;
    private Thread stackHolder;
    private final StringBuffer sb = new StringBuffer();

    public StackAsyncStringReceiver(String string) {
        append(string);
    }

    /**
     * Releases a data from the stack
     *
     * @see stack.collections.async.StackAsyncStringReceiver
     */
    public String release() {
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
        stackHolder = new Thread(() -> {
            try {
                addCharsToStack(arr.length-1, arr);

                toReturn = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        stackHolder.start();
    }

    private void addCharsToStack(int index, char[] arr) throws InterruptedException {
        if (index<0) {
            while (!toRelease) Thread.sleep(0, 10);
            return;
        }

        char ch = arr[index];

        addCharsToStack(index-1, arr);

        while (!toRelease) Thread.sleep(1);
        this.sb.append(ch);
    }
}
