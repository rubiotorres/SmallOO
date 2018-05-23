package interpreter.expr;

import interpreter.util.Arguments;
import interpreter.util.Instance;
import interpreter.util.InterpreterError;
import interpreter.value.IntegerValue;
import interpreter.value.Value;

public class SingleBoolExpr extends BoolExpr {

    private Expr leftExpr;
    private RelOp op;
    private Expr rightExpr;

    public SingleBoolExpr(Expr leftExpr, RelOp op, Expr rightExpr, int line) {
        super(line);
        this.leftExpr = leftExpr;
        this.op = op;
        this.rightExpr = rightExpr;
    }

    @Override
    public boolean expr(Instance self, Arguments args) {
        Value<?> left = leftExpr.rhs(self, args);
        Value<?> right = rightExpr.rhs(self, args);

        if (left instanceof IntegerValue && right instanceof IntegerValue) {
            int leftInt = ((IntegerValue) left).value();
            int rightInt = ((IntegerValue) right).value();

            if (op == RelOp.Equal) {
                return leftInt == rightInt;
            }
            if (op == RelOp.GreaterEqual) {
                return leftInt >= rightInt;
            }
            if (op == RelOp.GreaterThan) {
                return leftInt > rightInt;
            }
            if (op == RelOp.LowerEqual) {
                return leftInt <= rightInt;
            }
            if (op == RelOp.LowerThan) {
                return leftInt < rightInt;
            }
            if (op == RelOp.Diff) {
                return leftInt != rightInt;
            }
        } else {
            InterpreterError.abort(this.getLine());
        }

        return false;
    }

}
