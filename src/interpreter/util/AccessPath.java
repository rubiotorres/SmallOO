package interpreter.util;

import java.util.List;
import java.util.ArrayList;

import interpreter.util.InterpreterError;

import interpreter.value.Value;
import interpreter.value.IntegerValue;
import interpreter.value.InstanceValue;

public class AccessPath {

    private int line;
    private List<String> names;

    public AccessPath(String name, int line) {
        this.line = line;

        names = new ArrayList<String>();
        names.add(name);
    }

    public void addName(String name) {
        names.add(name);
    }

    public List<String> getNames() {
        return new ArrayList<String>(names);
    }

    public Value<?> getValue(Instance self, Arguments args) {
        Memory ref;

        Value<?> ret;
        if (this.isSingleName()) {
            if (this.isSelf())
                return new InstanceValue(self);
            else if (this.isArgs())
                return new InstanceValue(args);
            
            ref = Global.getGlobalTable();
        } else {
            ref = this.getReference(self, args);
        }

        String name = this.getLastName();
        if (ref.contains(name))
            ret = ref.getValue(name);
        else {
            ret = IntegerValue.Zero;
            ref.setValue(name, ret);
        }

        return ret;
    }

    public void setValue(Instance self, Arguments args, Value<?> value) {
        Memory ref;
        if (this.isSingleName()) {
            if (this.isSelf() || this.isArgs())
                InterpreterError.abort(line);

            ref = Global.getGlobalTable();
        } else {
            ref = this.getReference(self, args);
        }

        String name = this.getLastName();
        ref.setValue(name, value);
    }

    public Memory getReference(Instance self, Arguments args) {
        Memory ref = null;
        if (!this.isSingleName()) {
            int i;
            String name;

            name = names.get(0);
            if (name.equals("self")) {
                if (self == null)
                    InterpreterError.abort(line);

                ref = self;
                i = 1;
            } else if (name.equals("args")) {
                if (self == null)
                    InterpreterError.abort(line);

                ref = args;
                i = 1;
            } else {
                ref = Global.getGlobalTable();
                i = 0;
            }

            for (; i < names.size() - 1; i++) {
                name = names.get(i);

                Memory newRef;
                if (ref.contains(name) && ref.getValue(name) instanceof InstanceValue) {
                    InstanceValue iv = (InstanceValue) ref.getValue(name);
                    newRef = iv.value();
                } else {
                    // if there are more names, than it must be an instance (object) reference.
                    newRef = new Instance();
                    ref.setValue(name, new InstanceValue((Instance) newRef));
                }

                ref = newRef;
            }
        }

        return ref;
    }

    public String getLastName() {
        return names.get(names.size() - 1);
    }

    public boolean isSingleName() {
        return names.size() == 1;
    }

    public boolean isSelf() {
        return this.isSingleName() && this.names.get(0).equals("self");
    }

    public boolean isArgs() {
        return this.isSingleName() && this.names.get(0).equals("args");
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(names.get(0));
        for (int i = 1; i < names.size(); i++)
            sb.append(".").append(names.get(i));

        return sb.toString();
    }

}