public class Literal implements Expression {
    private double value;

    public Literal(double value) {
        this.value = value;
    }

    @Override
    public double calculate() {
        return value;
    }
}
