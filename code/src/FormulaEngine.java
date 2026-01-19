public class FormulaEngine {
    private String formula;
    private int position;
    private Spreadsheet spreadsheet;

    public FormulaEngine(String formula, Spreadsheet spreadsheet) {
        this.formula = formula.trim();
        this.position = 0;
        this.spreadsheet = spreadsheet;
    }

    /**
     * Parse the formula and return the Expression tree
     */
    public Expression parse() throws FormulaParseException {
        if (!formula.startsWith("=")) {
            throw new FormulaParseException("Formula must start with '='");
        }
        position = 1; // Skip '='
        Expression expr = parseExpression();

        if (position < formula.length()) {
            throw new FormulaParseException("Unexpected characters after formula at position " + position);
        }

        return expr;
    }

    /**
     * Convenience method: parse and calculate in one step
     */
    public double calculate() {
        try {
            Expression expr = parse();
            return expr.calculate();
        } catch (Exception e) {
            throw new RuntimeException("Formula error: " + e.getMessage(), e);
        }
    }

    /**
     * Parse expression: handles addition and subtraction
     * Expression -> Term (('+' | '-') Term)*
     */
    private Expression parseExpression() throws FormulaParseException {
        Expression left = parseTerm();

        while (position < formula.length()) {
            skipWhitespace();
            if (position >= formula.length()) break;

            char op = formula.charAt(position);
            if (op != '+' && op != '-') break;

            position++;
            Expression right = parseTerm();
            left = new BinaryOperation(left, op, right);
        }

        return left;
    }

    /**
     * Parse term: handles multiplication and division
     * Term -> Factor (('*' | '/') Factor)*
     */
    private Expression parseTerm() throws FormulaParseException {
        Expression left = parseFactor();

        while (position < formula.length()) {
            skipWhitespace();
            if (position >= formula.length()) break;

            char op = formula.charAt(position);
            if (op != '*' && op != '/') break;

            position++;
            Expression right = parseFactor();
            left = new BinaryOperation(left, op, right);
        }

        return left;
    }

    /**
     * Parse factor: handles numbers, cell references, functions, and parentheses
     * Factor -> Number | CellRef | Function | '(' Expression ')'
     */
    private Expression parseFactor() throws FormulaParseException {
        skipWhitespace();

        if (position >= formula.length()) {
            throw new FormulaParseException("Unexpected end of formula");
        }

        // Check for parentheses
        if (formula.charAt(position) == '(') {
            position++;
            Expression expr = parseExpression();
            skipWhitespace();
            if (position >= formula.length() || formula.charAt(position) != ')') {
                throw new FormulaParseException("Missing closing parenthesis");
            }
            position++;
            return expr;
        }

        // Check for number
        if (Character.isDigit(formula.charAt(position))) {
            return parseNumber();
        }

        // Check for cell reference or function
        if (Character.isLetter(formula.charAt(position))) {
            return parseCellOrFunction();
        }

        throw new FormulaParseException("Unexpected character at position " + position + ": " + formula.charAt(position));
    }

    /**
     * Parse number literal
     */
    private Expression parseNumber() {
        int start = position;
        while (position < formula.length() &&
               (Character.isDigit(formula.charAt(position)) ||
                formula.charAt(position) == '.')) {
            position++;
        }
        String numStr = formula.substring(start, position);
        return new Literal(Double.parseDouble(numStr));
    }

    /**
     * Parse cell reference or function call
     */
    private Expression parseCellOrFunction() throws FormulaParseException {
        int start = position;

        // Read letters
        while (position < formula.length() && Character.isLetter(formula.charAt(position))) {
            position++;
        }

        String identifier = formula.substring(start, position);
        skipWhitespace();

        // Check if it's a function (followed by '(')
        if (position < formula.length() && formula.charAt(position) == '(') {
            return parseFunction(identifier);
        }

        // Otherwise it's a cell reference - read the number part
        int numStart = position;
        while (position < formula.length() && Character.isDigit(formula.charAt(position))) {
            position++;
        }

        if (numStart == position) {
            throw new FormulaParseException("Invalid cell reference: " + identifier);
        }

        String cellCoord = identifier + formula.substring(numStart, position);

        // Check if it's part of a range (followed by ':')
        skipWhitespace();
        if (position < formula.length() && formula.charAt(position) == ':') {
            position++; // Skip ':'
            skipWhitespace();

            // Parse end cell
            String endCell = parseCellCoordinate();
            return new Range(cellCoord, endCell, spreadsheet);
        }

        return new CellReference(cellCoord, spreadsheet);
    }

    /**
     * Parse function call
     * Function -> FunctionName '(' Argument ')'
     */
    private Expression parseFunction(String functionName) throws FormulaParseException {
        position++; // Skip '('
        skipWhitespace();

        // Parse argument (could be range or expression)
        Expression argument = parseFunctionArgument();

        skipWhitespace();
        if (position >= formula.length() || formula.charAt(position) != ')') {
            throw new FormulaParseException("Missing closing parenthesis for function " + functionName);
        }
        position++;

        return new Function(functionName, argument);
    }

    /**
     * Parse function argument (can be range or expression)
     */
    private Expression parseFunctionArgument() throws FormulaParseException {
        // Try to parse as range first (look ahead for ':')
        int savedPosition = position;

        try {
            // Check if we have a cell reference followed by ':'
            if (Character.isLetter(formula.charAt(position))) {
                String startCell = parseCellCoordinate();
                skipWhitespace();
                if (position < formula.length() && formula.charAt(position) == ':') {
                    position++; // Skip ':'
                    skipWhitespace();
                    String endCell = parseCellCoordinate();
                    return new Range(startCell, endCell, spreadsheet);
                }
            }
        } catch (Exception e) {
            // Not a range, restore position
        }

        // Restore position and parse as general expression
        position = savedPosition;
        return parseExpression();
    }

    /**
     * Parse a cell coordinate (e.g., "A1", "B2", "AA10")
     */
    private String parseCellCoordinate() throws FormulaParseException {
        int start = position;

        // Read letters
        while (position < formula.length() && Character.isLetter(formula.charAt(position))) {
            position++;
        }

        if (start == position) {
            throw new FormulaParseException("Expected cell coordinate at position " + position);
        }

        String letters = formula.substring(start, position);

        // Read numbers
        int numStart = position;
        while (position < formula.length() && Character.isDigit(formula.charAt(position))) {
            position++;
        }

        if (numStart == position) {
            throw new FormulaParseException("Invalid cell coordinate: " + letters);
        }

        return letters + formula.substring(numStart, position);
    }

    /**
     * Skip whitespace characters
     */
    private void skipWhitespace() {
        while (position < formula.length() && Character.isWhitespace(formula.charAt(position))) {
            position++;
        }
    }
}
