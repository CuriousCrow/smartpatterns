package ru.levolex.smartpattern.expression;

import ru.levolex.smartpattern.StrUtils;
import ru.levolex.smartpattern.expression.functions.RandomIntFunction;
import ru.levolex.smartpattern.expression.functions.SmallFunctions;
import ru.levolex.smartpattern.expression.functions.TodayFunction;
import ru.levolex.smartpattern.expression.operations.IntegerOperation;
import ru.levolex.smartpattern.expression.operations.StringOperation;
import ru.levolex.smartpattern.interfaces.Expression;
import ru.levolex.smartpattern.interfaces.FunctionHandler;
import ru.levolex.smartpattern.interfaces.OperationHandler;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartExpression implements Expression {

    private static int MAX_PRIORITY = 3;
    private static final String VARIABLE_LB = "{";
    private static final String VARIABLE_RB = "}";

    public enum ItemType {
        Integer,
        Float,
        Date,
        String,
        Expression,
        Variable,
        Null,
        List,
        Function
    }

    enum ExpressionType {
        Expression,
        Function
    }

    private String name;
    private String expression = "";
    private Object value;
    private ItemType itemType = ItemType.Null;
    private String operation;
    private Byte priority;
    protected static final List<FunctionHandler> functionHandlers = new ArrayList<>();
    protected static final List<OperationHandler> operationHandlers = new ArrayList<>();
    private List<SmartExpression> siblings = new ArrayList<>();
    protected List<SmartExpression> children = new ArrayList<>();

    public static void registerOperationHandler(OperationHandler operationHandler) {
        operationHandlers.add(operationHandler);
    }
    public static void registerFunctionHandler(FunctionHandler functionHandler) {
        functionHandlers.add(functionHandler);
    }

    private void parseExpression(String exprStr, ExpressionType expressionType) {
        children.clear();
        int bracketLevel = 0;
        boolean inQuotes = false;
        boolean isOper = false;
        SmartExpression newItem;
        StringBuilder curItem = new StringBuilder();

        for(Character chr: exprStr.toCharArray()) {
            if (chr == '\'') {
                inQuotes = !inQuotes;
            }

            if (chr == '(') {
                bracketLevel++;
            }
            if (chr == ')') {
                bracketLevel--;
            }

            if (expressionType == ExpressionType.Function) {
                isOper = (bracketLevel <= 0) && !inQuotes && isFunctionDelimiter(chr);
            }
            else {
                isOper  = (bracketLevel <= 0) && !inQuotes && (getOperationPriority(chr.toString()) > 0);
            }
            if (isOper) {
                newItem = getInstance(curItem.toString(), this.siblings, chr.toString(), "");
                children.add(newItem);

                curItem = new StringBuilder();
                continue;
            }
            curItem.append(chr);
        }
        newItem = getInstance(curItem.toString(), this.siblings, "", "");
        children.add(newItem);
    }

    private void parseFunction(String functionStr) {
        Pattern pattern = Pattern.compile("([a-zA-Z]+)\\((.*)\\)");
        Matcher matcher = pattern.matcher(functionStr);
        if (matcher.find()) {
            this.expression = matcher.group(1);
            parseExpression(matcher.group(2), ExpressionType.Function);
        }
    }

    private void defineType() {
        if (this.expression.isEmpty()) {
            this.itemType = ItemType.Null;
            return;
        }

        if (isFunction(this.expression)) {
            this.itemType = ItemType.Function;
            return;
        }

        boolean inQuotes = false;
        for(Character chr : this.expression.toCharArray()) {
           if (chr == '\'')
               inQuotes = !inQuotes;

           if (!inQuotes && getOperationPriority(chr.toString()) > 0) {
               this.itemType = ItemType.Expression;
               return;
           }
        }

        if ((expression.length() > 2) && expression.startsWith(VARIABLE_LB) && expression.endsWith(VARIABLE_RB)) {
            itemType = ItemType.Variable;
            expression = StrUtils.removeEndings(expression);
            return;
        }

        try {
            this.value = Integer.parseInt(expression);
            this.itemType = ItemType.Integer;
            return;
        }
        catch (Exception e) {
            //Do nothing
        }

        try {
            this.value = Float.parseFloat(expression);
            this.itemType = ItemType.Float;
            return;
        }
        catch (Exception e) {
            //Do nothing
        }

        try {
            this.value = Date.parse(expression);
            this.itemType = ItemType.Date;
            return;
        }
        catch (Exception e) {
            //Do nothing
        }

        itemType = ItemType.String;
        if ((expression.length() >= 2) && expression.startsWith("'") && expression.endsWith("'")) {
            expression = StrUtils.removeEndings(expression);
        }
        value = expression;
    }

    private boolean isFunction(String str) {
        if (!str.matches("^[a-zA-Z]+\\(.*"))
            return false;
        StringBuilder sb = new StringBuilder(str);
        byte level = 1;
        for(int i = sb.indexOf("(") + 1; i<sb.length(); i++) {
            if (sb.charAt(i) == '(')
                level++;
            if (sb.charAt(i) == ')')
                level--;

            if (level == 0) {
                return i == sb.length()-1;
            }
        }
        return false;
    }

    private void evaluate(String usedVars) {
        switch (this.itemType) {
            case Variable:
                if (!usedVars.isEmpty() && (name + ",").contains(usedVars))
                    throw new IllegalStateException(String.format("Circular reference of value: %s", name));
                if (!name.isEmpty())
                    usedVars = usedVars + name + ",";
                SmartExpression exprObj = getSiblingByName(expression);

                if (exprObj == null) {
                    throw new IllegalStateException(String.format("Unknown variable: %s", this.expression));
                }

                exprObj.evaluate(usedVars);
                value = exprObj.value;
                itemType = exprObj.itemType;
                return;
            case Function:
                if (!performFunction()) {
                    throw new IllegalStateException(String.format("Unknown function: %s", this.expression));
                }
                break;
            case Expression:
                String finalUsedVars = usedVars;
                this.children.forEach(expr -> expr.evaluate(finalUsedVars));

                int idx = 0;
                while (this.children.size() > 1) {
                    idx = getNextOperationItem();
                    processOperation(children.get(idx), children.get(idx + 1));
                    children.remove(idx);
                }
                this.itemType = this.children.get(0).itemType;
                this.value = this.children.get(0).value;

                break;
            default:
        }

        children.clear();
    }

    private SmartExpression getSiblingByName(String name) {
        if (this.siblings == null)
            return null;
        return this.siblings.stream().filter(expr -> expr.name.equals(name)).findAny().orElse(null);
    }

    private Integer getNextOperationItem() {
        for(int pr = MAX_PRIORITY; pr > 0; pr--) {
            for (int idx = 0; idx < this.children.size(); idx++) {
                if (children.get(idx).priority == pr)
                    return idx;
            }
        }

        return -1;
    }


    private void processOperation(SmartExpression item1, SmartExpression item2) {
        if (item1.itemType == ItemType.Null) {
            setNullValueByType(item1, item2.itemType);
        }

        if (item2.itemType == ItemType.Null) {
            setNullValueByType(item2, item1.itemType);
        }

        if (!performOperation(item1, item2)) {
            throw new IllegalStateException(
                    String.format("Undefined operation %s %s %s",
                            item1.expression,
                            item1.operation,
                            item2.expression));
        }
    }

    protected SmartExpression(String expression, List<SmartExpression> siblings, String operation, String name) {
        clear();

        this.name = name;
        this.setOperation(operation);
        this.siblings = siblings;

        this.expression = expression.trim();
        if (this.expression.startsWith("(") && this.expression.endsWith(")")) {
            this.expression = this.getExpression().substring(1, this.getExpression().length() - 1);
        }

        defineType();

        switch (itemType) {
            case Expression:
                parseExpression(this.expression, ExpressionType.Expression);
                break;
            case Function:
                parseFunction(this.expression);
                break;
            default:
                break;
        }

    }

    protected void setNullValueByType(SmartExpression expression, ItemType itemType) {
        expression.itemType = itemType;
        switch (itemType) {
            case Integer:
                expression.value = 0;
                break;
            case Float:
                expression.value = 0.00;
                break;
            case Date:
                expression.value = new Date();
                break;
            case String:
                expression.value = "";
                break;
            default:
                expression.value = null;
        }
    }

    protected Byte getOperationPriority(String operation) {
        if (";".equals(operation)) {
            return 3;
        }
        else if (Arrays.asList("*", "/").contains(operation)) {
            return 2;
        }
        else if (Arrays.asList("+", "-").contains(operation)) {
            return 3;
        }
        else {
            return 0;
        }
    }

    protected boolean isFunctionDelimiter(char input) {
        return  ';' == input;
    }

    protected SmartExpression getInstance(String expression, List<SmartExpression> siblings, String operation, String name) {
        return new SmartExpression(expression, siblings, operation, name);
    }

    protected boolean performOperation(SmartExpression item1, SmartExpression item2) {
        Optional<OperationHandler> operationHandler = operationHandlers.stream().filter(oh -> oh.match(item1, item2)).findFirst();
        if (operationHandler.isPresent()) {
            operationHandler.get().performOperation(item1, item2);
            return true;
        }
        else {
            System.out.println("Operation handler not found");
        }

        item1.value = "";
        item1.itemType = ItemType.Null;

        return true;
    }

    protected boolean performFunction() {
        Optional<FunctionHandler> functionHandler = functionHandlers.stream().filter(fh -> fh.match(this)).findFirst();
        if (functionHandler.isPresent()) {
            functionHandler.get().calculateFunction(this);
            return true;
        }
        else {
            System.out.printf("Cant find proper handler for function: %s%n", this.expression);
            return false;
        }
    }

    public SmartExpression() {
        registerFunctionHandler(new TodayFunction());
        registerFunctionHandler(new SmallFunctions());
        registerFunctionHandler(new RandomIntFunction());

        registerOperationHandler(new StringOperation());
        registerOperationHandler(new IntegerOperation());
    }

    public SmartExpression(String name, String expression) {
        addExpression(name, expression);
    }

    public void addExpression(String name, String expression) {
        this.itemType = ItemType.List;

        SmartExpression expr = getInstance(expression, this.siblings, "", name);
        if (this.siblings == null) {
            this.siblings = new ArrayList<>();
        }
        this.siblings.add(expr);
    }

    public void clear() {
        siblings.clear();
        children.clear();
    }

    public String calcValue(String exprName) {
        SmartExpression expr = getSiblingByName(exprName);
        if (expr == null) {
            return String.format("Expression %s not found", exprName);
        }
        else {
            //TODO: Возможно здесь должен быть конвертер
            return expr.getValue().toString();
        }
    }


    public Object getValue() {
        evaluate("");

        return value;
    }

    public void setOperation(String operation) {
        this.operation = operation;
        this.priority = getOperationPriority(operation);
        MAX_PRIORITY = Math.max(MAX_PRIORITY, this.priority);
    }

    public String getOperation() {
        return this.operation;
    }

    public String getExpression() {
        return this.expression;
    }

    public ItemType getItemType() {
        return this.itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public List<SmartExpression> getSiblings() {
        return this.siblings;
    }

    public String getName() {
        return this.name;
    }

    public List<SmartExpression> getChildren() {
        return this.children;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
