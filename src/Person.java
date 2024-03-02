import orm.DynamicORM;
import orm.annotations.Id;

public class Person extends DynamicORM<Person> {
    String name;
    @Id
    Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
