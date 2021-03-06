package interpreter.value;

import interpreter.util.Function;

public class FunctionValue extends Value<Function> {

    public Function value;

    public FunctionValue(Function value) {
        this.value = value;
    }

    @Override
    public Function value() {
        return value;
    }
}
