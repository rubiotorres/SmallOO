package interpreter.value;

import interpreter.util.Arguments;
import interpreter.util.Instance;

public class InstanceValue extends Value<Instance> {

    private Instance value;

    public InstanceValue(Instance value) {
        this.value = value;
    }

    public InstanceValue(Arguments args) {
        throw new UnsupportedOperationException("InstanceValue(args) not supported"); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Instance value() {
        return value;
    }

}