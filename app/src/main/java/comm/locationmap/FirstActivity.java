package comm.locationmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import comm.model.HttpConnection;

public class FirstActivity extends AppCompatActivity {

    private HttpConnection conn;

    private SharedPreferences sharePref;

    private TextView state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_first);

        state = (TextView) findViewById(R.id.current_state);

        sharePref = this.getSharedPreferences("user",
                this.MODE_PRIVATE);
        Integer idUser = sharePref.getInt("id_user", -1);



        if (idUser == -1) {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        } else {
            LoadData data = new LoadData();
            data.execute(idUser);
            Toast.makeText(this, idUser.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    private class LoadData extends AsyncTask<Integer, Void, Void> {

        private String jsonUser;

        @Override
        protected void onPreExecute() {
            state.setText("Cargando");
        }

        @Override
        protected Void doInBackground(Integer... params) {

            conn = new HttpConnection("https://vegetarian-balls.000webhostapp.com/usuarios.php");

            Map<String, String> map = new HashMap<String, String>();
            map.put("id_user", params[0].toString());
            try {
                jsonUser = conn.sendGetMethodRequest(map);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            state.setText("Completado");
            Toast.makeText(getApplicationContext(), jsonUser, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("jsonUser", jsonUser);
            startActivity(intent);
        }
    }
}
