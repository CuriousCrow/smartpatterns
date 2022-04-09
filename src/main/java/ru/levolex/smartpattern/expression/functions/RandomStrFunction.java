package ru.levolex.smartpattern.expression.functions;

import ru.levolex.smartpattern.expression.SmartExpression;
import ru.levolex.smartpattern.interfaces.FunctionHandler;

import java.util.Random;

public class RandomStrFunction implements FunctionHandler {
    @Override
    public boolean match(SmartExpression expr) {
        return false;
    }

    @Override
    public boolean calculateFunction(SmartExpression expr) {
        Random random = new Random();
        expr.setValue(expr.getChildren().get(random.nextInt(expr.getChildren().size())).getValue());
        expr.setItemType(SmartExpression.ItemType.String);
        return true;
    }
}
