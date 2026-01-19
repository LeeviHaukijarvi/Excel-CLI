import java.util.*;

public class DependencyManager {
    // Key: cell coordinate, Value: set of cells that this cell depends on
    private Map<String, Set<String>> dependencies;

    // Key: cell coordinate, Value: set of cells that depend on this cell
    private Map<String, Set<String>> dependents;

    public DependencyManager() {
        this.dependencies = new HashMap<>();
        this.dependents = new HashMap<>();
    }

    /**
     * Register that 'cell' depends on 'dependsOn'
     * Example: If B1 has formula =A1*2, call addDependency("B1", "A1")
     */
    public void addDependency(String cell, String dependsOn) {
        cell = cell.toUpperCase();
        dependsOn = dependsOn.toUpperCase();

        dependencies.computeIfAbsent(cell, k -> new HashSet<>()).add(dependsOn);
        dependents.computeIfAbsent(dependsOn, k -> new HashSet<>()).add(cell);
    }

    /**
     * Clear all dependencies for a cell (called when cell content changes)
     */
    public void clearDependencies(String cell) {
        cell = cell.toUpperCase();

        // Remove from dependents of other cells
        Set<String> deps = dependencies.get(cell);
        if (deps != null) {
            for (String dep : deps) {
                Set<String> depSet = dependents.get(dep);
                if (depSet != null) {
                    depSet.remove(cell);
                }
            }
        }

        dependencies.remove(cell);
    }

    /**
     * Get all cells that depend on this cell (directly or indirectly)
     * Used for recalculation
     */
    public Set<String> getAllDependents(String cell) {
        cell = cell.toUpperCase();
        Set<String> result = new HashSet<>();
        collectDependents(cell, result);
        return result;
    }

    private void collectDependents(String cell, Set<String> result) {
        Set<String> directDependents = dependents.get(cell);
        if (directDependents != null) {
            for (String dependent : directDependents) {
                if (!result.contains(dependent)) {
                    result.add(dependent);
                    collectDependents(dependent, result);
                }
            }
        }
    }

    /**
     * Check if adding a dependency from 'fromCell' to any cell in 'toCells' would create a cycle
     * Returns true if circular dependency detected
     */
    public boolean wouldCreateCycle(String fromCell, Set<String> toCells) {
        fromCell = fromCell.toUpperCase();

        for (String toCell : toCells) {
            toCell = toCell.toUpperCase();

            // If toCell depends on fromCell (directly or indirectly), adding
            // fromCell -> toCell would create a cycle
            if (dependsOn(toCell, fromCell)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if 'cell' depends on 'target' (directly or indirectly)
     */
    private boolean dependsOn(String cell, String target) {
        if (cell.equals(target)) {
            return true;
        }

        Set<String> deps = dependencies.get(cell);
        if (deps == null) {
            return false;
        }

        Set<String> visited = new HashSet<>();
        return dependsOnRecursive(cell, target, visited);
    }

    private boolean dependsOnRecursive(String cell, String target, Set<String> visited) {
        if (cell.equals(target)) {
            return true;
        }

        if (visited.contains(cell)) {
            return false;
        }
        visited.add(cell);

        Set<String> deps = dependencies.get(cell);
        if (deps == null) {
            return false;
        }

        for (String dep : deps) {
            if (dependsOnRecursive(dep, target, visited)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get topological sort order for recalculation
     * Returns cells in order such that dependencies are calculated before dependents
     */
    public List<String> getCalculationOrder(Set<String> cells) {
        List<String> result = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> tempMark = new HashSet<>();

        for (String cell : cells) {
            if (!visited.contains(cell)) {
                topologicalSort(cell, visited, tempMark, result);
            }
        }

        return result;
    }

    private void topologicalSort(String cell, Set<String> visited,
                                  Set<String> tempMark, List<String> result) {
        if (tempMark.contains(cell)) {
            throw new RuntimeException("Circular dependency detected involving " + cell);
        }

        if (visited.contains(cell)) {
            return;
        }

        tempMark.add(cell);

        Set<String> deps = dependencies.get(cell);
        if (deps != null) {
            for (String dep : deps) {
                topologicalSort(dep, visited, tempMark, result);
            }
        }

        tempMark.remove(cell);
        visited.add(cell);
        result.add(cell);
    }

    /**
     * Clear all dependencies (for new spreadsheet)
     */
    public void reset() {
        dependencies.clear();
        dependents.clear();
    }
}
