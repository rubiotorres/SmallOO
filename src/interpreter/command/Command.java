package interpreter.command;

import interpreter.util.Instance;
import interpreter.util.Arguments;

public abstract class Command {

    private int line;

    protected Command(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public abstract void execute(Instance instance, Arguments args);

}
