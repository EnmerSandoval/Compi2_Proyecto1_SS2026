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

    public void registerTypes(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (StructDefinition struct : structs) {
            struct.registerName(sc);
        }
    }

    public void registerMembers(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (StructDefinition struct : structs) {
            struct.registerFields(sc);
        }
        for (FunctionDefinition function : functions) {
            function.register(sc);
        }
    }

    public void validate(SemanticContext sc) {
        sc.useFile(fileName, new TypeRules(new YCompatibilityTable()));
        for (FunctionDefinition function : functions) {
            function.validate(sc);
        }
    }

    public void registerStructs(IntermediateCodeUtils u) {
        for (StructDefinition struct : structs) {
            u.registerStruct(struct.getSymbol());
        }
    }

    public void toIntermediateCode(IntermediateCodeUtils u) {
        u.useBooleanWords("verdadero", "falso");
        for (FunctionDefinition function : functions) {
            function.toIntermediateCode(u);
        }
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("archivo", fileName);
        printer.children("estructuras", structs);
        printer.children("funciones", functions);
    }
}
