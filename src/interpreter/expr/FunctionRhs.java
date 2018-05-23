package interpreter.expr;

import interpreter.util.Arguments;
import interpreter.util.Instance;
import interpreter.value.FunctionValue;
import interpreter.value.Value;


public class FunctionRhs extends Rhs {
    
    private FunctionValue fv;
    
    public FunctionRhs(FunctionValue fv, int line) {
        super(line);
        this.fv = fv;
    }
    
    @Override
    public Value<?> rhs(Instance self, Arguments args) {
        return this.fv;
    }
    
}
