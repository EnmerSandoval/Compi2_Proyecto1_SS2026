package practica.compi2.c3d.cuartetas;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FunctionStart3D extends Cuarteta {

    private String name;
    private boolean main;
    private int firstTemporal;
    @Setter
    private int lastTemporal;

    public FunctionStart3D(String name, boolean main, int firstTemporal) {
        this.name = name;
        this.main = main;
        this.firstTemporal = firstTemporal;
        this.lastTemporal = firstTemporal;
    }

    @Override
    public void toCCode(StringBuilder sb) {
        if (main) {
            sb.append("int main() {\n");
        } else {
            sb.append("void ").append(name).append("() {\n");
        }
        for (int i = firstTemporal; i < lastTemporal; i++) {
            if ((i - firstTemporal) % 10 == 0) {
                sb.append("    double t").append(i);
            } else {
                sb.append(", t").append(i);
            }
            if ((i - firstTemporal) % 10 == 9 || i == lastTemporal - 1) {
                sb.append(";\n");
            }
        }
    }

    @Override
    public String[] toRow() {
        return new String[]{"inicio", name, "", ""};
    }
}
