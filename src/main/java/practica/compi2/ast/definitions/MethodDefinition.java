package practica.compi2.ast.definitions;

import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Frame;
import practica.compi2.semantic.symbols.SymbolKind;

@Getter
public class MethodDefinition extends FunctionDefinition {

    private AccessModifier access;

    public MethodDefinition(Position position, AccessModifier access, String name, List<Parameter> parameters,
            TypeReference returnType, Block body) {
        super(position, name, parameters, returnType, body);
        this.access = access;
    }

    public void registerIn(ClassSymbol owner, SemanticContext sc) {
        symbol = createSymbol(sc, owner.getName(), SymbolKind.METHOD);
        if (!owner.addMethod(symbol)) {
            sc.error(position, name, "El método " + symbol.getSignature()
                    + " ya existe en la clase " + owner.getName());
        }
    }

    @Override
    public void validate(SemanticContext sc) {
        validateBody(sc, new Frame(symbol.getOwner() + "." + name, 2));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("acceso", access);
        super.print(printer);
    }
}
