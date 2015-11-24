package prog_mobile.uqac.com.scanmonsters.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import prog_mobile.uqac.com.scanmonsters.MainActivity;

/**
 * Created by Major on 16/11/2015.
 */

/**
 * Classe qui va gérer les sesssions utilisateurs, afin de pouvoir récupérer les informations
 * de l'utilsateur au sein de toute les activités
 */
public class SessionManager {

    // Shared preferences
    SharedPreferences pref;

    // Editor to edit Shared Preferences
    Editor editor;

    // Context
    Context context;

    // Shared preferences mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "ScanMonsterPref";

    /**
     * All Shared preferences keys
     */
    // Key for boolean
    private static final String IS_LOGIN = "IsLoggedIn";
    // User login (make variable public to access from outside)
    public static final String KEY_LOGIN = "login";
    // User password
    public static final String KEY_PASSWORD = "password";
    // User token
    public static final String KEY_TOKEN = "token";

    // Room objective
    public static final String KEY_ROOM = "room";
    // Creature objective
    public static final String KEY_CREATURE = "creature";

    /**
     * Constructor
     * @param context
     */
    public SessionManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = pref.edit();
    }

    /**
     * Create Log In session
     * @param user
     */
    public void createLoginSession(User user) {
        this.editor.putBoolean(IS_LOGIN, true);
        this.editor.putString(KEY_LOGIN, user.getLogin());
        this.editor.putString(KEY_PASSWORD, user.getPassword());
        this.editor.putString(KEY_TOKEN, user.getToken());
        this.editor.putString(KEY_ROOM, user.getRoom());
        this.editor.putString(KEY_CREATURE, user.getCreature());
        this.editor.commit();
    }

    /**
     * Check user Log In status
     * If user is not logged in, he will return to the Main Activity
     */
    public void checkLogin() {
        if (!this.isLoggedIn()) {
            Intent intent = new Intent(this.context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(intent);
        }
    }

    /**
     * Check if user is Logged In
     */
    public boolean isLoggedIn() {
        return this.pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Return stored session data
     */
    public User getUser() {
        return new User(pref.getString(KEY_LOGIN, null), pref.getString(KEY_PASSWORD, null), pref.getString(KEY_TOKEN, null), pref.getString(KEY_ROOM, null), pref.getString(KEY_CREATURE, null));
    }

    /**
     * Clear session details and return user to Main Activity
     */
    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();

        Intent intent = new Intent(this.context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(intent);
    }

    public void setObjective(String roomId, String creatureId) {
        User user = getUser();
        user.setRoom(roomId);
        user.setCreature(creatureId);
        createLoginSession(user);
    }
}
