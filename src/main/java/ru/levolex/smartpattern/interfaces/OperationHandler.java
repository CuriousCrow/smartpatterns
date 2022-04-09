package ru.levolex.smartpattern.interfaces;

import ru.levolex.smartpattern.expression.SmartExpression;

public interface OperationHandler {

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MULIPLY = "*";
    public static final String DIVIDE = "/";

    boolean match(SmartExpression item1, SmartExpression item2);

    boolean performOperation(SmartExpression item1, SmartExpression item2);
}
