package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.R.layout;
import tekmob.nfc.note_u_list.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ViewNoteDetailActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note_detail);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note_detail, menu);
		return true;
	}

}
