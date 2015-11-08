package com.example.gmapsapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gmapsapp.db.DataSource;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class MenuActivity extends FragmentActivity implements OnClickListener,
		OnItemSelectedListener {

	EditText RecordName, EnterDescription;
	Spinner type;
	int count;
	String check_In_Location = null;
	String check_In_Type = null;
	String lat, lng;

	// Browse Image
	private static final int REQUEST_ID = 1;
	LinearLayout ImageAttachment;
	int height, width;
	List<String> imagePaths;
	int lastCount = 0;
	// Audio
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private String outputFile = null;
	private long startTime = 0L;
	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private TextView timerValue;
	String Recfolder;
	Boolean isRecord;
	DatePicker datePicker1;
	// DB
	DataSource ds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		datePicker1 = (DatePicker) findViewById(R.id.datePicker1);

		ds = new DataSource(this);
		ds.open();
		count = ds.getCount() + 1;
		imagePaths = new ArrayList<String>();

		LoadMediaPlayer();
		RecordName = (EditText) findViewById(R.id.RecordName);
		EnterDescription = (EditText) findViewById(R.id.EnterDescription);
		timerValue = (TextView) findViewById(R.id.timerValue);
		ImageAttachment = (LinearLayout) findViewById(R.id.ImageAttachment);
		type = (Spinner) findViewById(R.id.SpinnerType);
		List<String> types = new ArrayList<String>();
		types.add("General");
		types.add("Business");
		types.add("Education");
		types.add("Health");
		types.add("Tour");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, types);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type.setAdapter(dataAdapter);

		String fontPath = "fonts/gooddog.otf";
		Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
		EnterDescription.setTypeface(tf);
		Bundle b = getIntent().getExtras();
		check_In_Location = b.getString("Location");
		lat = b.getString("Lat");
		lng = b.getString("Lng");
		TextView textViewLocation = (TextView) findViewById(R.id.textViewLocation);
		textViewLocation.setText(check_In_Location + " - " );
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		lastCount = ds.getImageCount();
		findViewById(R.id.buttonAttachImage).setOnClickListener(this);
		findViewById(R.id.buttonRecord).setOnClickListener(this);
		findViewById(R.id.buttonRecordStop).setOnClickListener(this);
		findViewById(R.id.buttonRecordPlay).setOnClickListener(this);
		findViewById(R.id.buttonRecordDelete).setOnClickListener(this);
		findViewById(R.id.buttonSave).setOnClickListener(this);
		type.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonAttachImage:

			if (RecordName.getText().toString().length() > 0) {
				AttachImage();
			} else {
				Toast.makeText(getApplicationContext(), "Please Enter Title",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.buttonRecord:
			if (RecordName.getText().toString().length() > 0) {
				Start();
			} else {
				Toast.makeText(getApplicationContext(), "Please Enter Title",
						Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.buttonRecordStop:
			Stop();
			break;
		case R.id.buttonRecordPlay:
			Play();
			break;
		case R.id.buttonRecordDelete:
			Delete();
			break;
		case R.id.buttonSave:
			Save();

			break;
		}
	}

	public void Save() {
		if (RecordName.getText().toString().length() > 0) {
			String title = RecordName.getText().toString();
			String location = check_In_Location;
			if (EnterDescription.getText().toString().length() > 0) {
				String description = EnterDescription.getText().toString();
				String record = null;
				if (isRecord)
					record = outputFile;
				else
					record = "";
				String lat = this.lat;
				String lng = this.lng;
				String type = check_In_Type;
				long dateL = datePicker1.getCalendarView().getDate();
				Date date = new Date(dateL);
				SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
				String dateText = df2.format(date);
				
				ds.Enter_Check_In(title, description, location, record, lat,
						lng, type, dateText);

				String path = "";
				for (int i = 0; i < imagePaths.size(); i++) {
					ds.Enter_Image_Record(RecordName.getText().toString(),
							imagePaths.get(i));
					path += imagePaths.get(i);
				}
				startActivity(new Intent(getApplicationContext(),
						MapMenuActivity.class));
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						"Please Enter Description", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), "Please Enter Title",
					Toast.LENGTH_LONG).show();
		}
	}

	public void LoadMediaPlayer() {
		String sdCard = Environment.getExternalStorageDirectory().toString();
		Recfolder = sdCard + "/Myrecs1";
		File targetFolder = new File(Recfolder);
		if (!targetFolder.exists()) {
			targetFolder.mkdir();
		}
		isRecord = false;
		myRecorder = new MediaRecorder();
		myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
	}

	public void Start() {
		outputFile = Recfolder + "/" + RecordName.getText().toString() + "_"
				+ count + ".3gpp";
		myRecorder.setOutputFile(outputFile);
		findViewById(R.id.buttonRecordStop).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonRecord).setVisibility(View.GONE);
		startTime = SystemClock.uptimeMillis();
		customHandler.postDelayed(updateTimerThread, 0);
		try {
			myRecorder.prepare();
			myRecorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toast.makeText(getApplicationContext(), "Start recording...",
				Toast.LENGTH_SHORT).show();
	}

	public void Stop() {
		customHandler.removeCallbacks(updateTimerThread);
		findViewById(R.id.buttonRecordPlay).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonRecordDelete).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonRecordStop).setVisibility(View.GONE);
		timerValue.setVisibility(View.GONE);
		try {
			myRecorder.stop();
			myRecorder.release();
			myRecorder = null;
			Toast.makeText(getApplicationContext(), "Stop recording...",
					Toast.LENGTH_SHORT).show();
			isRecord = true;
			// ds.EnterRecord(Recfolder + "/" + RecordName.getText().toString()
			// + ".3gpp", Recfolder);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

	}

	public void Play() {
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(outputFile);
			myPlayer.prepare();
			myPlayer.start();
			Toast.makeText(getApplicationContext(),
					"Start play the recording...", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Delete() {
		LoadMediaPlayer();
		timerValue.setText("Click Start to record");
		timerValue.setVisibility(View.VISIBLE);
		findViewById(R.id.buttonRecord).setVisibility(View.VISIBLE);
		findViewById(R.id.buttonRecordPlay).setVisibility(View.GONE);
		findViewById(R.id.buttonRecordDelete).setVisibility(View.GONE);
		isRecord = false;
	}

	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;
			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;
			timerValue.setText("" + mins + ":" + String.format("%02d", secs));
			customHandler.postDelayed(this, 0);
		}

	};

	public void AttachImage() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, REQUEST_ID);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
			new LoadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					data);
		}
	}

	public class LoadImage extends AsyncTask<Intent, Void, Void> {
		InputStream stream = null;
		ImageView iv;
		Bitmap original;
		String ImagePath;

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			iv.setImageBitmap(Bitmap.createScaledBitmap(original, height / 3,
					height / 3, true));
			ImageAttachment.addView(iv);

			imagePaths.add(ImagePath);
			ImageAttachment.getLayoutParams().height = height / 3;
			ImageAttachment.requestLayout();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			iv = new ImageView(getApplicationContext());
			lastCount++;
		}

		@Override
		protected Void doInBackground(Intent... params) {
			Intent data = params[0];
			try {
				stream = getContentResolver().openInputStream(data.getData());
				original = BitmapFactory.decodeStream(stream);
				String sdCard = Environment.getExternalStorageDirectory()
						.toString();
				String folder = sdCard + "/MyPics2";
				File targetFolder = new File(folder);
				if (!targetFolder.exists()) {
					targetFolder.mkdir();
				}
				ImagePath = targetFolder + "/ "
						+ RecordName.getText().toString() + "_" + lastCount
						+ ".jpg";
				File targetLocation = new File(ImagePath);
				FileOutputStream fos = new FileOutputStream(targetLocation);
				original.compress(Bitmap.CompressFormat.PNG, 90, fos);

				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		check_In_Type = parent.getItemAtPosition(position).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		check_In_Type = parent.getItemAtPosition(0).toString();
	}
}
