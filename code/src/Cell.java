
public class Cell {
    private String coordinate;
    private String content;


    public Cell(String coordinate) {
        this.coordinate = coordinate;
        this.content = "";
    }

    public String getCoordinate() {
        return coordinate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
