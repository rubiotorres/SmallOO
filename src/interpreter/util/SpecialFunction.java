package interpreter.util;

import interpreter.value.FunctionValue;
import interpreter.value.InstanceValue;
import interpreter.value.IntegerValue;
import interpreter.value.StringValue;
import interpreter.value.Value;

import java.util.Scanner;

import java.util.Random;

public class SpecialFunction extends Function {

    private FunctionType type;
    private Scanner in;

    public SpecialFunction(FunctionType type) {
        this.type = type;
        this.in = new Scanner(System.in);
    }

    @Override
    public Value<?> call(Instance self, Arguments args) {
        Value<?> v;

        switch (type) {
            case Print:
                v = this.print(args);
                break;
            case Println:
                v = this.println(args);
                break;
            case Read:
                v = this.read(args);
                break;
            case Random:
                v = this.random(args);
                break;
            case Get:
                v = this.get(self, args);
                break;
            case Set:
                v = this.set(self, args);
                break;
            case Abort:
                v = this.abort(args);
                break;
            case Type:
                v = this.type(args);
                break;
            case Length:
                v = this.length(args);
                break;
            case Substring:
                v = this.subString(args);
                break;
            case Clone:
                v = this.clone(args);
                break;
            default:
                throw new RuntimeException("Funçao " + type + " nao implementada");
        }

        return v;
    }

    private Value<?> print(Arguments args) {
        if (args.contains("arg1")) {
            Value<?> v = args.getValue("arg1");
            if (v instanceof IntegerValue) {
                IntegerValue iv = (IntegerValue) v;
                System.out.print(v.value());
            } else if (v instanceof StringValue) {
                StringValue sv = (StringValue) v;
                System.out.print(sv.value());
            } else {
                throw new RuntimeException("FIXME: Implement me!");
            }
        }

        return IntegerValue.Zero;
    }

    private Value<?> println(Arguments args) {
        Value<?> v = print(args);
        System.out.println();
        return v;
    }

    private Value<?> read(Arguments args) {
        // Print the argument.
        this.print(args);

        String str = in.nextLine();
        try {
            int n = Integer.parseInt(str);
            IntegerValue iv = new IntegerValue(n);
            return iv;
        } catch (Exception e) {
            StringValue sv = new StringValue(str);
            return sv;
        }
    }

    private Value<?> random(Arguments args) {
        if (!args.contains("arg1") || !args.contains("arg2")) {
            throw new operacaoException();
        }
        Value<?> argumentOne = args.getValue("arg1");
        Value<?> argumentTwo = args.getValue("arg2");

        if (!(argumentOne instanceof IntegerValue)
                || !(argumentTwo instanceof IntegerValue)) {
            throw new operacaoException();
        }

        int argumentOneInt = ((IntegerValue) argumentOne).value();
        int argumentTwoInt = ((IntegerValue) argumentTwo).value();

        if (argumentTwoInt <= argumentOneInt) {
            throw new operacaoException();
        }
        
        Random rand = new Random();
        int randomNumber = argumentOneInt 
                + rand.nextInt(argumentTwoInt - argumentOneInt + 1);
        
        return new IntegerValue(randomNumber);
    }

    private Value<?> get(Instance self, Arguments args) {
        if (!args.contains("arg1") || !args.contains("arg2")) {
            throw new operacaoException();
        }
        
        Value<?> objInstance = args.getValue("arg1");
        Value<?> attrString = args.getValue("arg2");
        
        if (!(objInstance instanceof InstanceValue)) {
            throw new operacaoException();
        }
        
        if (!(attrString instanceof StringValue)) {
            throw new operacaoException();
        }
        
        String attr = (String) attrString.value();

        Instance pureInstance = ((InstanceValue) objInstance).value();
        Value<?> attrValue = pureInstance.getValue(attr);

        return attrValue;
    }

    private Value<?> set(Instance self, Arguments args) {
        if (!args.contains("arg1")) {
            System.out.println("Caso 1");
            throw new operacaoException();
        }
        
        Value<?> objInstance = args.getValue("arg1");
        Value<?> attrString = args.getValue("arg2");
        Value<?> value = args.getValue("arg3");
        
        if (!(objInstance instanceof InstanceValue)) {
            System.out.println("Caso 2");
            throw new operacaoException();
        }
        
        if (!(attrString instanceof StringValue)) {
            System.out.println("Caso 3");
            throw new operacaoException();
        }
        
        String attr = (String) attrString.value();

        Instance pureInstance = ((InstanceValue) objInstance).value();
        pureInstance.setValue(attr, value);
        
        return new IntegerValue(0);
        
    }

    private Value<?> abort(Arguments args) {
        Value<?> argumentOne;
        
        if (args.contains("arg1")) {
            argumentOne = args.getValue("arg1");
            
            // Argumento nao e uma string
            if (!(argumentOne instanceof StringValue)) {
                throw new operacaoException();
            }
            
            this.println(args);   
        }
        
        System.exit(0);
        
        return new IntegerValue(0);
    }

    private Value<?> type(Arguments args) {
        if (!(args.contains("arg1"))) {
            throw new operacaoException();
        }
        
        Value<?> argumentOne = args.getValue("arg1");
        StringValue ret = null;
        
        if (argumentOne instanceof IntegerValue) {
            ret = new StringValue("integer");
        } else if (argumentOne instanceof StringValue) {
            ret = new StringValue("string");
        } else if (argumentOne instanceof FunctionValue) {
            ret = new StringValue("function");
        } else if (argumentOne instanceof InstanceValue) {
            ret = new StringValue("instance");
        }
        
        return ret;
    }

    private Value<?> length(Arguments args) {
        Value<?> argumentOne = null;
        
        if (!(args.contains("arg1"))) {
            throw new operacaoException();
        }
        
        argumentOne = args.getValue("arg1");
        
        if (!(argumentOne instanceof StringValue)) {
            throw new operacaoException();
        } 
        
        String value = (String) argumentOne.value();
        int len = value.length();
        IntegerValue strLen = new IntegerValue(len);
        
        return strLen;
    }

    private Value<?> subString(Arguments args) {
        if (!(args.contains("arg1")) 
                || !(args.contains("arg2")) 
                || !(args.contains("arg3"))) {
            throw new operacaoException();
        }
        
        Value<?> argumentOne = args.getValue("arg1");
        Value<?> argumentTwo = args.getValue("arg2");
        Value<?> argumentThree = args.getValue("arg3");
        
        if (!(argumentOne instanceof StringValue) 
                || !(argumentTwo instanceof IntegerValue)
                || !(argumentThree instanceof IntegerValue)) {
            throw new operacaoException();
        }
        
        String value = (String) argumentOne.value();
        int indexI = ((IntegerValue) argumentTwo).value();
        int indexF = ((IntegerValue) argumentThree).value();
        
        
        String strRet = value.substring(indexI, indexF);
        StringValue subStr = new StringValue(strRet);
        
        return subStr;
    }

    private Value<?> clone(Arguments args) {
        if (!(args.contains("arg1"))) {
            throw new operacaoException();
        }
        
        Value<?> argumentOne = args.getValue("arg1");
        
        if (!(argumentOne instanceof InstanceValue)) {
            throw new operacaoException();
        }
        
        Value<?> clonedValue = new InstanceValue(((InstanceValue) argumentOne).value().dup());
        
        return clonedValue;
    }

}