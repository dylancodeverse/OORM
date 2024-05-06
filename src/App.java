import orm.DynamicORM;

public class App {
    public static void main(String[] args) throws Exception {
        Activite p = new Activite();
        p.setNomactivite("OK");
        p.insert();

        System.out.println(p.getIdactivite());

    }
}
