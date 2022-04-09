package ru.levolex.smartpattern.expression.functions;

import ru.levolex.smartpattern.expression.SmartExpression;
import ru.levolex.smartpattern.interfaces.FunctionHandler;

import java.util.Arrays;
import java.util.Date;

public class TodayFunction implements FunctionHandler {

    public static final String TODAY = "today";
    public static final String NOW = "now";

    @Override
    public boolean match(SmartExpression expr) {
        return Arrays.asList(TODAY, NOW).contains(expr.getExpression());
    }

    @Override
    public boolean calculateFunction(SmartExpression expr) {
        expr.setValue(new Date());
        expr.setItemType(SmartExpression.ItemType.Date);
        return true;
    }
}
