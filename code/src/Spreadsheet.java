import Content.Content;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Spreadsheet {
    private Map<String, Cell> cells;
    private DependencyManager dependencyManager;


    public void setCell(String coord, Content content) {}
    public void calculateAll() {}
    public void loadFromFile(String path) {}

    public Spreadsheet() {
        this.cells = new HashMap<>();  // <-- REQUIRED!
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
