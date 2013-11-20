package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

public class ViewNoteActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		DBAdapter db = new DBAdapter(ViewNoteActivity.this);
		db.open();
		Cursor c = db.getAllBerkas();
		if (c.moveToFirst()) {
			do {
				Toast.makeText(ViewNoteActivity.this, c.getString(1) + ", " + c.getString(2)+ ", " + c.getString(3)
						, Toast.LENGTH_SHORT).show();
			} while (c.moveToNext());
		}
		else
			Toast.makeText(ViewNoteActivity.this, "No data", Toast.LENGTH_SHORT).show();
		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
