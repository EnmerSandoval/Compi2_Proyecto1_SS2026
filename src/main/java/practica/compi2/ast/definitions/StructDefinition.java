package practica.compi2.ast.definitions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.ast.statements.Statement;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class StructDefinition extends Statement {

    private String name;
    private List<Declaration> fields;
    private StructSymbol symbol;

    public StructDefinition(Position position, String name) {
        super(position);
        this.name = name;
        this.fields = new ArrayList<>();
    }

    public void addField(Declaration field) {
        fields.add(field);
    }

    public void registerName(SemanticContext context) {
        symbol = new StructSymbol(name, this);
        if (!context.getGlobals().addStruct(symbol)) {
            context.error(position, name, "Ya existe una estructura o clase llamada '" + name + "'");
        }
        context.getTypeTable().registerStruct(symbol);
        reportStruct(context);
    }

    public void registerFields(SemanticContext context) {
        for (Declaration field : fields) {
            checkConstantSizes(field, context);
            Type fieldType = context.resolveType(field.getType());
            Symbol fieldSymbol = new Symbol(field.getId(), fieldType, SymbolKind.FIELD, symbol.getFields().size());
            fieldSymbol.setScope(name);
            if (!symbol.addField(fieldSymbol)) {
                context.error(field.getPosition(), field.getId(), "El campo '" + field.getId()
                        + "' está repetido en la estructura " + name);
            }
            context.report(fieldSymbol);
        }
    }

    @Override
    public void validate(SemanticContext context) {
        symbol = new StructSymbol(name, this);
        context.declareStruct(symbol, position);
        context.getTypeTable().registerStruct(symbol);
        reportStruct(context);
        registerFields(context);
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.registerStruct(symbol);
    }

    private void checkConstantSizes(Declaration field, SemanticContext context) {
        for (Expression size : field.getSizes()) {
            if (!size.isConstant()) {
                context.error(size.getPosition(), field.getId(), "El tamaño del arreglo '" + field.getId()
                        + "' debe ser una constante dentro de una estructura");
            } else if (!size.validate(context).is(TypeKind.INTEGER)) {
                context.error(size.getPosition(), field.getId(), "El tamaño del arreglo '" + field.getId()
                        + "' debe ser entero");
            }
        }
    }

    private void reportStruct(SemanticContext context) {
        Type structType = new Type(TypeKind.STRUCT, name, 0);
        context.report(new Symbol(name, structType, SymbolKind.STRUCT, -1));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("nombre", name);
        printer.children("campos", fields);
    }
}
