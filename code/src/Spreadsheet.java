import java.util.HashMap;
import java.util.Map;

public class Spreadsheet {
    private Map<String, Cell> cells;
    private DependencyManager dependencyManager;

    public void calculateAll() {}

    public Spreadsheet() {
        this.cells = new HashMap<>();
    }
    public Cell getCell(String coord) {
        return cells.computeIfAbsent(coord, Cell::new);
    }

    public void setCellContent(String coord, String content) {
        getCell(coord).setContent(content);
    }

    public String getCellContent(String coord) {
        return getCell(coord).getContent();
    }

    public Map<String, Cell> getAllCells() {
        return cells;
    }


}
