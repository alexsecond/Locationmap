package comm.locationmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import comm.model.LocationMap;
import comm.model.OnEventListener;
import comm.model.WebSocketConection;

public class TestActivity extends AppCompatActivity implements OnEventListener{

    TextView txtView1;
    TextView txtView2;
    TextView txtView3;
    TextView txtView4;

    WebSocketConection webSocketConection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Toast.makeText(this, "inicializando", Toast.LENGTH_LONG).show();

        txtView1 = (TextView) findViewById(R.id.textView1);
        txtView2 = (TextView) findViewById(R.id.textView2);
        txtView3 = (TextView) findViewById(R.id.textView3);
        txtView4 = (TextView) findViewById(R.id.textView4);

        webSocketConection = new WebSocketConection(this);
    }

    @Override
    public void onReceiveLocations(LocationMap[] locations) {
        Toast.makeText(this, "Se han recibido nuevas localizaciones", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReceiveMessages(Object message) {
        Toast.makeText(this, "Se ha recibido mensaje", Toast.LENGTH_LONG).show();
    }
}
