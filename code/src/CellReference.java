public class CellReference implements Expression {
    private String coordinate;
    private Spreadsheet spreadsheet;

    public CellReference(String coordinate, Spreadsheet spreadsheet) {
        this.coordinate = coordinate.toUpperCase();
        this.spreadsheet = spreadsheet;
    }

    @Override
    public double calculate() {
        Cell cell = spreadsheet.getCell(coordinate);
        Content content = cell.getContent();

        // Empty cell treated as 0
        if (content == null) {
            return 0;
        }

        Object value = content.getValue();

        // Handle numeric values
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        // Handle string values
        if (value instanceof String) {
            String str = (String) value;
            if (str.isEmpty()) {
                return 0;
            }
            // Try to parse as number
            try {
                return Double.parseDouble(str);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cell " + coordinate + " contains non-numeric value: " + str);
            }
        }

        throw new RuntimeException("Cannot convert cell " + coordinate + " to number");
    }

    public String getCoordinate() {
        return coordinate;
    }
}
