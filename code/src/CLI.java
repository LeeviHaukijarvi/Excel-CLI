import java.util.Map;
import java.util.Scanner;

public class CLI {

    private Spreadsheet sheet = new Spreadsheet();
    private Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n=== Excel-CLI ===");
            System.out.println("1. Set cell");
            System.out.println("2. View cell");
            System.out.println("3. View spreadsheet grid");
            System.out.println("4. Save spreadsheet");
            System.out.println("5. Load spreadsheet");
            System.out.println("6. Create new spreadsheet");
            System.out.println("7. Exit");

            System.out.print("Choose: ");
            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1 -> setCell();
                case 2 -> viewCell();
                case 3 -> viewGrid();
                case 4 -> save();
                case 5 -> load();
                case 6 -> createNew();
                case 7 -> { return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void setCell() {
        System.out.print("Cell (e.g. A1): ");
        String coord = sc.nextLine();
        System.out.print("Content (text, number, or formula starting with =): ");
        String content = sc.nextLine();

        try {
            sheet.setCellContent(coord.toUpperCase(), content);
            System.out.println("Cell " + coord + " set successfully");
            System.out.println("Evaluated value: " + sheet.getCellContent(coord.toUpperCase()));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void viewCell() {
        System.out.print("Cell (e.g. A1): ");
        String coord = sc.nextLine();
        String coordUpper = coord.toUpperCase();

        Cell cell = sheet.getCell(coordUpper);
        System.out.println("\n--- Cell " + coordUpper + " ---");
        System.out.println("Raw content: " + cell.getRawContent());
        System.out.println("Evaluated value: " + cell.getDisplayValue());
    }

    private void viewGrid() {
        Map<String, Cell> allCells = sheet.getAllCells();

        if (allCells.isEmpty()) {
            System.out.println("Spreadsheet is empty");
            return;
        }

        // Find bounds
        int maxRow = 0;
        int maxCol = 0;

        for (String coord : allCells.keySet()) {
            int col = FileSystem.columnNumber(coord);
            int row = FileSystem.rowNumber(coord);
            if (row > maxRow) maxRow = row;
            if (col > maxCol) maxCol = col;
        }

        // Build grid display
        System.out.println("\n=== Spreadsheet Grid ===");

        // Print column headers
        System.out.print("     ");
        for (int col = 1; col <= maxCol; col++) {
            System.out.printf("%-15s ", FileSystem.coordinate(col, 1).replaceAll("\\d", ""));
        }
        System.out.println();
        System.out.print("     ");
        for (int col = 1; col <= maxCol; col++) {
            System.out.print("--------------- ");
        }
        System.out.println();

        // Print rows
        for (int row = 1; row <= maxRow; row++) {
            System.out.printf("%-4d|", row);

            for (int col = 1; col <= maxCol; col++) {
                String coord = FileSystem.coordinate(col, row);
                Cell cell = allCells.get(coord);
                String value = cell != null ? cell.getDisplayValue() : "";

                // Truncate if too long
                if (value.length() > 14) {
                    value = value.substring(0, 11) + "...";
                }

                System.out.printf("%-15s ", value);
            }
            System.out.println();
        }
    }

    private void createNew() {
        System.out.print("Are you sure? This will clear all data (y/n): ");
        String confirm = sc.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            sheet.reset();
            System.out.println("New spreadsheet created");
        }
    }

    private void save() {
        try {
            System.out.print("File path: ");
            String path = sc.nextLine();
            FileSystem.save(sheet, path);
            System.out.println("Saved!");
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    private void load() {
        try {
            System.out.print("File path: ");
            String path = sc.nextLine();
            sheet = FileSystem.load(path);
            System.out.println("Loaded successfully!");
            System.out.println("Use option 3 to view the spreadsheet");
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
