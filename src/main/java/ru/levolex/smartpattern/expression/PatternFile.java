package ru.levolex.smartpattern.expression;

import ru.levolex.smartpattern.interfaces.Expression;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class PatternFile {

    private String patternFilename;

    private String preparedPattern;

    public PatternFile(String filePath, String patternString) {
        Path path = Paths.get(filePath);
        this.patternFilename = String.valueOf(path.getFileName());
        this.preparedPattern = patternString;
    }

    public String fillPatternWithValues(Expression expression) {
        return fillPatternWithValues(expression, this.preparedPattern);
    }

    public String fillPatternWithValues(Expression expression, String pattern) {
        StringBuffer sb = new StringBuffer(pattern);


        return sb.toString();
    }

    public String fillPatternWithExpressions(Collection<Expression> expressions) {
        //TODO: Add
        return "";
    }
}
