import java.util.*;

public class Spreadsheet {
    private Map<String, Cell> cells;
    private DependencyManager dependencyManager;

    public Spreadsheet() {
        this.cells = new HashMap<>();
        this.dependencyManager = new DependencyManager();
    }

    public Cell getCell(String coord) {
        return cells.computeIfAbsent(coord.toUpperCase(), Cell::new);
    }

    /**
     * Set cell content with automatic type detection and dependency tracking
     */
    public void setCellContent(String coord, String rawInput) throws Exception {
        coord = coord.toUpperCase();
        Cell cell = getCell(coord);

        // Clear old dependencies for this cell
        dependencyManager.clearDependencies(coord);

        // Detect content type and create appropriate Content object
        Content newContent = parseContent(rawInput);

        // Extract dependencies if formula
        if (newContent instanceof FormulaContent) {
            Set<String> referencedCells = extractReferences((FormulaContent) newContent);

            // Check for circular dependencies
            if (dependencyManager.wouldCreateCycle(coord, referencedCells)) {
                throw new RuntimeException("Circular dependency detected: Cannot set " + coord);
            }

            // Add dependencies
            for (String refCell : referencedCells) {
                dependencyManager.addDependency(coord, refCell);
            }
        }

        // Set the content
        cell.setContent(newContent);

        // Recalculate dependent cells
        recalculateDependents(coord);
    }

    /**
     * Parse raw input string and detect content type
     */
    private Content parseContent(String rawInput) throws Exception {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return new TextContent("");
        }

        String trimmed = rawInput.trim();

        // Formula detection
        if (trimmed.startsWith("=")) {
            try {
                FormulaEngine engine = new FormulaEngine(trimmed, this);
                Expression expr = engine.parse();
                return new FormulaContent(trimmed, expr);
            } catch (FormulaParseException e) {
                throw new RuntimeException("Formula syntax error: " + e.getMessage());
            }
        }

        // Numeric detection
        try {
            double number = Double.parseDouble(trimmed);
            return new NumericContent(number);
        } catch (NumberFormatException e) {
            // Not a number, treat as text
        }

        // Default to text
        return new TextContent(trimmed);
    }

    /**
     * Extract all cell references from a formula
     */
    private Set<String> extractReferences(FormulaContent formula) {
        Set<String> references = new HashSet<>();
        collectReferences(formula.getExpression(), references);
        return references;
    }

    private void collectReferences(Expression expr, Set<String> references) {
        if (expr instanceof CellReference) {
            references.add(((CellReference) expr).getCoordinate());
        } else if (expr instanceof Range) {
            Range range = (Range) expr;
            // Add all cells in range
            for (String coord : range.getAllCoordinates()) {
                references.add(coord);
            }
        } else if (expr instanceof Function) {
            Function func = (Function) expr;
            collectReferences(func.getArgument(), references);
        } else if (expr instanceof BinaryOperation) {
            BinaryOperation binOp = (BinaryOperation) expr;
            collectReferences(binOp.getLeft(), references);
            collectReferences(binOp.getRight(), references);
        }
        // Literal has no references
    }

    /**
     * Recalculate all cells that depend on the changed cell
     */
    private void recalculateDependents(String changedCell) throws Exception {
        Set<String> dependents = dependencyManager.getAllDependents(changedCell);

        if (dependents.isEmpty()) {
            return;
        }

        // Get calculation order (topological sort)
        List<String> calcOrder = dependencyManager.getCalculationOrder(dependents);

        // Recalculate in order
        for (String cellCoord : calcOrder) {
            Cell cell = cells.get(cellCoord);
            if (cell != null && cell.getContent() instanceof FormulaContent) {
                // Force recalculation by re-evaluating the formula
                // The getValue() call will trigger calculation
                cell.getContent().getValue();
            }
        }
    }

    /**
     * Get evaluated cell value (for display)
     */
    public String getCellContent(String coord) {
        return getCell(coord.toUpperCase()).getDisplayValue();
    }

    /**
     * Calculate all formulas in the spreadsheet
     */
    public void calculateAll() throws Exception {
        // Get all cells with formulas
        Set<String> formulaCells = new HashSet<>();
        for (Map.Entry<String, Cell> entry : cells.entrySet()) {
            if (entry.getValue().getContent() instanceof FormulaContent) {
                formulaCells.add(entry.getKey());
            }
        }

        if (formulaCells.isEmpty()) {
            return;
        }

        // Calculate in dependency order
        List<String> calcOrder = dependencyManager.getCalculationOrder(formulaCells);

        for (String coord : calcOrder) {
            Cell cell = cells.get(coord);
            if (cell != null) {
                cell.getContent().getValue(); // Trigger calculation
            }
        }
    }

    public Map<String, Cell> getAllCells() {
        return cells;
    }

    /**
     * Reset spreadsheet (for UC1: Create New Spreadsheet)
     */
    public void reset() {
        cells.clear();
        dependencyManager.reset();
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }
}
