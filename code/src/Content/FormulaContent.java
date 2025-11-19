package Content;

import Formula.Expression;
import Formula.FormulaEngine;

public class FormulaContent extends Content {
    private Expression expression;
    private FormulaEngine formulaEngine;
    @Override
    Object getValue() {
       return formulaEngine.calculate();
    }
}
