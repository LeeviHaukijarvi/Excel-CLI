import java.util.ArrayList;
import java.util.List;

public class Function implements Expression {
    private String functionName;
    private Expression argument;

    public Function(String functionName, Expression argument) {
        this.functionName = functionName.toUpperCase();
        this.argument = argument;
    }

    @Override
    public double calculate() {
        List<Double> values = extractValues(argument);

        if (values.isEmpty()) {
            return 0;
        }

        switch (functionName) {
            case "SUMA":
                return values.stream().mapToDouble(d -> d).sum();
            case "MIN":
                return values.stream().mapToDouble(d -> d).min().orElse(0);
            case "MAX":
                return values.stream().mapToDouble(d -> d).max().orElse(0);
            case "PROMEDIO":
                return values.stream().mapToDouble(d -> d).average().orElse(0);
            default:
                throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }

    /**
     * Extract numeric values from an expression
     * If it's a Range, get all values; otherwise evaluate single expression
     */
    private List<Double> extractValues(Expression expr) {
        List<Double> values = new ArrayList<>();

        if (expr instanceof Range) {
            return ((Range) expr).getValues();
        } else {
            values.add(expr.calculate());
        }

        return values;
    }

    public Expression getArgument() {
        return argument;
    }
}
