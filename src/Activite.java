import orm.DynamicORM;
import orm.annotations.Id;

public class Activite extends DynamicORM<Activite> {
    @Id
    String idactivite;
    String nomactivite;
    Integer typeactivite;

    public String getIdactivite() {
        return idactivite;
    }

    public void setIdactivite(String idactivite) {
        this.idactivite = idactivite;
    }

    public String getNomactivite() {
        return nomactivite;
    }

    public void setNomactivite(String nomactivite) {
        this.nomactivite = nomactivite;
    }

    public Integer getTypeactivite() {
        return typeactivite;
    }

    public void setTypeactivite(Integer typeactivite) {
        this.typeactivite = typeactivite;
    }
}
