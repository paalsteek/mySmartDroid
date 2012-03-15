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
	public TreeMap<Integer, Double[]> map;
	public Double[] means;
	public Paint[][] paint;

	public MsgGraph() {
		map = new TreeMap<Integer, Double[]>();
		means = new Double[3];
		paint = new Paint[3][2];
		for ( int i = 0; i < 3; i++ ) {
			means[i] = 0.0;
			paint[i][0] = new Paint();
			paint[i][1] = new Paint();
		}
	}
}

public class MsgGraphView extends View
{
	private MsgGraph graph;
	private int h, w;
	private double max;

	public MsgGraphView(Context context) {
		super(context);
		graph = new MsgGraph();
		graph.paint[0][0].setARGB(255, 255, 0, 0);
		graph.paint[0][1].setARGB(255, 55, 0, 0);
		graph.paint[1][0].setARGB(255, 0, 255, 0);
		graph.paint[1][1].setARGB(255, 0, 55, 0);
		graph.paint[2][0].setARGB(255, 0, 0, 255);
		graph.paint[2][1].setARGB(255, 0, 0, 55);
	}

	public MsgGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		graph = new MsgGraph();
		graph.paint[0][0].setARGB(255, 255, 0, 0);
		graph.paint[0][1].setARGB(255, 55, 0, 0);
		graph.paint[1][0].setARGB(255, 0, 255, 0);
		graph.paint[1][1].setARGB(255, 0, 55, 0);
		graph.paint[2][0].setARGB(255, 0, 0, 255);
		graph.paint[2][1].setARGB(255, 0, 0, 55);
	}

	private int[] order(Double[] values)
	{
		int[] order = new int[3];
		double[] tempValues = new double[3];
		for ( int i = 0; i < 3; i++ )
		{
			if ( values[i] == null )
				tempValues[i] = 0.0;
			else
				tempValues[i] = values[i];
		}
		if ( tempValues[0] >= tempValues[1] && tempValues[0] >= tempValues[2] )
		{
			order[0] = 0;
			if ( tempValues[1] >= tempValues[2] )
			{
				order[1] = 1;
				order[2] = 2;
			} else {
				order[1] = 2;
				order[2] = 1;
			}
		}
		else if ( tempValues[1] >= tempValues[0] && tempValues[1] >= tempValues[2] )
		{
			order[0] = 1;
			if ( tempValues[0] >= tempValues[2] )
			{
				order[1] = 0;
				order[2] = 2;
			} else {
				order[1] = 2;
				order[2] = 0;
			}
		}
		else if ( tempValues[2] >= tempValues[0] && tempValues[2] >= tempValues[1] )
		{
			order[0] = 2;
			if ( tempValues[0] >= tempValues[1] )
			{
				order[1] = 0;
				order[2] = 1;
			} else {
				order[1] = 1;
				order[2] = 0;
			}
		}

		return order;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if ( graph.map.size() > 0 ) {
			float maxval = h/((float) (max+(0.1*max)));
			float numval = w/(graph.map.lastKey() - graph.map.firstKey());
			Log.i("MsgGraphView", "numval: " + numval + " (" + graph.map.lastKey() + " - " + graph.map.firstKey() + ")");
			float lastx[] = new float[3];
			float lasty[] = new float[3];

			for ( int i = 0; i < 3; i++ ) {
				canvas.drawLine(0f, (float) (h - (graph.means[i] * maxval)), (float) w,  (float) (h - (graph.means[i] * maxval)),graph.paint[i][1]);
			}
			for ( Map.Entry< Integer, Double[] > entry: graph.map.entrySet() )
			{
				int[] graphOrder = order(entry.getValue());
				float x[] = new float[3];
				float y[] = new float[3];
				for ( int i = 0; i < 3; i++ )
				{
					if ( entry.getValue()[graphOrder[i]] != null ) {
						x[graphOrder[i]] = (entry.getKey() - graph.map.firstKey()) * numval;
						y[graphOrder[i]] = (float) (h - (entry.getValue()[graphOrder[i]] * maxval));
					} else {
						x[graphOrder[i]] = (entry.getKey() - graph.map.firstKey()) * numval;
						y[graphOrder[i]] = lasty[graphOrder[i]];
					}
				}
				for ( int i = 0; i < 3; i++ )
				{
					if ( entry.getKey() > graph.map.firstKey() )
					{
						Path p = new Path();
						if ( i < 2 )
						{
							p.moveTo(lastx[graphOrder[i]], lasty[graphOrder[i]]);
							p.lineTo(x[graphOrder[i]], y[graphOrder[i]]);
							p.lineTo(x[graphOrder[i+1]], y[graphOrder[i+1]]);
							p.lineTo(lastx[graphOrder[i+1]], lasty[graphOrder[i+1]]);
						} else {
							p.moveTo(lastx[graphOrder[i]], lasty[graphOrder[i]]);
							p.lineTo(x[graphOrder[i]], y[graphOrder[i]]);
							p.lineTo(x[graphOrder[i]], h);
							p.lineTo(lastx[graphOrder[i]], h);
						}
						p.close();
						canvas.drawPath(p, graph.paint[graphOrder[i]][1]);
						canvas.drawLine(lastx[graphOrder[i]], lasty[graphOrder[i]], x[graphOrder[i]], y[graphOrder[i]], graph.paint[graphOrder[i]][0]);
					}
					lastx[graphOrder[i]] = x[graphOrder[i]];
					lasty[graphOrder[i]] = y[graphOrder[i]];
				}
			}
		}
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
			if ( ! graph.map.containsKey( entry.getKey() ) ) {
				Double[] value = new Double[3];
				value[index] = entry.getValue();
				graph.map.put( entry.getKey(), value );
				if ( entry.getValue() > max )
					max = entry.getValue();
				count += 1;
				//Log.i("MySmartDroid/graph", "Value " + entry.getKey() + " updated!");
			}
			else if ( graph.map.get(entry.getKey())[index] != entry.getValue() )
			{
				Double[] value = graph.map.get( entry.getKey() );
				value[index] = entry.getValue();
				graph.map.put( entry.getKey(), value );
				if ( entry.getValue() > max )
					max = entry.getValue();
				count += 1;
				//Log.i("MySmartDroid/graph", "Value " + entry.getKey() + " updated!");
			}
		}
		SortedMap<Integer, Double[] > current = graph.map.tailMap(m.lastKey() - 300);
		Double mean = 0.0;
		for( Double[] value: current.values() )
		{
			if ( value[index] != null ) {
				mean += value[index];
			}
		}
		mean = mean / current.size();
		graph.means[index] = mean;
		Log.i("MySmartDroid/avg", "Average value of " + index + " is " + mean);
		invalidate();
		return count;
	}
}
