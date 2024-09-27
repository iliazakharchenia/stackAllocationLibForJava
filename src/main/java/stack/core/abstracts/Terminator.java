package stack.core.abstracts;

public abstract class Terminator {
    private static boolean TO_TERMINATE_ALL = false;
    private boolean isInterruptedForever = false;

    public void interruptForever() {
        this.isInterruptedForever = true;
    }

    public boolean isInterruptedForever() {
        return this.isInterruptedForever;
    }

    public boolean toTerminate() {
        return Terminator.TO_TERMINATE_ALL;
    }

    public static void terminateAll() {
        Terminator.TO_TERMINATE_ALL = true;
    }
}
