 package syntatic;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import lexical.Lexeme;
import lexical.TokenType;
import lexical.LexicalAnalysis;

import interpreter.command.*;
import interpreter.expr.*;
import interpreter.value.*;
import interpreter.util.*;
/**
 *
 * @author Rubio Torres & Renan Siman
 */
public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;

    public SyntaticAnalysis(LexicalAnalysis lex) throws IOException {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    public Command start() throws IOException {
        Command c = procCode();
        matchToken(TokenType.END_OF_FILE);
        return c;
    }

    private void matchToken(TokenType type) throws IOException {
        // System.out.println("Match token: " + current.type + " == " + type + "?");
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inválido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema não esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }
    
    //<code> ::= { <statement> }
    private CommandsBlock procCode() throws IOException {
        CommandsBlock cb = new CommandsBlock();
        while(current.type == TokenType.IF
           || current.type == TokenType.WHILE
           || current.type == TokenType.SYSTEM
           || current.type == TokenType.SELF
           || current.type == TokenType.ARGS
           || current.type == TokenType.NAME) {
            Command c = procStatement();
            cb.addCommand(c);
        }
        return cb;
    }
    
    //<statement> ::= <if> | <while> | <cmd>
    private Command procStatement() throws IOException {
        Command c = null;
        if(current.type == TokenType.IF) {
            c = procIf();
        }
        else if(current.type == TokenType.WHILE) {
            c = procWhile();
        }
        else {
            c = procCmd();
        }
        return c;
    }
    
    //<if> ::= if '(' <boolexpr> ')' '{' <code> '}' [else '{' <code> '}' ]
    private IfCommand procIf() throws IOException {
        IfCommand ic;
        BoolExpr be;
        CommandsBlock doThen;
        CommandsBlock doElse = null;
        int line = lex.getLine();
        matchToken(TokenType.IF);
        matchToken(TokenType.OPEN_PAR);
        be = procBoolExpr();
        matchToken(TokenType.CLOSE_PAR);
        matchToken(TokenType.OPEN_CUR);
        doThen = procCode();
        matchToken(TokenType.CLOSE_CUR);
        if(current.type == TokenType.ELSE) {
            matchToken(TokenType.ELSE);
            matchToken(TokenType.OPEN_CUR);
            doElse = procCode();
            matchToken(TokenType.CLOSE_CUR);
        }
        ic = new IfCommand(be, doThen, doElse, line);
        return ic;
    }
    
    //<while> ::= while '(' <boolexpr> ')' '{' <code> '}'
    private WhileCommand procWhile() throws IOException {
        WhileCommand wc;
        BoolExpr be;
        CommandsBlock cb;
        int line = lex.getLine();
        matchToken(TokenType.WHILE);
        matchToken(TokenType.OPEN_PAR);
        be = procBoolExpr();
        matchToken(TokenType.CLOSE_PAR);
        matchToken(TokenType.OPEN_CUR);
        cb = procCode();
        matchToken(TokenType.CLOSE_CUR);
        wc = new WhileCommand(be, cb, line);
        return wc;
    }
    
    //<cmd> ::= <access> ( <assign> | <call> ) ';'
    private AssignCommand procCmd() throws IOException {
        AccessPath path = procAccess();
        AssignCommand ac = null;
        if(current.type == TokenType.ASSIGN) {
            ac = procAssign(path);
        }
        else if(current.type == TokenType.OPEN_PAR) {
            int line = lex.getLine();
            FunctionCallExpr fce = procCall(path);
            ac = new AssignCommand(null, fce, line);
        }
        else {
            showError();
        }
        matchToken(TokenType.DOT_COMMA);
        return ac;
    }
    
    //<access> ::= <var> { '.' <name> }
    private AccessPath procAccess() throws IOException {
        int line = lex.getLine();
        String name = procVar();
        AccessPath path = new AccessPath(name, line);
        while(current.type == TokenType.DOT) {
            matchToken(TokenType.DOT);
            name = procName();
            path.addName(name);
        }
        return path;
    }
    
    //<assign> ::= '=' <rhs>
    private AssignCommand procAssign(AccessPath path) throws IOException {
        int line = lex.getLine();
        matchToken(TokenType.ASSIGN);
        Rhs rhs = procRhs();
        
        AssignCommand ac = new AssignCommand(path, rhs, line); 
        return ac;
    }
    
    //<call> ::= '(' [ <rhs> { ',' <rhs> } ] ')'
    private FunctionCallExpr procCall(AccessPath path) throws IOException {
        FunctionCallExpr fce = new FunctionCallExpr(path, lex.getLine());
        matchToken(TokenType.OPEN_PAR);
        if(current.type == TokenType.FUNCTION || //function
           current.type == TokenType.NUMBER || //expr -> term -> factor -> number
           current.type == TokenType.STRING || //expr -> term -> factor -> string
           current.type == TokenType.SYSTEM || //expr -> term -> factor -> access -> var -> system
           current.type == TokenType.SELF || //expr -> term -> factor -> access -> var -> self
           current.type == TokenType.ARGS || //expr -> term -> factor -> access -> var -> args
           current.type == TokenType.NAME || //expr -> term -> factor -> access -> var -> name
           current.type == TokenType.OPEN_PAR) {
            Rhs rhs = procRhs();
            fce.addParam(rhs);
            while(current.type == TokenType.COMMA) {
                matchToken(TokenType.COMMA);
                rhs = procRhs();
                fce.addParam(rhs);
            }
        }
        matchToken(TokenType.CLOSE_PAR);
        return fce;
    } 
    
    //<boolexpr> ::= [ '!' ] <cmpexpr> [ ('&' | '|') <boolexpr> ]
    private BoolExpr procBoolExpr() throws IOException {
        BoolExpr be = null;
        int line = lex.getLine();
        if(current.type == TokenType.NOT) {
            matchToken(TokenType.NOT);
            
        }
        procCmpExpr();
        if(current.type == TokenType.AND || current.type == TokenType.OR) {
            if(current.type == TokenType.AND) {
                matchToken(TokenType.AND);
            }
            else {
                matchToken(TokenType.OR);
            }
            procBoolExpr();
        }
        return be;
    }
    
    //<cmpexpr> ::= <expr> <relop> <expr>
    private SingleBoolExpr procCmpExpr() throws IOException {
        SingleBoolExpr sbe;
        int line = lex.getLine();
        Expr leftExpr = procExpr();
        RelOp op = procRelop();
        Expr rightExpr = procExpr();
        sbe = new SingleBoolExpr(leftExpr, op, rightExpr, line);
        return sbe;
    }
    
    //<relop> ::= '==' | '!=' | '<' | '>' | '<=' | '>='
    private RelOp procRelop() throws IOException {
        if(current.type == TokenType.EQUAL) {
            matchToken(TokenType.EQUAL);
            return RelOp.Equal;
        }
        else if(current.type == TokenType.DIFF) {
            matchToken(TokenType.DIFF);
            return RelOp.Diff;
        }
        else if(current.type == TokenType.LOWER) {
            matchToken(TokenType.LOWER);
            return RelOp.LowerThan;
        }
        else if(current.type == TokenType.GREATER) {
            matchToken(TokenType.GREATER);
            return RelOp.GreaterThan;
        }
        else if(current.type == TokenType.LOWER_EQ) {
            matchToken(TokenType.LOWER_EQ);
            return RelOp.LowerEqual;
        }
        else if(current.type == TokenType.GREATER_EQ) {
            matchToken(TokenType.GREATER_EQ);
            return RelOp.GreaterEqual;
        }
        
        return null;
    }
    
    //<rhs> ::= <function> | <expr>
    private Rhs procRhs() throws IOException {
        Rhs rhs;
        if(current.type == TokenType.FUNCTION) {
            rhs = procFunction();
        }
        else {
            rhs = procExpr();
        }
        return rhs;
    }
    
    //<function> ::= function '{' <code> [ return <rhs> ';' ] '}'
    private FunctionRhs procFunction() throws IOException {
        int line = lex.getLine();
        FunctionRhs frhs;
        CommandsBlock cb;
        Rhs rhs = null;
        matchToken(TokenType.FUNCTION);
        matchToken(TokenType.OPEN_CUR);
        cb = procCode();
        if(current.type == TokenType.RETURN) {
            matchToken(TokenType.RETURN);
            rhs = procRhs();
            matchToken(TokenType.DOT_COMMA);
        }
        StandardFunction func;
        if(rhs == null) {
            func = new StandardFunction(cb);
        }
        else {
            func = new StandardFunction(cb, rhs);
        }
        matchToken(TokenType.CLOSE_CUR);
        FunctionValue fv = new FunctionValue(func);
        frhs = new FunctionRhs(fv, line);
        return frhs;
    }
    
    //<expr> ::= <term> { ('+' | '-') <term> }
    private Expr procExpr() throws IOException {
        Expr e = procTerm();
        int line;
        while(current.type == TokenType.ADD || current.type == TokenType.SUB) {
            line = lex.getLine();
            if(current.type == TokenType.ADD) {
                matchToken(TokenType.ADD);
                Expr rightExpr = procTerm();
                e = new CompositeExpr(e, CompOp.Add, rightExpr, line);
            }
            else {
                matchToken(TokenType.SUB);
                Expr rightExpr = procTerm();
                e = new CompositeExpr(e, CompOp.Sub, rightExpr, line);
            }
        }
        return e;
    }
    
    //<term> ::= <factor> { ('*' | '/' | '%') <factor> }
    private Expr procTerm() throws IOException {
        Expr e = procFactor();
        int line;
        while(current.type == TokenType.MULT ||
              current.type == TokenType.DIV ||
              current.type == TokenType.MOD) {
            line = lex.getLine();
            if(current.type == TokenType.MULT) {
                matchToken(TokenType.MULT);
                Expr rightExpr = procFactor();
                e = new CompositeExpr(e, CompOp.Mult, rightExpr, line);
            }
            else if(current.type == TokenType.DIV) {
                matchToken(TokenType.DIV);
                Expr rightExpr = procFactor();
                e = new CompositeExpr(e, CompOp.Div, rightExpr, line);
            }
            else {
                matchToken(TokenType.MOD);
                Expr rightExpr = procFactor();
                e = new CompositeExpr(e, CompOp.Mod, rightExpr, line);
            }
            
        }
        return e;
    }
    
    //<factor> ::= <number> | <string> | <access> [ <call> ] | '(' <expr> ')'
    private Expr procFactor() throws IOException {
        Expr e = null;
        if(current.type == TokenType.NUMBER) {
            e = procNumber();
        }
        else if(current.type == TokenType.STRING) {
            e = procString();
        }
        else if(current.type == TokenType.OPEN_PAR) {
            matchToken(TokenType.OPEN_PAR);
            e = procExpr();
            matchToken(TokenType.CLOSE_PAR);
        }
        else if(current.type == TokenType.SYSTEM || current.type == TokenType.SELF ||
                current.type == TokenType.ARGS || current.type == TokenType.NAME) {
            int line = lex.getLine();
            AccessPath path = procAccess();
            if(current.type == TokenType.OPEN_PAR) {
                e = procCall(path);
            }
            else {
                e = new AccessExpr(path, line);
            }
        }
        return e;
    }
    
    //<var> ::= system | self | args | <name> 	
    private String procVar() throws IOException {
        String var = null;
        if(current.type == TokenType.SYSTEM) {
            var = current.token;
            matchToken(TokenType.SYSTEM);
        }
        else if(current.type == TokenType.SELF) {
            var = current.token;
            matchToken(TokenType.SELF);
        }
        else if(current.type == TokenType.ARGS) {
            var = current.token;
            matchToken(TokenType.ARGS);
        }
        else if(current.type == TokenType.NAME) {
            var = procName();
        }
        return var;
    }
    
    private ConstExpr procNumber() throws IOException {
        int line = lex.getLine();
        String tmp = current.token;
        matchToken(TokenType.NUMBER);
        int n = Integer.parseInt(tmp);
        IntegerValue iv = new IntegerValue(n);
        ConstExpr ce = new ConstExpr(iv, line);
        return ce;
    }
    
    private ConstExpr procString() throws IOException {
        int line = lex.getLine();
        String str = current.token;
        matchToken(TokenType.STRING);
        StringValue sv = new StringValue(str);
        ConstExpr ce = new ConstExpr(sv, line);
        return ce;
    }
    
    private String procName() throws IOException {
        String name = current.token;
        matchToken(TokenType.NAME);
        return name;
    }
}
