package prog_mobile.uqac.com.scanmonsters.database;

public class Friend {


    public int id;
    public String name;
    public int score;
    public long subscriptionDate;
    public boolean isInUQAC;
    public int roomFound;
    public int nbCreatures;
    public long lastActivity;
    public int lastAction;

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

    public void fromRawData(String data) {
        String[] playerData = data.split("-");
        id = -1;
        name = playerData[0];
        subscriptionDate = Long.parseLong(playerData[1]);
        isInUQAC = playerData[2].equals("1");
        score = roomFound = Integer.parseInt(playerData[3]);
        nbCreatures = Integer.parseInt(playerData[4]);
        lastActivity = Long.parseLong(playerData[5]);
        lastAction = Integer.parseInt(playerData[6]);
    }

    @Override
    public String toString() {
        return "Friend [id=" + id + ", name=" + name + ", score=" + score + "]";
    }
}