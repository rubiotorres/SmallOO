package interpreter.command;

import interpreter.expr.BoolExpr;
import interpreter.util.Arguments;
import interpreter.util.Instance;


public class WhileCommand extends Command{
    
    private final BoolExpr expr;
    private final Command c;
    
    public WhileCommand(BoolExpr expr, Command c, int line) {
        super(line);
        
        this.expr = expr;
        this.c = c;
    }
    
    
    
    @Override
    public void execute(Instance self, Arguments args) {
        while(expr.expr(self, args)) {
            c.execute(self, args);
        }
    }
    
    
}
