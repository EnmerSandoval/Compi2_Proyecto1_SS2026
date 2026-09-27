package practica.compi2.c3d.cuartetas;

public class Return3D extends Cuarteta {

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("return;\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"return", "", "", ""};
    }
}
