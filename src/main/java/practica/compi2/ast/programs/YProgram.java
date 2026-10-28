package practica.compi2.ast.programs;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.FunctionDefinition;
import practica.compi2.ast.definitions.StructDefinition;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.TypeRules;
import practica.compi2.semantic.types.YCompatibilityTable;

@Getter
public class YProgram extends Program {

    private List<StructDefinition> structs;
    private List<FunctionDefinition> functions;

    public YProgram(Position position, String fileName) {
        super(position, fileName);
        this.structs = new ArrayList<>();
        this.functions = new ArrayList<>();
    }

    public void addStruct(StructDefinition struct) {
        structs.add(struct);
    }

    public void addFunction(FunctionDefinition function) {
        functions.add(function);
    }

    public void registerTypes(SemanticContext context) {
        context.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (StructDefinition struct : structs) {
            struct.registerName(context);
        }
    }

    public void registerMembers(SemanticContext context) {
        context.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (StructDefinition struct : structs) {
            struct.registerFields(context);
        }
        for (FunctionDefinition function : functions) {
            function.register(context);
        }
    }

    public void validate(SemanticContext context) {
        context.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (FunctionDefinition function : functions) {
            function.validate(context);
        }
    }

    public void registerStructs(IntermediateCodeUtils utils) {
        for (StructDefinition struct : structs) {
            utils.registerStruct(struct.getSymbol());
        }
    }

    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.useBooleanWords("verdadero", "falso");
        for (FunctionDefinition function : functions) {
            function.toIntermediateCode(utils);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("archivo", fileName);
        printer.children("estructuras", structs);
        printer.children("funciones", functions);
    }
}
