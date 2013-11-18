package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView mNoteButton, mCameraButton, mViewNoteButton, mVoiceRecButton,
			mShareButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO when migrating to API lv 14, change to grid layout
		setContentView(R.layout.activity_main);

		mNoteButton = (ImageView) findViewById(R.id.noteButton);
		mNoteButton.setOnClickListener(this);
		mCameraButton = (ImageView) findViewById(R.id.cameraButton);
		mCameraButton.setOnClickListener(this);
		mViewNoteButton = (ImageView) findViewById(R.id.viewNoteButton);
		mViewNoteButton.setOnClickListener(this);
		mVoiceRecButton = (ImageView) findViewById(R.id.voiceRecButton);
		mVoiceRecButton.setOnClickListener(this);
		mShareButton = (ImageView) findViewById(R.id.shareButton);
		mShareButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		Intent next = null;
		switch (v.getId()) {
		case R.id.noteButton:
			next = new Intent(getApplicationContext(), NoteActivity.class);
			break;
		case R.id.cameraButton:
			next = new Intent(getApplicationContext(), CameraActivity.class);
			break;
		case R.id.viewNoteButton:
			next = new Intent(getApplicationContext(), ViewNoteActivity.class);
			break;
		case R.id.voiceRecButton:
			next = new Intent(getApplicationContext(), AudioRecordingActivity.class);
			break;
		case R.id.shareButton:
			next = new Intent(getApplicationContext(), ShareActivity.class);
			break;
		}
		startActivity(next);
	}

}
