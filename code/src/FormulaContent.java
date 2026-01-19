public class FormulaContent extends Content {
    private String rawFormula;
    private Expression expression;

    public FormulaContent(String rawFormula, Expression expression) {
        this.rawFormula = rawFormula;
        this.expression = expression;
    }

    @Override
    public Object getValue() {
        try {
            return expression.calculate();
        } catch (Exception e) {
            return "#ERROR: " + e.getMessage();
        }
    }

    @Override
    public String getRawContent() {
        return rawFormula;
    }

    public Expression getExpression() {
        return expression;
    }
}
