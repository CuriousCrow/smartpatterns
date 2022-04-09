package ru.levolex.smartpattern.interfaces;

import ru.levolex.smartpattern.expression.SmartExpression;

public interface FunctionHandler {

    boolean match(SmartExpression expr);

    boolean calculateFunction(SmartExpression expr);
}
