package tekmob.nfc.note_u_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class ResultActivity extends Activity {

	public static final String NOTE_TITLE = "note title";
	public static final String NOTE_TAG = "note_tag";
	public static final int GET_TITLE_TAG = 21;
	private static final String TAG = "Note-U-List! Result";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		final EditText noteTitle = (EditText) findViewById(R.id.note_title);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line,
				getTagsAutocompletes());
		final MultiAutoCompleteTextView noteTag = (MultiAutoCompleteTextView) findViewById(R.id.note_tag);
		MultiAutoCompleteTextView.CommaTokenizer tokenizer = new MultiAutoCompleteTextView.CommaTokenizer();

		noteTag.setAdapter(adapter);
		noteTag.setTokenizer(tokenizer);

		Button submit_button = (Button) findViewById(R.id.button_submit);
		submit_button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (noteTitle.getText().toString().equals("")) {
					Log.d(TAG, "Content invalid");
					Toast t = Toast.makeText(ResultActivity.this,
							"Insert title!", Toast.LENGTH_SHORT);
					t.show();
				} else {
					Log.d(TAG, "Content valid");
					Intent intent = new Intent();
					intent.putExtra(NOTE_TITLE, noteTitle.getText().toString());
					intent.putExtra(NOTE_TAG, noteTag.getText().toString());
					Log.d(TAG,
							"FINISH RESULT::"
									+ intent.getExtras().get(
											ResultActivity.NOTE_TITLE)
									+ "::"
									+ intent.getExtras().get(
											ResultActivity.NOTE_TAG));
					setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
	}

	private String[] getTagsAutocompletes() {
		// TODO get from database
		return new String[] { "aDum", "bDum", "cDum", "dDum", "eDum", "fDum",
				"gDum", "hDum", "iDum", "jDum", "kDum", "lDum", "mDum", "nDum",
				"oDum", "pDum", "qDum", "rDum", "sDum", "tDum", "uDum", "vDum",
				"wDum", "xDum", "yDum", "zDum" };
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.result, menu);
		return true;
	}

}
