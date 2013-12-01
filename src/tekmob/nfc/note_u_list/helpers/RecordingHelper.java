package tekmob.nfc.note_u_list.helpers;



import java.io.File;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.activities.AudioRecordingActivity;
import tekmob.nfc.note_u_list.activities.ResultActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class RecordingHelper extends Activity  {
	
	private Activity mActivity = this;
	protected static final String TAG = "Note-U-List! Recording";
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3ga";
	private static final String AUDIO_RECORDER_FOLDER = "Note-U-List!";
	private MediaRecorder recorder = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO when migrating to API lv 14, change to grid layout
		setContentView(R.layout.note_taken);

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
										Intent intent = new Intent(getApplicationContext(), AudioRecordingActivity.class);
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
	
	private String getFilename() {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath, AUDIO_RECORDER_FOLDER);
		if (!file.exists()) {
			file.mkdirs();
		}
		return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_3GP);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "In onActivityResult()");
		if (data != null) {
			if (requestCode == ResultActivity.GET_TITLE_TAG
					&& resultCode == RESULT_OK) {
				data.putExtra(NoteUListHelper.MEDIA_TYPE,
						NoteUListHelper.MEDIA_TYPE_IMAGE);
				//NoteUListHelper.save2(mActivity, data, recorder);
				finish();
			}
		}
	}
}
