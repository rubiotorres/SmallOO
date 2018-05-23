package interpreter.expr;

import interpreter.util.Arguments;
import interpreter.util.Instance;

public class NotBoolExpr extends BoolExpr {

    private final BoolExpr expr;

    public NotBoolExpr(BoolExpr expr, int line) {
        super(line);
        this.expr = expr;
    }

    @Override
    public boolean expr(Instance self, Arguments args) {
        return !expr.expr(self, args);
    }

}
