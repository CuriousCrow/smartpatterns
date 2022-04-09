package ru.levolex.smartpattern.expression.functions;

import ru.levolex.smartpattern.expression.SmartExpression;
import ru.levolex.smartpattern.interfaces.FunctionHandler;

import java.util.Arrays;

public class SmallFunctions implements FunctionHandler {

    public static final String PI_FUNCTION = "pi";
    public static final String SPACE_FUNCTION = "sp";

    @Override
    public boolean match(SmartExpression expr) {
        return Arrays.asList(PI_FUNCTION, SPACE_FUNCTION).contains(expr.getExpression());
    }

    @Override
    public boolean calculateFunction(SmartExpression expr) {
        switch (expr.getExpression()) {
            case PI_FUNCTION:
                expr.setValue(3.14);
                expr.setItemType(SmartExpression.ItemType.Float);
                break;
            case SPACE_FUNCTION:
                expr.setValue(" ");
                expr.setItemType(SmartExpression.ItemType.String);
                break;
            default:
                throw new IllegalStateException("Unknown function name");
        }
        return true;
    }
}
