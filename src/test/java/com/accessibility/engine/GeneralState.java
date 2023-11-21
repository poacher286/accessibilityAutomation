package com.accessibility.engine;

import java.util.function.Function;

public class GeneralState<T, R> implements ConditionState<T, R>{
    private final Function<T, R> fun;

    public GeneralState(Function<T, R> fun) {
        this.fun = fun;
    }

    @Override
    public Function<T, R> condition() {
        return this.fun;
    }

    @Override
    public int timeout() {
        return ONE_MIN;
    }
}
