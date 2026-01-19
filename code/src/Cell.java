public class Cell {
    private String coordinate;
    private Content content;

    public Cell(String coordinate) {
        this.coordinate = coordinate;
        this.content = null;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * Get evaluated value for display
     */
    public String getDisplayValue() {
        if (content == null) {
            return "";
        }
        Object value = content.getValue();
        return value == null ? "" : value.toString();
    }

    /**
     * Get raw content for file saving
     */
    public String getRawContent() {
        if (content == null) {
            return "";
        }
        return content.getRawContent();
    }
}
