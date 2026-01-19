public class NumericContent extends Content {
    private double number;

    public NumericContent(double number) {
        this.number = number;
    }

    @Override
    public Object getValue() {
        return number;
    }

    @Override
    public String getRawContent() {
        return String.valueOf(number);
    }
}
