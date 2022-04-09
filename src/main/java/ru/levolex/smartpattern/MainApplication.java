package ru.levolex.smartpattern;

import ru.levolex.smartpattern.expression.SmartExpression;

public class MainApplication {

    public static void main(String[] args) {

        SmartExpression expr = new SmartExpression();
        expr.addExpression("param1", "50+(20*2)");
        expr.addExpression("param2", "Hello+sp()+World");

        for(int i=0; i<1; i++) {
            System.out.println(expr.calcValue("param1"));
        }
    }
}
