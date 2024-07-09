import orm.DynamicORM;
import orm.annotations.Id;

public class Salles extends DynamicORM<Salles>{
    @Id
    Integer idsalles ;
    String salle;
    public Integer getIdsalles() {
        return idsalles;
    }
    public void setIdsalles(Integer idsalles) {
        this.idsalles = idsalles;
    }
    public String getSalle() {
        return salle;
    }
    public void setSalle(String salle) {
        this.salle = salle;
    }



    
} 