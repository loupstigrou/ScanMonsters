package prog_mobile.uqac.com.scanmonsters.user;

import java.io.Serializable;

/**
 * Created by Major on 14/10/2015.
 */

/**
 * Classe représentant un utilisateur
 * Login + Password
 */
public class User implements Serializable {

    private String login;
    private String password;
    private String token;

    private String room;
    private String creature;

    public User(String login, String password) {
        this(login, password, null, null, null);
    }
    public User(String login, String password, String token, String room, String creature) {
        this.login = login;
        this.password = password;

        this.token = token;
        this.room = room;
        this.creature = creature;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public void setToken(String token) {
        this.token = room;
    }
    public String getToken() {
        return this.token;
    }



    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCreature() {
        return creature;
    }

    public void setCreature(String creature) {
        this.creature = creature;
    }
}
