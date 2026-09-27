package practica.compi2.ast.statements;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class SwitchStmt extends Statement {

    private Expression expression;
    private List<SwitchCase> cases;

    public SwitchStmt(Position position, Expression expression, List<SwitchCase> cases) {
        super(position);
        this.expression = expression;
        this.cases = cases;
    }

    @Override
    public void validate(SemanticContext sc) {
        Type switchType = expression.validate(sc);
        int defaultCases = 0;
        sc.enterSwitch();
        sc.enterScope();
        for (SwitchCase switchCase : cases) {
            if (switchCase.isDefault()) {
                defaultCases++;
            }
            switchCase.validate(sc, switchType);
        }
        sc.exitScope();
        sc.exitSwitch();
        if (defaultCases > 1) {
            sc.error(position, "", "Solo puede haber un caso por defecto");
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess value = expression.toIntermediateCode(u);
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        int defaultLabel = endLabel;
        List<Integer> caseLabels = new ArrayList<>();
        for (SwitchCase switchCase : cases) {
            int caseLabel = u.getCounter().getAndIncrementLabelCount();
            caseLabels.add(caseLabel);
            if (switchCase.isDefault()) {
                defaultLabel = caseLabel;
            } else {
                switchCase.jumpIfMatches(u, value, expression.getType(), caseLabel);
            }
        }
        u.getCuartetas().add(new Goto3D(defaultLabel));
        u.enterSwitch(endLabel);
        for (int i = 0; i < cases.size(); i++) {
            u.getCuartetas().add(new LabelDefinition3D(caseLabels.get(i)));
            cases.get(i).toIntermediateCode(u);
        }
        u.exitSwitch();
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("expresion", expression);
        printer.children("casos", cases);
    }
}
