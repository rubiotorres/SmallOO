package interpreter.expr;

import interpreter.util.Arguments;
import interpreter.util.Instance;


public abstract class BoolExpr {
    private final int line;
    
    protected BoolExpr(int line) {
        this.line = line;
    }
    
    public int getLine() {
        return line;
    }
    
    public abstract boolean expr(Instance self, Arguments args);
}
