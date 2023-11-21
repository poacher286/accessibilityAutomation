package com.accessibility.engine;

import java.util.function.Function;

public interface ConditionState<T, R> {
    int ONE_SEC = 1000;//in millis
    int ONE_MIN = 60 * ONE_SEC;

    Function<T, R> condition();

    int timeout();
}
