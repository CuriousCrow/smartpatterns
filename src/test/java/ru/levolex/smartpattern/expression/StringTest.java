package ru.levolex.smartpattern.expression;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class StringTest {

    @Test
    public void stringConcatenationTest() {
        SmartExpression expression = new SmartExpression();
        expression.addExpression("param", "'Private' + Jet");
        Assertions.assertEquals(expression.calcValue("param"), "PrivateJet");
    }
}
