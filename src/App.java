import orm.DynamicORM;

public class App {
    public static void main(String[] args) throws Exception {
         Person p =new Person();
         p.setName("La");
         p.setId(1);
         p.updateById();
        
    }
}
