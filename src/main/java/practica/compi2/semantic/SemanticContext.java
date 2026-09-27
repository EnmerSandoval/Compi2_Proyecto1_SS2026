package practica.compi2.semantic;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.errors.ErrorManager;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Frame;
import practica.compi2.semantic.symbols.FunctionSymbol;
import practica.compi2.semantic.symbols.GlobalTable;
import practica.compi2.semantic.symbols.Scope;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;
import practica.compi2.semantic.types.TypeTable;

@Getter
public class SemanticContext {

    private final ErrorManager em;
    private final GlobalTable globals;
    private final List<Symbol> symbols;
    private final TypeResolver typeResolver;
    private final TypeTable typeTable;
    private String fileName;
    private TypeRules rules;
    @Setter
    private ClassSymbol currentClass;
    private FunctionSymbol currentFunction;
    private Scope scope;
    private Frame frame;
    private int loopDepth;
    private int switchDepth;
    @Setter
    private boolean returnFound;

    public SemanticContext(ErrorManager em) {
        this.em = em;
        this.globals = new GlobalTable();
        this.symbols = new ArrayList<>();
        this.typeResolver = new TypeResolver(this);
        this.typeTable = new TypeTable();
    }

    public void useFile(String fileName, TypeRules rules) {
        this.fileName = fileName;
        this.rules = rules;
    }

    public void error(Position position, String lexeme, String description) {
        if (lexeme.isEmpty() && position != null) {
            lexeme = position.getLexeme();
        }
        em.addSemanticError(fileName, position, lexeme, description);
    }

    public void enterFunction(FunctionSymbol function, Frame newFrame) {
        this.currentFunction = function;
        this.frame = newFrame;
        this.scope = new Scope(null);
        this.returnFound = false;
        this.loopDepth = 0;
        this.switchDepth = 0;
    }

    public void enterMain(Frame mainFrame) {
        enterFunction(null, mainFrame);
    }

    public void exitFunction() {
        this.currentFunction = null;
        this.frame = null;
        this.scope = null;
    }

    public void enterScope() {
        scope = new Scope(scope);
    }

    public void exitScope() {
        scope = scope.getParent();
    }

    public Symbol declareVariable(String name, Type type, Position position) {
        Symbol symbol = new Symbol(name, type, SymbolKind.VARIABLE, frame.allocate());
        declare(symbol, position);
        return symbol;
    }

    public void declareParameter(Symbol parameter, Position position) {
        parameter.setOffset(frame.allocate());
        declare(parameter, position);
    }

    public void declareStruct(StructSymbol struct, Position position) {
        if (!scope.declareStruct(struct)) {
            error(position, struct.getName(), "La estructura '" + struct.getName() + "' ya fue declarada en este ámbito");
        }
    }

    public void report(Symbol symbol) {
        symbol.setFile(fileName);
        if (symbol.getScope() == null) {
            symbol.setScope(frame == null ? "global" : frame.getName());
        }
        symbols.add(symbol);
    }

    public Symbol lookupVariable(String name) {
        Symbol symbol = null;
        if (scope != null) {
            symbol = scope.lookup(name);
        }
        if (symbol == null && currentClass != null) {
            symbol = currentClass.getAttribute(name);
        }
        return symbol;
    }

    public StructSymbol lookupStruct(String name) {
        if (scope != null && scope.lookupStruct(name) != null) {
            return scope.lookupStruct(name);
        }
        return globals.getStruct(name);
    }

    public Symbol findMember(Type owner, String member) {
        return typeResolver.findMember(owner, member);
    }

    public Type resolveType(TypeReference reference) {
        return typeResolver.resolve(reference);
    }

    public void checkCondition(Expression condition, String statement) {
        Type conditionType = condition.validate(this);
        if (!conditionType.isError() && !conditionType.isBoolean()) {
            error(condition.getPosition(), "", "La condición del " + statement + " debe ser booleana y es " + conditionType);
        }
    }

    public void enterLoop() {
        loopDepth++;
    }

    public void exitLoop() {
        loopDepth--;
    }

    public void enterSwitch() {
        switchDepth++;
    }

    public void exitSwitch() {
        switchDepth--;
    }

    public boolean isInsideLoop() {
        return loopDepth > 0;
    }

    public boolean isInsideSwitch() {
        return switchDepth > 0;
    }

    private void declare(Symbol symbol, Position position) {
        if (!scope.declare(symbol)) {
            error(position, symbol.getName(), "'" + symbol.getName() + "' ya fue declarado en este ámbito");
        }
        report(symbol);
    }
}
