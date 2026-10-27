package practica.compi2.ast.statements;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.CallMatcher;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.types.Type;

@Getter
public class ConstructorCall extends Statement {

    private boolean self;
    private List<Expression> arguments;
    @Setter
    private boolean firstInConstructor;
    private FunctionSymbol constructor;

    public ConstructorCall(Position position, boolean self, List<Expression> arguments) {
        super(position);
        this.self = self;
        this.arguments = arguments;
    }

    @Override
    public void validate(SemanticContext sc) {
        List<Type> argumentTypes = CallMatcher.validateArguments(arguments, sc);
        String keyword = self ? "this" : "super";
        if (!firstInConstructor) {
            sc.error(position, keyword, keyword + "(...) debe ser la primera instrucción de un constructor");
            return;
        }
        ClassSymbol owner = self ? sc.getCurrentClass() : sc.getCurrentClass().getParent();
        if (owner == null) {
            sc.error(position, keyword, "La clase " + sc.getCurrentClass().getName() + " no hereda de ninguna clase");
            return;
        }
        if (owner.getConstructors().isEmpty() && arguments.isEmpty()) {
            constructor = owner.getImplicitConstructor();
            return;
        }
        constructor = CallMatcher.findOverload(owner.getConstructors(), argumentTypes, sc.getRules());
        if (constructor == null) {
            sc.error(position, keyword, "La clase " + owner.getName()
                    + " no tiene un constructor que reciba esos argumentos");
            return;
        }
        if (constructor == sc.getCurrentFunction()) {
            sc.error(position, keyword, "El constructor " + constructor.getSignature() + " se llama a sí mismo");
            return;
        }
        sc.checkAccess(position, keyword, constructor.getAccess(), owner,
                "El constructor " + constructor.getSignature());
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        if (constructor == null) {
            return;
        }
        List<MemoryAccess> values = new ArrayList<>();
        for (Expression argument : arguments) {
            values.add(argument.toIntermediateCode(u));
        }
        CallHelper.call(u, constructor.getCName(), MemoryHelper.thisPointer(u), values);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("llamada", self ? "this" : "super");
        printer.children("argumentos", arguments);
    }
}
