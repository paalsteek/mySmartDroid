package de.mysmartgrid.mysmartdroid;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.util.Log;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONArray;

import android.os.Handler;

import java.lang.Exception;

public class MySmartDroid extends Activity
{
	private Handler mHandler = new Handler();
	private MsgGraphView mView;
	private Runnable mUpdater = new Runnable() {
		public void run() {
			String[] sensors = new String[3];
			sensors[0] = "4f802808dd39070a8f1f10ab64f63d93";
			sensors[1] = "404b37f9757e7efbe70e05f8f85e5523";
			sensors[2] = "66e08d4b9faca79599661251b2e78390";
			for ( int i = 0; i < 3; i++ )
			{
				try {
					fluksoApi api = new fluksoApi("http://192.168.178.54:8080/",
						sensors[i],
						"0a0fa85454554ae6a902d3129fc03175");
					String values = api.request("minute", "watt");
					Log.i("MySmartDroid", values);
					TreeMap<Integer, Double> map = parseJSON(values);
					int c = mView.updateValues(i, map);
					Log.i("MySmartDroid", c + " values updated!");
				} catch ( Exception e )
				{
					Log.d("MySmartDroid", "Uuups!", e);
				}
			}
			/*Map<Integer, Double> map = new HashMap<Integer, Double>();
			map.put(1, 0.0);
			map.put(2, 10.0);
			map.put(3, 20.0);
			map.put(4, 20.0);
			map.put(5, 0.0);
			map.put(-1, 0.0);
			map.put(0, 10.0);
			map.put(3, 40.0);
			map.put(4, 45.0);
			map.put(5, 15.0);*/
			mHandler.postDelayed(mUpdater, 1000);
		}
	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				final Button button = (Button) findViewById(R.id.my_button);
				mView = (MsgGraphView) findViewById(R.id.graph);
				button.setOnClickListener(
					new View.OnClickListener() {
						public void onClick(View v) {
							mHandler.removeCallbacks(mUpdater);
							mHandler.post(mUpdater);
						}
					}
				);
    }

		TreeMap<Integer, Double> parseJSON(String input)
		{
			TreeMap<Integer, Double> map = new TreeMap<Integer, Double>();
			try {
				JSONArray array = new JSONArray(input);
				for( int x = 0; x < array.length(); x++ )
				{
					JSONArray elem = new JSONArray(array.getString(x));
					int i = elem.getInt(0);
					double d = elem.getDouble(1);
					map.put(i, d);
				}
			} catch ( Exception e )
			{
				Log.e("MySmartDroid/parseJSON", "Parser failed!", e);
			}

			return map;
		}
}
