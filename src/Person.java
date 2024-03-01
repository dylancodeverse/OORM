import orm.DynamicORM;

public class Person extends DynamicORM<Person> {
    Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
