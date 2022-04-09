package ru.levolex.smartpattern.expression.operations;

import ru.levolex.smartpattern.expression.SmartExpression;
import ru.levolex.smartpattern.interfaces.OperationHandler;

import java.util.Arrays;

public class IntegerOperation implements OperationHandler {
    @Override
    public boolean match(SmartExpression item1, SmartExpression item2) {
        return Arrays.asList(PLUS, MINUS, MULIPLY, DIVIDE).contains(item1.getOperation())
                && (item1.getItemType() == SmartExpression.ItemType.Integer)
                && (item2.getItemType() == SmartExpression.ItemType.Integer);
    }

    @Override
    public boolean performOperation(SmartExpression item1, SmartExpression item2) {
        switch (item1.getOperation()) {
            case PLUS:
                item2.setValue((Integer)item1.getValue() + (Integer)item2.getValue());
                break;
            case MINUS:
                item2.setValue((Integer)item1.getValue() - (Integer)item2.getValue());
                break;
            case MULIPLY:
                item2.setValue((Integer)item1.getValue() * (Integer)item2.getValue());
                break;
            case DIVIDE:
                item2.setValue((Integer)item1.getValue() / (Integer)item2.getValue());
                break;
            default:
                throw new IllegalStateException(String.format("Unknown operation: %s", item1.getOperation()));
        }
        return true;
    }
}
