package practica.compi2.ast.definitions;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.Position;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.ConstructorCall;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.symbols.SymbolKind;

public class ConstructorDefinition extends MethodDefinition {

    private ClassSymbol owner;
    private boolean explicitCall;
    private FunctionSymbol parentConstructor;

    public ConstructorDefinition(Position position, AccessModifier access, String name,
            List<Parameter> parameters, Block body) {
        super(position, access, name, parameters, null, body);
    }

    @Override
    public void registerIn(ClassSymbol owner, SemanticContext context) {
        this.owner = owner;
        if (!name.equals(owner.getName())) {
            context.error(position, name, "'" + name + "' no tiene tipo de retorno, un constructor debe llamarse "
                    + owner.getName());
        }
        symbol = createSymbol(context, owner.getName(), SymbolKind.CONSTRUCTOR);
        symbol.setAccess(getAccess() == AccessModifier.DEFAULT ? AccessModifier.PUBLIC : getAccess());
        symbol.setOwnerClass(owner);
        if (!owner.addConstructor(symbol)) {
            context.error(position, name, "El constructor " + symbol.getSignature() + " ya existe");
        }
        if (!body.getStatements().isEmpty() && body.getStatements().get(0) instanceof ConstructorCall call) {
            call.setFirstInConstructor(true);
            explicitCall = true;
        }
    }

    @Override
    public void validate(SemanticContext context) {
        ClassSymbol parent = owner.getParent();
        if (parent != null && !explicitCall) {
            if (parent.hasNoArgConstructor()) {
                parentConstructor = parent.getImplicitConstructor();
            } else {
                context.error(position, name, "La clase " + parent.getName() + " no tiene un constructor sin parámetros, "
                        + "el constructor " + symbol.getSignature() + " debe llamar a super(...) en su primera instrucción");
            }
        }
        super.validate(context);
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.startFunction(symbol.getCName(), symbol.getFrameSize());
        if (parentConstructor != null) {
            CallHelper.call(utils, parentConstructor.getCName(), MemoryHelper.thisPointer(utils), new ArrayList<>());
        }
        body.toIntermediateCode(utils);
        utils.endFunction();
    }
}
