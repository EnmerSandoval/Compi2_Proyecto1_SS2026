package practica.compi2.console;

import java.nio.file.Files;
import java.nio.file.Path;
import practica.compi2.ast.Ast;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.programs.Program;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.c3d.CCodeGenerator;
import practica.compi2.compiler.CompilationResult;
import practica.compi2.compiler.Compiler;
import practica.compi2.riscv.RiscVGenerator;
import practica.compi2.riscv.simulador.RiscVException;
import practica.compi2.riscv.simulador.RiscVRunner;
import practica.compi2.riscv.simulador.StreamIO;

public class ConsoleRunner {

    private final ConsoleOptions options;
    private final AstPrinter astPrinter;
    private final ErrorPrinter errorPrinter;
    private final SymbolPrinter symbolPrinter;
    private final CuartetaPrinter cuartetaPrinter;
    private final TypeReportPrinter typeReportPrinter;

    public ConsoleRunner(ConsoleOptions options) {
        this.options = options;
        this.astPrinter = new AstPrinter();
        this.errorPrinter = new ErrorPrinter();
        this.symbolPrinter = new SymbolPrinter();
        this.cuartetaPrinter = new CuartetaPrinter();
        this.typeReportPrinter = new TypeReportPrinter();
    }

    public void run() {
        Path file = Path.of(options.getPath()).toAbsolutePath().normalize();
        if (!Files.exists(file)) {
            System.out.println("No se encontró el archivo: " + file);
            return;
        }

        CompilationResult compilation = new Compiler().compile(file);
        if (options.isShowAst()) {
            printAst(compilation.getAst());
        }
        if (compilation.getTypeTable() != null) {
            System.out.println("================ TABLA DE SÍMBOLOS ================");
            System.out.println(symbolPrinter.print(compilation.getSymbols()));
            if (options.isShowTypes()) {
                System.out.println(typeReportPrinter.print(compilation.getTypeTable()));
            }
        }
        System.out.println("================ ERRORES ================");
        System.out.println(errorPrinter.print(compilation.getErrors()));
        if (compilation.hasErrors()) {
            return;
        }
        if (!compilation.hasCode()) {
            System.out.println("Para generar código se necesita compilar un archivo .pig");
            return;
        }
        printCode(compilation);
    }

    private void printCode(CompilationResult compilation) {
        if (options.isShowCuartetas()) {
            System.out.println("================ CUARTETAS ================");
            System.out.println(cuartetaPrinter.print(compilation.getCuartetas()));
        }
        if (options.isShowCode()) {
            System.out.println("================ CÓDIGO DE TRES DIRECCIONES ================");
            System.out.println(compilation.getCode());
        }
        if (options.isShowRiscV()) {
            System.out.println("================ RISC-V ================");
            System.out.println(compilation.getRiscVCode());
        }
        Path folder = Path.of(System.getProperty("user.dir"));
        System.out.println(new CCodeGenerator().generateFile(compilation.getCode(), folder));
        System.out.println(new RiscVGenerator().generateFile(compilation.getRiscVCode(), folder));
        if (options.isRunRiscV()) {
            runRiscV(compilation.getRiscVCode());
        }
    }

    private void runRiscV(String code) {
        System.out.println("================ EJECUCIÓN EN EL SIMULADOR RISC-V ================");
        try {
            long steps = RiscVRunner.run(code, new StreamIO(System.in, System.out));
            System.out.flush();
            System.out.println();
            System.out.println("[Programa terminado, " + steps + " instrucciones ejecutadas]");
        } catch (RiscVException e) {
            System.out.flush();
            System.out.println();
            System.out.println("[Error en el simulador] " + e.getMessage());
        }
    }

    private void printAst(Ast ast) {
        if (ast.getMain() != null) {
            printProgram(ast.getMain());
        }
        for (YProgram program : ast.getYPrograms()) {
            printProgram(program);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            printProgram(program);
        }
    }

    private void printProgram(Program program) {
        System.out.println("================ AST: " + program.getFileName() + " ================");
        System.out.println(astPrinter.print(program));
    }
}
