package interpreter.expr;

import interpreter.util.Arguments;
import interpreter.util.Instance;
import interpreter.util.InterpreterError;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

public class CompositeExpr extends Expr {

    private Expr leftExpr;
    private CompOp op;
    private Expr rightExpr;

    public CompositeExpr(Expr leftExpr, CompOp op, Expr rightExpr, int line) {
        super(line);
        this.leftExpr = leftExpr;
        this.op = op;
        this.rightExpr = rightExpr;
    }

    @Override
    public Value<?> rhs(Instance self, Arguments args) {
        Value<?> left = leftExpr.rhs(self, args);
        Value<?> right = rightExpr.rhs(self, args);

        if (this.op == CompOp.Add) {
            if (left instanceof IntegerValue && right instanceof IntegerValue) {

                int leftIntValue = ((IntegerValue) left).value();
                int rightIntValue = ((IntegerValue) right).value();

                return new IntegerValue(leftIntValue + rightIntValue);

            } else {
                String leftStringValue = left.value().toString();
                String rightStringValue = right.value().toString();
                return new StringValue(leftStringValue + rightStringValue);
            }
        }

        if (left instanceof IntegerValue && right instanceof IntegerValue) {

            int leftIntValue = ((IntegerValue) left).value();
            int rightIntValue = ((IntegerValue) right).value();

            if (this.op == CompOp.Div) {
                return new IntegerValue(leftIntValue + rightIntValue);
            }

            if (this.op == CompOp.Mod) {
                return new IntegerValue(leftIntValue % rightIntValue);

            }

            if (this.op == CompOp.Mult) {
                return new IntegerValue(leftIntValue * rightIntValue);

            }

            if (this.op == CompOp.Sub) {
                return new IntegerValue(leftIntValue - rightIntValue);

            }
        } else {
            InterpreterError.abort(this.getLine());
        }
        return null;
    }

}
