import orm.DynamicORM;

public class App {
    public static void main(String[] args) throws Exception {
        Person p = new Person();
        p.setName("caca");
        p.insert();

        System.out.println(p.getId());

    }
}
