package practica.compi2.riscv.simulador;

public enum Op {
    // x entero, f decimal, n numero, l etiqueta, m memoria, r redondeo
    ADD("x x x"), ADDI("x x n"), SLLI("x x n"), DIV("x x x"), REM("x x x"),
    LI("x n"), LA("x l"), MV("x x"),
    BEQZ("x l"), BNEZ("x l"), BGE("x x l"), J("l"), ECALL(""),
    FLD("f m"), FSD("f m"),
    FADD_D("f f f"), FSUB_D("f f f"), FMUL_D("f f f"), FDIV_D("f f f"), FMSUB_D("f f f f"),
    FEQ_D("x f f"), FLT_D("x f f"), FLE_D("x f f"),
    FCVT_D_L("f x"), FCVT_L_D("x f r"), FMV_D("f f"), FNEG_D("f f");

    private final String[] operands;

    Op(String operands) {
        this.operands = operands.isEmpty() ? new String[0] : operands.split(" ");
    }

    public String[] getOperands() {
        return operands;
    }

    // fadd.d -> FADD_D
    public static Op fromMnemonic(String mnemonic) {
        try {
            return valueOf(mnemonic.toUpperCase().replace('.', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
