package practica.compi2.ast.definitions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.helpers.CallHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class ClassDefinition extends AstNode {

    private AccessModifier access;
    private String name;
    @Setter
    private String parentName;
    private List<AttributeDefinition> attributes;
    private List<ConstructorDefinition> constructors;
    private List<MethodDefinition> methods;
    private ClassSymbol symbol;

    public ClassDefinition(Position position, AccessModifier access, String name) {
        super(position);
        this.access = access;
        this.name = name;
        this.attributes = new ArrayList<>();
        this.constructors = new ArrayList<>();
        this.methods = new ArrayList<>();
    }

    public void addAttribute(AttributeDefinition attribute) {
        attributes.add(attribute);
    }

    public void addConstructor(ConstructorDefinition constructor) {
        constructors.add(constructor);
    }

    public void addMethod(MethodDefinition method) {
        methods.add(method);
    }

    public void registerName(SemanticContext context, String expectedName) {
        if (!name.equals(expectedName)) {
            context.error(position, name, "La clase '" + name + "' debe estar en un archivo llamado " + name + ".z");
        }
        symbol = new ClassSymbol(name, access);
        if (!context.getGlobals().addClass(symbol)) {
            context.error(position, name, "Ya existe una estructura o clase llamada '" + name + "'");
        }
        context.getTypeTable().registerClass(symbol);
        context.report(new Symbol(name, new Type(TypeKind.CLASS, name, 0), SymbolKind.CLASS, -1));
    }

    public void linkParent(SemanticContext context) {
        if (parentName == null) {
            return;
        }
        ClassSymbol parent = context.getGlobals().getClass(parentName);
        if (parent == null) {
            context.error(position, parentName, "La clase padre '" + parentName + "' no existe");
            return;
        }
        if (parent.isSubclassOf(symbol)) {
            context.error(position, parentName, "Herencia cíclica: '" + parentName + "' ya hereda de '" + name + "'");
            return;
        }
        symbol.setParent(parent);
    }

    public int depth() {
        int depth = 0;
        for (ClassSymbol current = symbol == null ? null : symbol.getParent(); current != null;
                current = current.getParent()) {
            depth++;
        }
        return depth;
    }

    public void registerMembers(SemanticContext context) {
        context.setCurrentClass(symbol);
        boolean parentNeedsArguments = symbol.getParent() != null && !symbol.getParent().hasNoArgConstructor();
        if (constructors.isEmpty() && parentNeedsArguments) {
            context.error(position, name, "La clase " + symbol.getParent().getName() + " no tiene un constructor sin parámetros, "
                    + name + " debe declarar un constructor que llame a super(...)");
        }
        for (AttributeDefinition attribute : attributes) {
            attribute.registerIn(symbol, context);
        }
        for (ConstructorDefinition constructor : constructors) {
            constructor.registerIn(symbol, context);
        }
        for (MethodDefinition method : methods) {
            method.registerIn(symbol, context);
        }
        context.setCurrentClass(null);
    }

    public void validate(SemanticContext context) {
        context.setCurrentClass(symbol);
        for (AttributeDefinition attribute : attributes) {
            attribute.validate(context);
        }
        for (ConstructorDefinition constructor : constructors) {
            constructor.validate(context);
        }
        for (MethodDefinition method : methods) {
            method.validate(context);
        }
        context.setCurrentClass(null);
    }

    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.startFunction(symbol.getInitName(), 2);
        if (symbol.getParent() != null) {
            CallHelper.call(utils, symbol.getParent().getInitName(), MemoryHelper.thisPointer(utils), new ArrayList<>());
        }
        for (AttributeDefinition attribute : attributes) {
            attribute.toIntermediateCode(utils);
        }
        utils.endFunction();
        for (ConstructorDefinition constructor : constructors) {
            constructor.toIntermediateCode(utils);
        }
        for (MethodDefinition method : methods) {
            method.toIntermediateCode(utils);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("acceso", access);
        printer.value("nombre", name);
        printer.value("padre", parentName);
        printer.children("atributos", attributes);
        printer.children("constructores", constructors);
        printer.children("metodos", methods);
    }
}
