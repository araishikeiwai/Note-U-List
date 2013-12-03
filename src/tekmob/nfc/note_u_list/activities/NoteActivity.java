package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.NoteUListHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class NoteActivity extends Activity {

	private static final String TAG = "Note-U-List! TextNote";
	private EditText mNoteTitle, mNoteContent;
	private String mData;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_taken);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mActivity = this;

		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View theInflatedView = inflater.inflate(R.layout.activity_note, null);

		((FrameLayout) findViewById(R.id.result)).addView(theInflatedView);

		mNoteTitle = (EditText) findViewById(R.id.noteTitle);
		mNoteContent = (EditText) findViewById(R.id.noteContent);

		ImageView saveButton, discardButton, shareButton;

		saveButton = (ImageView) findViewById(R.id.button_camera_captured_save);
		discardButton = (ImageView) findViewById(R.id.button_camera_captured_discard);
		shareButton = (ImageView) findViewById(R.id.button_camera_captured_share);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mNoteTitle.getText().toString().equals("")) {
					Log.d(TAG, "Content invalid");
					Toast t = Toast.makeText(NoteActivity.this,
							"Insert title!", Toast.LENGTH_SHORT);
					t.show();
				} else {
					mData = mNoteContent.getText().toString();
					Intent result = new Intent(mActivity
							.getApplicationContext(), ResultActivity.class);
					result.putExtra(ResultActivity.NOTE_TITLE, mNoteTitle
							.getText().toString());
					startActivityForResult(result, ResultActivity.GET_TITLE_TAG);
				}
			}

		});

		discardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
										finish();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "In onActivityResult()");
		if (data != null) {
			if (requestCode == ResultActivity.GET_TITLE_TAG
					&& resultCode == RESULT_OK) {
				data.putExtra(NoteUListHelper.MEDIA_TYPE,
						NoteUListHelper.MEDIA_TYPE_TEXT);
				NoteUListHelper.save(mActivity, data, mData.getBytes());
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.note, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
