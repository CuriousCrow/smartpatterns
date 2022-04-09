package ru.levolex.smartpattern.expression.functions;

import ru.levolex.smartpattern.interfaces.FunctionHandler;
import ru.levolex.smartpattern.expression.SmartExpression;

import java.util.Random;

public class RandomIntFunction implements FunctionHandler {

    public static final String NAME = "randomInt";

    @Override
    public boolean match(SmartExpression expr) {
        return NAME.equalsIgnoreCase(expr.getExpression());
    }

    @Override
    public boolean calculateFunction(SmartExpression expr) {
        Random random = new Random();
        if (expr.getChildren().isEmpty()) {
            //TODO: Возможно бросать исключение
            return false;
        }
        else if (expr.getChildren().size() == 1) {
            int intBound = Integer.parseInt(expr.getChildren().get(0).getValue().toString());
            expr.setValue(random.nextInt(intBound));
        } else if (expr.getChildren().size() == 2) {
            int intMin = (Integer) expr.getChildren().get(0).getValue();
            int intMax = (Integer) expr.getChildren().get(1).getValue();
            expr.setValue(random.nextInt(intMax - intMin) + intMin);
        } else {
            expr.setValue(expr.getChildren().get(random.nextInt(expr.getChildren().size())).getValue());
        }
        expr.setItemType(SmartExpression.ItemType.Integer);
        return true;
    }
}
