import java.util.ArrayList;
import java.util.List;

public class Range implements Expression {
    private String startCoord;
    private String endCoord;
    private Spreadsheet spreadsheet;

    public Range(String startCoord, String endCoord, Spreadsheet spreadsheet) {
        this.startCoord = startCoord.toUpperCase();
        this.endCoord = endCoord.toUpperCase();
        this.spreadsheet = spreadsheet;
    }

    @Override
    public double calculate() {
        // Default calculate() returns sum for backward compatibility
        return getValues().stream().mapToDouble(d -> d).sum();
    }

    /**
     * Get all numeric values in the range
     */
    public List<Double> getValues() {
        List<Double> values = new ArrayList<>();

        int startCol = columnNumber(startCoord);
        int startRow = rowNumber(startCoord);
        int endCol = columnNumber(endCoord);
        int endRow = rowNumber(endCoord);

        // Ensure start <= end
        if (startCol > endCol) {
            int temp = startCol;
            startCol = endCol;
            endCol = temp;
        }
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }

        // Iterate over rectangular range
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String coord = coordinate(col, row);
                Cell cell = spreadsheet.getCell(coord);
                Content content = cell.getContent();

                if (content != null) {
                    Object value = content.getValue();
                    if (value instanceof Number) {
                        values.add(((Number) value).doubleValue());
                    } else if (value instanceof String) {
                        String str = (String) value;
                        if (!str.isEmpty()) {
                            try {
                                values.add(Double.parseDouble(str));
                            } catch (NumberFormatException e) {
                                // Skip non-numeric values
                            }
                        }
                    }
                }
            }
        }

        return values;
    }

    /**
     * Get all cell coordinates in the range (for dependency tracking)
     */
    public List<String> getAllCoordinates() {
        List<String> coordinates = new ArrayList<>();

        int startCol = columnNumber(startCoord);
        int startRow = rowNumber(startCoord);
        int endCol = columnNumber(endCoord);
        int endRow = rowNumber(endCoord);

        // Ensure start <= end
        if (startCol > endCol) {
            int temp = startCol;
            startCol = endCol;
            endCol = temp;
        }
        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }

        // Iterate over rectangular range
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                coordinates.add(coordinate(col, row));
            }
        }

        return coordinates;
    }

    // Utility: Convert "A1" → column number
    private static int columnNumber(String coord) {
        int i = 0;
        while (i < coord.length() && Character.isLetter(coord.charAt(i))) i++;

        String colLetters = coord.substring(0, i).toUpperCase();
        int col = 0;
        for (char ch : colLetters.toCharArray()) {
            col = col * 26 + (ch - 'A' + 1);
        }
        return col;
    }

    // Utility: Extract row number from "A1"
    private static int rowNumber(String coord) {
        int i = 0;
        while (i < coord.length() && Character.isLetter(coord.charAt(i))) i++;
        return Integer.parseInt(coord.substring(i));
    }

    // Utility: Convert column and row numbers back to coordinate
    private static String coordinate(int col, int row) {
        StringBuilder sb = new StringBuilder();

        while (col > 0) {
            col--;
            sb.insert(0, (char) ('A' + (col % 26)));
            col /= 26;
        }

        return sb.toString() + row;
    }
}
