package prog_mobile.uqac.com.scanmonsters.database;

public class Friend {


    public int id;
    public String name;
    public int score;

    public Friend(){}

    public Friend(String name, int score) {
        this(-1,name,score);
    }

    public Friend(int id, String name, int score) {
        super();
        this.id = id;
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Friend [id=" + id + ", name=" + name + ", score=" + score + "]";
    }
}