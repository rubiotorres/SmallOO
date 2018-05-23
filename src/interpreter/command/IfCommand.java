package interpreter.command;

import interpreter.expr.BoolExpr;
import interpreter.util.Arguments;
import interpreter.util.Instance;


public class IfCommand extends Command {
    
    private final BoolExpr expr;
    private Command doThen;
    private Command doElse;
    
    public IfCommand(BoolExpr expr, Command doThen, int line) {
        super(line);
        
        this.expr = expr;
        this.doThen = doThen;
    }
    
    public IfCommand(BoolExpr expr, Command doThen, Command doElse, int line) {
        super(line);
        
        this.expr = expr;
        this.doThen = doThen;
        this.doElse = doElse;
    }
    
    @Override
    public void execute(Instance self, Arguments args) {
        if(expr.expr(self, args)) {
            doThen.execute(self, args);
        }
        else {
            doElse.execute(self, args);
        }
    }
    
}
