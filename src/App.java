import orm.DynamicORM;

public class App {
    public static void main(String[] args) throws Exception {
        Salles sa= new Salles();
        sa.setSalle("dxasykan");
        Salles sa3= new Salles();
        sa3.setSalle("csadsfhd");

        new Salles().insertBatch(new Salles[]{sa,sa3});

    }
}
