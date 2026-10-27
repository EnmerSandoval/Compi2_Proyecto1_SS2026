package practica.compi2.riscv.simulador;

public class RiscVRunner {

    public static long run(String source, SimulatorIO io) {
        // ensamblar y ejecutar
        AssembledProgram program = new Assembler().assemble(source);
        return new Simulator(program, io).run(Simulator.DEFAULT_STEP_LIMIT);
    }
}
