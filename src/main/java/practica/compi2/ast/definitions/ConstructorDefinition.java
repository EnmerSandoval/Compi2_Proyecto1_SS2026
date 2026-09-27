package practica.compi2.ast.definitions;

import java.util.List;
import practica.compi2.ast.Position;
import practica.compi2.ast.statements.Block;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.SymbolKind;

public class ConstructorDefinition extends MethodDefinition {

    public ConstructorDefinition(Position position, AccessModifier access, String name,
            List<Parameter> parameters, Block body) {
        super(position, access, name, parameters, null, body);
    }

    @Override
    public void registerIn(ClassSymbol owner, SemanticContext sc) {
        if (!name.equals(owner.getName())) {
            sc.error(position, name, "'" + name + "' no tiene tipo de retorno, un constructor debe llamarse "
                    + owner.getName());
        }
        symbol = createSymbol(sc, owner.getName(), SymbolKind.CONSTRUCTOR);
        if (!owner.addConstructor(symbol)) {
            sc.error(position, name, "El constructor " + symbol.getSignature() + " ya existe");
        }
    }
}
