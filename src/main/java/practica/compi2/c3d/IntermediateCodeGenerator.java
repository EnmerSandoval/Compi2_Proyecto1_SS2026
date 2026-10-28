package practica.compi2.c3d;

import practica.compi2.ast.Ast;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.c3d.natives.NativeLibrary;
import practica.compi2.semantic.types.TypeTable;

public class IntermediateCodeGenerator {

    public IntermediateCodeUtils generate(Ast ast, TypeTable typeTable) {
        IntermediateCodeUtils utils = new IntermediateCodeUtils();
        utils.setTypeTable(typeTable);
        for (YProgram program : ast.getYPrograms()) {
            program.registerStructs(utils);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            utils.registerClass(program.getClassDefinition().getSymbol());
        }
        NativeLibrary.generate(utils);
        for (YProgram program : ast.getYPrograms()) {
            program.toIntermediateCode(utils);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            program.toIntermediateCode(utils);
        }
        ast.getMain().toIntermediateCode(utils);
        utils.linkReturns();
        return utils;
    }
}
