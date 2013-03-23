package ch.opendata.partibudget_remote;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

import java.net.MalformedURLException;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;

public class MainActivity extends SherlockListActivity implements SensorEventListener {
	private final static int	TIME_BETWEEN		= 1000000000;
	private SocketIO			socket;
	private SensorManager		sensorManager;
	private Sensor				accelerometer;
	private float[]				accelerometerVector	= new float[3];
	private long				previousTime;
	private String[]			cantonNames = new String[] { "Argovie", "Appenzell Rhodes intérieures", "Appenzell Rhodes extérieures", "Berne", "Bâle Campagne", "Bâle Ville", "Fribourg", "Genève", "Glaris", "Grisons", "Jura", "Lucerne", "Neuchâtel", "Nidwald", "Obwald", "Saint-Gall", "Schaffhouse", "Soleure", "Schwyz", "Thurgovie", "Tessin", "Uri", "Vaud", "Valais", "Zug", "Zürich" };
	private String[]			cantonShortnames = new String[] { "AG", "AI", "AR", "BE", "BL", "BS", "FR", "GE", "GL", "GR", "JU", "LU", "NE", "NW", "OW", "SG", "SH", "SO", "SZ", "TG", "TI", "UR", "VD", "VS", "ZG", "ZH" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		loadList();

		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				socket.send(cantonShortnames[position]);
			}
		});
		
		connect();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		super.onResume();
	}

	@Override
	protected void onPause() {
		sensorManager.unregisterListener(this, accelerometer);
		super.onPause();
	}

	private void connect() {
		try {
			socket = new SocketIO("http://10.1.0.252/");
		}
		catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		socket.connect(new IOCallback() {
			@Override
			public void onMessage(JSONObject json, IOAcknowledge ack) {
				try {
					System.out.println("Server said:" + json.toString(2));
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onMessage(String data, IOAcknowledge ack) {
				System.out.println("Server said: " + data);
			}

			@Override
			public void onError(SocketIOException socketIOException) {
				System.out.println("an Error occured");
				socketIOException.printStackTrace();
			}

			@Override
			public void onDisconnect() {
				System.out.println("Connection terminated.");
			}

			@Override
			public void onConnect() {
				System.out.println("Connection established");
			}

			@Override
			public void on(String event, IOAcknowledge ack, Object... args) {
				System.out.println("Server triggered event '" + event + "'");
			}
		});
	}
	
	private void loadList() {
		BitmapDrawable[] cantonsIconList = new BitmapDrawable[cantonNames.length];

		for (int i = 0 ; i < cantonShortnames.length ; i++) {
			try {
				int cantonDrawableResourceId = getResources().getIdentifier("canton_" + cantonShortnames[i].toLowerCase(Locale.getDefault()), "drawable", getPackageName());
				cantonsIconList[i] = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), cantonDrawableResourceId));
			}
			catch (Exception ex) { }
		}

		setListAdapter(new IconListAdapter(this, R.id.listitem_canton_name, cantonNames, cantonsIconList));
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	@Override
	public void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			//Log.e("TEST", "VALUES:" + accelerometerVector[0] + ";" + accelerometerVector[1] +";" + accelerometerVector[2]);

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				accelerometerVector[0] = event.values[0];
				accelerometerVector[1] = event.values[1];
				accelerometerVector[2] = event.values[2];
							
				if (event.timestamp - previousTime > TIME_BETWEEN) {
					previousTime = event.timestamp;

					if (accelerometerVector[1] > 8)
						socket.send("UP");
					else if (accelerometerVector[1] < -5)
						socket.send("DOWN");
					
					if (accelerometerVector[0] > 8)
						socket.send("LEFT");
					else if (accelerometerVector[0] < -8)
						socket.send("RIGHT");
				}
			}
		}
	}
}