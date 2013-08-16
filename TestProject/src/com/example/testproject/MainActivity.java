package com.example.testproject;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private TextView textView1;
	private TextView textView2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button b = (Button)findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new BitmapLoadAsync().execute();
			}
		});
		
		textView1 = (TextView)findViewById(R.id.textView2);
		textView2 = (TextView)findViewById(R.id.textView3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class BitmapLoadAsync extends AsyncTask<Void, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				return LentaHttpImageDownloader.downloadBitmap("http://icdn.lenta.ru/images/2013/08/16/17/20130816175225450/pic_38b8fb0a60dc6d3ddbc2220208318f38.jpg");
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				
				textView1.setText(String.valueOf(result.getScaledHeight(160)));
				textView2.setText(String.valueOf(result.getScaledWidth(160)));
			}
		}
	}
}
