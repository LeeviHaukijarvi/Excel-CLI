public class TextContent extends Content {
    private String text;

    public TextContent(String text) {
        this.text = text;
    }

    @Override
    public Object getValue() {
        return text;
    }

    @Override
    public String getRawContent() {
        return text;
    }
}
