package ru.levolex.smartpattern.expression.operations;

import ru.levolex.smartpattern.expression.SmartExpression;
import ru.levolex.smartpattern.interfaces.OperationHandler;

import java.util.Arrays;

public class StringOperation implements OperationHandler {
    @Override
    public boolean match(SmartExpression item1, SmartExpression item2) {
        return Arrays.asList(PLUS, MINUS).contains(item1.getOperation())
                && Arrays.asList(item1.getItemType(), item2.getItemType()).contains(SmartExpression.ItemType.String);
    }

    @Override
    public boolean performOperation(SmartExpression item1, SmartExpression item2) {
        switch (item1.getOperation()) {
            case PLUS:
                item2.setValue(item1.getValue().toString() + item2.getValue().toString());
                break;
            case MINUS:
                break;
            default:
                return false;
        }
        item2.setItemType(SmartExpression.ItemType.String);
        return true;
    }
}
