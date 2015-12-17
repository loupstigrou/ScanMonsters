package prog_mobile.uqac.com.scanmonsters.database;

public class Creature {


    public int id;
    public String name;
    public int quantity;
    public long date;

    public Creature(){}

    public Creature(String name, int quantity) {
        this(-1,name,quantity);
    }

    public Creature(int id, String name, int quantity) {
        super();
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Creature [id=" + id + ", name=" + name + ", quantity=" + quantity + "]";
    }
}