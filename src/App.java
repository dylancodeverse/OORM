import orm.DynamicORM;

public class App {
    public static void main(String[] args) throws Exception {
        Salles sa= new Salles();
        sa.setIdsalles(5);
        sa.setSalle("dykan");
        Salles sa3= new Salles();
        sa3.setSalle("dsfhd");
        sa3.setIdsalles(6);
        new Salles().insertBatch(new Salles[]{sa,sa3});

    }
}
