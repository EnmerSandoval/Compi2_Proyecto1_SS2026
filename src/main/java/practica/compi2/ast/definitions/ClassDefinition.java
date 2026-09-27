package practica.compi2.ast.definitions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
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

    public void registerName(SemanticContext sc, String expectedName) {
        if (!name.equals(expectedName)) {
            sc.error(position, name, "La clase '" + name + "' debe estar en un archivo llamado " + name + ".z");
        }
        symbol = new ClassSymbol(name);
        if (!sc.getGlobals().addClass(symbol)) {
            sc.error(position, name, "Ya existe una estructura o clase llamada '" + name + "'");
        }
        sc.getTypeTable().registerClass(symbol);
        sc.report(new Symbol(name, new Type(TypeKind.CLASS, name, 0), SymbolKind.CLASS, -1));
    }

    public void registerMembers(SemanticContext sc) {
        sc.setCurrentClass(symbol);
        for (AttributeDefinition attribute : attributes) {
            attribute.registerIn(symbol, sc);
        }
        for (ConstructorDefinition constructor : constructors) {
            constructor.registerIn(symbol, sc);
        }
        for (MethodDefinition method : methods) {
            method.registerIn(symbol, sc);
        }
        sc.setCurrentClass(null);
    }

    public void validate(SemanticContext sc) {
        sc.setCurrentClass(symbol);
        for (AttributeDefinition attribute : attributes) {
            attribute.validate(sc);
        }
        for (ConstructorDefinition constructor : constructors) {
            constructor.validate(sc);
        }
        for (MethodDefinition method : methods) {
            method.validate(sc);
        }
        sc.setCurrentClass(null);
    }

    public void toIntermediateCode(IntermediateCodeUtils u) {
        u.startFunction(symbol.getInitName(), 2);
        for (AttributeDefinition attribute : attributes) {
            attribute.toIntermediateCode(u);
        }
        u.endFunction();
        for (ConstructorDefinition constructor : constructors) {
            constructor.toIntermediateCode(u);
        }
        for (MethodDefinition method : methods) {
            method.toIntermediateCode(u);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("acceso", access);
        printer.value("nombre", name);
        printer.children("atributos", attributes);
        printer.children("constructores", constructors);
        printer.children("metodos", methods);
    }
}
