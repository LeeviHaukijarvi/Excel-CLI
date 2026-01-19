public class BinaryOperation implements Expression {
    private Expression left;
    private Expression right;
    private char operator;

    public BinaryOperation(Expression left, char operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public double calculate() {
        double l = left.calculate();
        double r = right.calculate();

        switch (operator) {
            case '+':
                return l + r;
            case '-':
                return l - r;
            case '*':
                return l * r;
            case '/':
                if (r == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return l / r;
            default:
                throw new IllegalStateException("Unknown operator: " + operator);
        }
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }
}
