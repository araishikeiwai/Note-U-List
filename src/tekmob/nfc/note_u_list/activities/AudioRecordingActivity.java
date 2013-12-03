package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.helpers.DBAdapter;
import tekmob.nfc.note_u_list.helpers.NoteUListHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tekmob.nfc.note_u_list.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class AudioRecordingActivity extends Activity {
	private String mData;
	protected static final String TAG = "Note-U-List! Recording";
	private static MediaRecorder recorder = new MediaRecorder();
	private Activity mActivity = this;
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3ga";
	private static final String AUDIO_RECORDER_FOLDER = "Note-U-List!";
	File file;
	String fileName;
	ViewNoteActivity vn = new ViewNoteActivity();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_rec);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#3BB3C2")));
		setButtonHandlers();
		enableButtons(false);
		pref = getSharedPreferences("pref", MODE_PRIVATE);
		editor = getSharedPreferences("pref", MODE_PRIVATE).edit();
	}

	private void setButtonHandlers() {
		(findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		(findViewById(R.id.btnStop)).setOnClickListener(btnClick);
		(findViewById(R.id.btnPause)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		(findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnStop, isRecording);
		enableButton(R.id.btnPause, isRecording);
	}

	private String getFilename() {
		file = new File(Environment.getExternalStorageDirectory(),
				"Note-U-List!");
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmm")
				.format(new Date());
		String mediaName = file.getPath() + File.separator + timeStamp + "_";
		if (!file.exists()) {
			file.mkdirs();
		}
		return (mediaName);
	}

	private void startRecording() {
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(getFilename());
		fileName = getFilename();
		recorder.setOnErrorListener(errorListener);
		recorder.setOnInfoListener(infoListener);
		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (null != recorder) {
			try {
				recorder.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			recorder.reset();
			recorder.release();
			onRecordTaken(recorder);
		}
	}

	protected void onPause() {
		super.onPause();
		if (recorder != null) {
			recorder.release();
		}
	}

	public void onRecordTaken(MediaRecorder recorder) {

		setContentView(R.layout.note_taken);
		recorder.setOutputFile(getFilename());
		// ImageView imageView = new ImageView(mActivity);
		// imageView.setImageBitmap(rotatedBitmap);

		// ((FrameLayout) findViewById(R.id.result)).addView(imageView);

		ImageView saveButton, discardButton, shareButton;
		saveButton = (ImageView) findViewById(R.id.button_camera_captured_save);
		discardButton = (ImageView) findViewById(R.id.button_camera_captured_discard);
		shareButton = (ImageView) findViewById(R.id.button_camera_captured_share);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent result = new Intent(mActivity.getApplicationContext(),
						ResultActivity.class);
				startActivityForResult(result, ResultActivity.GET_TITLE_TAG);
			}
		});

		discardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO when migrating to API 4.0 and above, change to
				// recreate() method!
				new AlertDialog.Builder(mActivity)
						.setTitle("Discard confirmation")
						.setMessage(
								"Do you really want to discard this note?\nThis cannot be undone!")
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setPositiveButton(android.R.string.yes,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Toast.makeText(mActivity,
												"Note discarded!",
												Toast.LENGTH_SHORT).show();
										Intent intent = getIntent();
										finish();
										startActivity(intent);
									}
								}).setNegativeButton(android.R.string.no, null)
						.show();
			}
		});

		shareButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO implement!
				Toast.makeText(mActivity, "NOT YET IMPLEMENTED",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
		@Override
		public void onError(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
		}
	};

	private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
		@Override
		public void onInfo(MediaRecorder mr, int what, int extra) {
			Toast.makeText(AudioRecordingActivity.this,
					"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
					.show();
		}
	};

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStart: {
				Toast.makeText(AudioRecordingActivity.this, "Start Recording",
						Toast.LENGTH_SHORT).show();
				enableButtons(true);
				startRecording();
				break;
			}
			case R.id.btnStop: {
				Toast.makeText(AudioRecordingActivity.this, "Stop Recording",
						Toast.LENGTH_SHORT).show();
				enableButtons(false);
				stopRecording();
				break;
			}
			case R.id.btnPause: {
				Toast.makeText(AudioRecordingActivity.this, "Pause Recording",
						Toast.LENGTH_SHORT).show();
				enableButtons(false);
				onPause();
				break;
			}
			}
		}
	};

	@Override
	protected void onDestroy() {
		FrameLayout layout = (FrameLayout) findViewById(R.id.result);
		if (layout != null) {
			layout.removeAllViews();
		}
		super.onDestroy();
	}

	private void renameFileOrFolder(File file, String newFileName) {
		// newFileName = file.getName().substring(0, 13) + newFileName + ".txt";
		File newFile = new File(newFileName);
		Log.d("getParentFile", newFileName);
		rename(file, newFile);
		DBAdapter db = new DBAdapter(this);
		db.open();
		Log.d("newFile.getName()", newFile.getName());
		Log.d("newFile.getAbsolutePath()", newFile.getAbsolutePath());
		Log.d("file.getName()", file.getName());
		db.updateBerkas(newFile.getName(), newFile.getAbsolutePath(),
				file.getName());
		// refresh();

	}

	private void rename(File oldFile, File newFile) {
		int toast = 0;
		if (oldFile.renameTo(newFile)) {
			Log.d("Rename", "was successful.");
			toast = R.string.file_renamed;
		} else {
			toast = R.string.error_renaming_file;

		}
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "In onActivityResult()");
		if (data != null) {
			if (requestCode == ResultActivity.GET_TITLE_TAG
					&& resultCode == RESULT_OK) {
				data.putExtra(NoteUListHelper.MEDIA_TYPE,
						NoteUListHelper.MEDIA_TYPE_AUDIO);

				Log.d(TAG, fileName);

				String namaFile = fileName + data.getExtras().get("note_title")
						+ AUDIO_RECORDER_FILE_EXT_3GP;
				Log.d("recorder", namaFile);
				File old = new File(fileName);
				NoteUListHelper.save_audio(mActivity, data, old);
				Log.d("filename", old.getName());
				renameFileOrFolder(old, namaFile);
				recorder = null;
			}
			finish();
		}
	}
}
