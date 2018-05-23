package interpreter.util;

import interpreter.value.FunctionValue;
import interpreter.value.InstanceValue;

public class Global extends Memory {

    private static Global global;

    private Global() {
        Instance system = new Instance();
        system.setValue("print", new FunctionValue(new SpecialFunction(FunctionType.Print)));
        system.setValue("println", new FunctionValue(new SpecialFunction(FunctionType.Println)));
        system.setValue("read", new FunctionValue(new SpecialFunction(FunctionType.Read)));
        system.setValue("random", new FunctionValue(new SpecialFunction(FunctionType.Random)));
        system.setValue("clone", new FunctionValue(new SpecialFunction(FunctionType.Clone)));

        this.setValue("system", new InstanceValue(system));
    }

    public static Global getGlobalTable() {
        if (global == null)
            global = new Global();

        return global;
    }
}