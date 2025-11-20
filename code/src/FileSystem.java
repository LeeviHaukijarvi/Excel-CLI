import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class FileSystem {
    public static void save(Spreadsheet sheet, String path) throws IOException {
        Map<Integer, Map<Integer, String>> rows = new TreeMap<>();

        for (Cell c : sheet.getAllCells().values()) {
            String coord = c.getCoordinate();
            int col = columnNumber(coord);
            int row = rowNumber(coord);

            rows.putIfAbsent(row, new TreeMap<>());
            rows.get(row).put(col, c.getContent());
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        for (int row : rows.keySet()) {
            Map<Integer, String> cols = rows.get(row);

            int maxCol = cols.keySet().stream().mapToInt(i -> i).max().orElse(0);

            StringBuilder line = new StringBuilder();
            for (int col = 1; col <= maxCol; col++) {
                String content = cols.getOrDefault(col, "");
                line.append(content);

                if (col < maxCol) line.append(";");
            }

            writer.write(line.toString());
            writer.newLine();
        }

        writer.close();
    }

    public static Spreadsheet load(String path) throws IOException {
        Spreadsheet sheet = new Spreadsheet();

        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line;
        int row = 1;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";", -1); // keep empty cells

            for (int col = 1; col <= parts.length; col++) {
                String coord = coordinate(col, row);
                sheet.setCellContent(coord, parts[col - 1]);
            }

            row++;
        }

        reader.close();
        return sheet;
    }
    // Utility: Convert "A" → 1, "B" → 2, ... "AA" → 27
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

    // Utility: returns row number from coordinate "A12"
    private static int rowNumber(String coord) {
        int i = 0;
        while (i < coord.length() && Character.isLetter(coord.charAt(i))) i++;
        return Integer.parseInt(coord.substring(i));
    }

    private static String coordinate(int col, int row) {
        StringBuilder sb = new StringBuilder();

        while (col > 0) {
            col--;  // 1 → A, 26 → Z
            sb.insert(0, (char) ('A' + (col % 26)));
            col /= 26;
        }

        return sb.toString() + row;
    }
}