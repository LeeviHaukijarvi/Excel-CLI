import java.util.Scanner;

public class CLI {

    private Spreadsheet sheet = new Spreadsheet();
    private Scanner sc = new Scanner(System.in);

    public void start() {
        while (true) {
            System.out.println("\n1. Set cell");
            System.out.println("2. View cell");
            System.out.println("3. Save spreadsheet");
            System.out.println("4. Load spreadsheet");
            System.out.println("5. Exit");

            System.out.print("Choose: ");
            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1 -> setCell();
                case 2 -> viewCell();
                case 3 -> save();
                case 4 -> load();
                case 5 -> { return; }
            }
        }
    }

    private void setCell() {
        System.out.print("Cell (e.g. A1): ");
        String coord = sc.nextLine();
        System.out.print("Content: ");
        String content = sc.nextLine();
        sheet.setCellContent(coord, content);
    }

    private void viewCell() {
        System.out.print("Cell (e.g. A1): ");
        String coord = sc.nextLine();
        System.out.println("Content = " + sheet.getCellContent(coord.toUpperCase()));
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
            System.out.println("Loaded!");
        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}
