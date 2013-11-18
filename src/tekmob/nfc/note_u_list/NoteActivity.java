package tekmob.nfc.note_u_list;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NoteActivity extends Activity {

	private static final String TAG = "Note-U-List! TextNote";
	private EditText mNoteTitle, mNoteContent;
	private Button mSaveButton;
	private String mData;
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		mActivity = this;

		mNoteTitle = (EditText) findViewById(R.id.noteTitle);
		mNoteContent = (EditText) findViewById(R.id.noteContent);
		mSaveButton = (Button) findViewById(R.id.button_save);
		mSaveButton.setOnClickListener(new OnClickListener() {
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
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "In onActivityResult()");
		if (data != null) {
			if (requestCode == ResultActivity.GET_TITLE_TAG
					&& resultCode == RESULT_OK) {
				data.putExtra(NoteUListHelper.MEDIA_TYPE, NoteUListHelper.MEDIA_TYPE_TEXT);
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

}
