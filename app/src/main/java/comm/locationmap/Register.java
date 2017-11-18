package comm.locationmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import comm.model.HttpConnection;

public class Register extends AppCompatActivity {

    EditText username;
    Button registerButton;

    HttpConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        registerButton = (Button) findViewById(R.id.register_user);

        conn = new HttpConnection("https://vegetarian-balls.000webhostapp.com/usuarios.php");

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadData().execute();
            }
        });
    }

    private class LoadData extends AsyncTask<Void, Void, Void> {

        private String user = username.getText().toString();
        private String jsonUser;

        private Integer idUser;

        @Override
        protected void onPreExecute() {
            registerButton
                    .setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {


            Map<String, String> map = new HashMap<String, String>();

            map.put("username", user);
            try {
                conn.sendPostMethodRequest(map);
            } catch (IOException e) {
                e.printStackTrace();
            }

            idUser = Integer.parseInt(conn.getResponse());

            try {
                map.put("id_user", idUser.toString());
                jsonUser = conn.sendGetMethodRequest(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            SharedPreferences sharePref = getApplicationContext().getSharedPreferences("user",
                    getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor edit =  sharePref.edit();
            edit.putInt("id_user", idUser);
            edit.commit();

            //Toast.makeText(getApplicationContext(), jsonUser, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("jsonUser", jsonUser);
            startActivity(intent);
        }

    }
}
