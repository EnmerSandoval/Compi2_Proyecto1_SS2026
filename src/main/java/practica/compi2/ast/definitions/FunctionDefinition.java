package practica.compi2.ast.definitions;

import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Frame;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;

@Getter
public class FunctionDefinition extends AstNode {

    protected String name;
    protected List<Parameter> parameters;
    protected TypeReference returnType;
    protected Block body;
    protected FunctionSymbol symbol;

    public FunctionDefinition(Position position, String name, List<Parameter> parameters,
            TypeReference returnType, Block body) {
        super(position);
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public void register(SemanticContext context) {
        symbol = createSymbol(context, null, SymbolKind.FUNCTION);
        if (!context.getGlobals().addFunction(symbol)) {
            context.error(position, name, "La función '" + name + "' ya fue declarada");
        }
    }

    public void validate(SemanticContext context) {
        validateBody(context, new Frame(name, 1));
    }

    protected FunctionSymbol createSymbol(SemanticContext context, String owner, SymbolKind kind) {
        Type resolvedReturn = Type.VOID;
        if (returnType != null) {
            resolvedReturn = context.resolveType(returnType);
        }
        FunctionSymbol function = new FunctionSymbol(name, resolvedReturn, owner);
        for (Parameter parameter : parameters) {
            function.addParameter(parameter.createSymbol(context));
        }
        Symbol row = new Symbol(function.getSignature(), resolvedReturn, kind, -1);
        row.setScope(owner);
        context.report(row);
        return function;
    }

    protected void validateBody(SemanticContext context, Frame frame) {
        context.enterFunction(symbol, frame);
        for (int i = 0; i < parameters.size(); i++) {
            context.declareParameter(symbol.getParameters().get(i), parameters.get(i).getPosition());
        }
        body.validateStatements(context);
        Type expected = symbol.getReturnType();
        if (!expected.isVoid() && !expected.isError() && !context.isReturnFound()) {
            context.error(position, name, "'" + name + "' debe retornar un valor de tipo " + expected);
        }
        symbol.setFrameSize(frame.getSize());
        context.exitFunction();
    }

    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.startFunction(symbol.getCName(), symbol.getFrameSize());
        body.toIntermediateCode(utils);
        utils.endFunction();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("nombre", name);
        printer.value("retorno", returnType);
        printer.children("parametros", parameters);
        printer.child("cuerpo", body);
    }
}
