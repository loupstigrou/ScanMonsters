package prog_mobile.uqac.com.scanmonsters.services;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import prog_mobile.uqac.com.scanmonsters.InGameActivity;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;


public class BasicService extends AsyncTask<Void, Void, Boolean> {

    protected Context context;
    protected String serverResponse;
    protected SessionManager session;
    protected String requestType;
    protected String urlParameters;
//"&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8")
    public BasicService(Context context, SessionManager session, String requestType, String urlParameters) {
        this.context = context;
        this.serverResponse = "";
        this.session = session;
        this.requestType = requestType;
        this.urlParameters = urlParameters;

        try {
            this.urlParameters =
                    "requestType="+requestType+
                            "&login=" + URLEncoder.encode(session.getUser().getLogin(), "UTF-8") +
                            "&password=" + URLEncoder.encode(session.getUser().getPassword(), "UTF-8")+
                            urlParameters;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* //*/

    @Override
    protected Boolean doInBackground(Void... params) {
        URL url;
        HttpURLConnection connection;
        try {
            url = new URL(InGameActivity.webserviceURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            connection.setFixedLengthStreamingMode(urlParameters.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(urlParameters);
            out.close();

            Scanner inStream = new Scanner(connection.getInputStream());

            while (inStream.hasNextLine())
                this.serverResponse += (inStream.nextLine());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return !this.serverResponse.equals("NOK");
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {


        } else {
            Toast.makeText(context, "Erreur: " + serverResponse, Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    public boolean finished() {
        return getStatus() == Status.FINISHED;
    }

}
