package prog_mobile.uqac.com.scanmonsters;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Pattern;

public class InscriptionActivity extends AppCompatActivity {

    private EditText firstNameView;
    private EditText lastNameView;
    private EditText emailView;
    private EditText passwordView;
    private Button inscriptionButton;

    private View progressView;
    private View loginFormView;

    // TODO: implémenter les regex
    // Les expressions régulières que doivent matcher les différents champs en entrées
    private static final Pattern firstNamePattern = Pattern.compile("^[a-zàáâäçèéêëìíîïñòóôöùúûü]+[ \\-']?[[a-zàáâäçèéêëìíîïñòóôöùúûü]+[ \\-']?]*[a-zàáâäçèéêëìíîïñòóôöùúûü]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern lastNamePattern = Pattern.compile("^([a-zàáâäçèéêëìíîïñòóôöùúûü]){1,19}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern emailPattern = Pattern.compile("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[A-Z]{2,4}", Pattern.CASE_INSENSITIVE);
    private static final Pattern passwordPattern = Pattern.compile("[a-z0-9]{2,19}", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        this.firstNameView = (EditText) findViewById(R.id.inscription_prenom);
        this.lastNameView = (EditText) findViewById(R.id.inscription_nom);
        this.emailView = (EditText) findViewById(R.id.inscription_email);
        this.passwordView = (EditText) findViewById(R.id.inscription_password);

        this.inscriptionButton = (Button) findViewById(R.id.signin_button);
        this.inscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedInscription();
            }
        });
    }

    private void proceedInscription() {
        // TODO: Vérifier la validité des informations données et réaliser l'inscription
        String firstName = this.firstNameView.getText().toString();
        String lastName = this.lastNameView.getText().toString();
        String email = this.emailView.getText().toString();
        String password = this.passwordView.getText().toString();
    }

    private boolean isFirstNameValid(CharSequence firstName) {
        return this.firstNamePattern.matcher(firstName).matches() && firstName.length() < 20;
    }

    private boolean isLastNameValid(CharSequence lastName) {
        return this.lastNamePattern.matcher(lastName).matches() && lastName.length() < 20;
    }

    private boolean isEmailValid(CharSequence email) {
        return this.emailPattern.matcher(email).matches();
    }

    private boolean isPasswordValid(CharSequence password) {
        return this.passwordPattern.matcher(password).matches() && password.length() < 20;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
