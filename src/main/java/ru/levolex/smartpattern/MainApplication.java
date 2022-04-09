package ru.levolex.smartpattern;

import ru.levolex.smartpattern.expression.SmartExpression;

public class MainApplication {

    public static void main(String[] args) {

        SmartExpression expr = new SmartExpression();
//        expr.addExpression("param1", "today()");
        expr.addExpression("param2", "randomInt(50;100)");

        for(int i=0; i<1; i++) {
            System.out.println(expr.calcValue("param2"));
        }
    }
}
