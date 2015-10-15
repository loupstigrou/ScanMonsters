package prog_mobile.uqac.com.scanmonsters;

import java.io.Serializable;

/**
 * Created by Major on 14/10/2015.
 */
public class User implements Serializable {

    private String login;
    private String password;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }
}
