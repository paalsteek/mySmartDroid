package de.mysmartgrid.mysmartdroid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.SortedMap;
import java.lang.Integer;

class MsgGraph
{
	public TreeMap<Integer, Double> map;
	public Double avg;
	public Paint[] paint;

	public MsgGraph() {
		map = new TreeMap<Integer, Double>();
		avg = 0.0;
		paint = new Paint[2];
		paint[0] = new Paint();
		paint[1] = new Paint();
	}
}

public class MsgGraphView extends View
{
	final static int SENSORS = 3;

	//private ArrayList< TreeMap<Integer, Double> > mMap;
	private ArrayList< MsgGraph > graphs;
	private int h, w;
	private double max;

	public MsgGraphView(Context context) {
		super(context);
		graphs = new ArrayList< MsgGraph >();
		for ( int i = 0; i < SENSORS; i++ )
			graphs.add(new MsgGraph());
		graphs.get(0).paint[0].setARGB(255, 255, 0, 0);
		graphs.get(0).paint[1].setARGB(255, 55, 0, 0);
		graphs.get(1).paint[0].setARGB(255, 0, 255, 0);
		graphs.get(1).paint[1].setARGB(255, 0, 55, 0);
		graphs.get(2).paint[0].setARGB(255, 0, 0, 255);
		graphs.get(2).paint[1].setARGB(255, 0, 0, 55);
		//graphs.get(3).paint.setARGB(255, 255, 255, 0);
		/*mMap = new ArrayList< TreeMap<Integer, Double> >();
		for ( int i = 0; i < ( SENSORS + 1 ); i++ ) 
			mMap.add(new TreeMap<Integer, Double>());*/
	}

	public MsgGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		graphs = new ArrayList< MsgGraph >();
		for ( int i = 0; i < SENSORS; i++ )
			graphs.add(new MsgGraph());
		graphs.get(0).paint[0].setARGB(255, 255, 0, 0);
		graphs.get(0).paint[1].setARGB(255, 55, 0, 0);
		graphs.get(1).paint[0].setARGB(255, 0, 255, 0);
		graphs.get(1).paint[1].setARGB(255, 0, 55, 0);
		graphs.get(2).paint[0].setARGB(255, 0, 0, 255);
		graphs.get(2).paint[1].setARGB(255, 0, 0, 55);
		//graphs.get(3).paint.setARGB(255, 255, 255, 0);
		/*mMap = new ArrayList< TreeMap<Integer, Double> >();
		for ( int i = 0; i < ( SENSORS + 1 ); i++ ) 
			mMap.add(new TreeMap<Integer, Double>());*/
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		/*Paint[] mPaint = new Paint[4];
		mPaint[0] = new Paint();
		mPaint[0].setARGB(255, 255, 0, 0);
		mPaint[1] = new Paint();
		mPaint[1].setARGB(255, 0, 255, 0);
		mPaint[2] = new Paint();
		mPaint[2].setARGB(255, 0, 0, 255);
		mPaint[3] = new Paint();
		mPaint[3].setARGB(255, 255, 255, 0);*/
		//canvas.drawLine(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), mPaint);
		int[] order = new int[3];
		if ( graphs.get(0).avg >= graphs.get(1).avg && graphs.get(0).avg >= graphs.get(2).avg )
		{
			order[0] = 0;
			if ( graphs.get(1).avg >= graphs.get(2).avg )
			{
				order[1] = 1;
				order[2] = 2;
			} else {
				order[1] = 2;
				order[2] = 1;
			}
		}
		else if ( graphs.get(1).avg >= graphs.get(0).avg && graphs.get(1).avg >= graphs.get(2).avg )
		{
			order[0] = 1;
			if ( graphs.get(0).avg >= graphs.get(2).avg )
			{
				order[1] = 0;
				order[2] = 2;
			} else {
				order[1] = 2;
				order[2] = 0;
			}
		}
		else if ( graphs.get(2).avg >= graphs.get(0).avg && graphs.get(2).avg >= graphs.get(1).avg )
		{
			order[0] = 2;
			if ( graphs.get(0).avg >= graphs.get(1).avg )
			{
				order[1] = 0;
				order[2] = 1;
			} else {
				order[1] = 1;
				order[2] = 0;
			}
		}
		Log.i("MsgGraphView", graphs.get(order[0]).avg + " >= " + graphs.get(order[1]).avg + " >= " + graphs.get(order[2]).avg);

		float maxval = h/((float) (max+(0.1*max)));
		for( int i = 0; i < SENSORS; i++ )
		{
			Log.i("MsgGraphView", "index: " + i + " of " + graphs.size());
			if ( graphs.get(order[i]).map.size() > 0 )
			{
				TreeMap<Integer, Double> mMap = graphs.get(order[i]).map;
				int xStep = w/(mMap.size() - 1);
				int xLast = mMap.firstKey();
				Double yLast = mMap.get(xLast);
				for( Map.Entry<Integer, Double> entry: mMap.entrySet() )
				{
					graphs.get(order[i]).paint[0].setAlpha(255);
					float x1 = new Integer((xLast - mMap.firstKey()) * xStep).floatValue();
					float y1 = new Double(h - ( yLast * maxval ) ).floatValue();
					float x2 = new Integer((entry.getKey() - mMap.firstKey()) * xStep).floatValue();
					float y2 = new Double(h - ( entry.getValue() * maxval )).floatValue();
					//graphs.get(order[i]).paint.setAlpha(55);
					Path p = new Path();
					p.moveTo(x1, h);
					p.lineTo(x1, y1);
					p.lineTo(x2, y2);
					p.lineTo(x2, h);
					p.close();
					canvas.drawPath(p, graphs.get(order[i]).paint[1]);
					canvas.drawLine(x1, y1, x2, y2, graphs.get(order[i]).paint[0]);
					xLast = entry.getKey();
					yLast = entry.getValue();
				}
				graphs.get(order[i]).paint[0].setAlpha(55);
				canvas.drawLine((float) 0, (float) (h - ( graphs.get(order[i]).avg * maxval )), (float) w, (float) (h - ( graphs.get(order[i]).avg * maxval )), graphs.get(order[i]).paint[0]);
			}
		}
		//canvas.drawLine(0.0f, 0.0f, 0.0f + w, 0.0f + h, mPaint);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		this.h = h - h/10;
		this.w = w;
		invalidate();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public int updateValues(int index, TreeMap<Integer, Double> m)
	{
		int count = 0;
		for( Map.Entry<Integer, Double> entry: m.entrySet() )
		{
			if ( ( ! graphs.get(index).map.containsKey( entry.getKey() ) ) || ( graphs.get(index).map.get(entry.getKey()) != entry.getValue() ) )
			{
				Log.i("MySmartDroid/graph", "Value " + entry.getKey() + " updated!");
				graphs.get(index).map.put( entry.getKey(), entry.getValue() );
				if ( entry.getValue() > max )
					max = entry.getValue();
				count += 1;
			}
		}
		SortedMap<Integer, Double> current = graphs.get(index).map.tailMap(m.lastKey() - 300);
		Double mean = 0.0;
		for( Double value: current.values() )
		{
			mean += value;
		}
		mean = mean / current.size();
		graphs.get(index).avg = mean;
		Log.i("MySmartDroid/avg", "Average value of " + index + " is " + mean);
		invalidate();
		return count;
	}
}
