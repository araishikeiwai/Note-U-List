package tekmob.nfc.note_u_list.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import tekmob.nfc.note_u_list.helpers.ViewNoteListAdapter;
import tekmob.nfc.note_u_list.helpers.ViewNoteListObject;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FileActivity extends Activity {
	private String mContextText = "";
	private List<ViewNoteListObject> mList;
	Cursor c;
	private File targetFile;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_browser);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		showNotes();
	}

	private String getFileName(String rawFileName) {
		return rawFileName.substring(13, rawFileName.length() - 4);
	}

	private void showNotes() {
		listView = (ListView) findViewById(R.id.list);
		DBAdapter db = new DBAdapter(this);
		db.open();
		c = db.getAllBerkas();
		mList = new ArrayList<ViewNoteListObject>();
		while (c.moveToNext()) {
			mList.add(new ViewNoteListObject(getFileName(c.getString(1)), c
					.getString(3), c.getString(2), db.getTagsForFile(c
					.getString(2))));

		}

		ViewNoteListAdapter adapter = new ViewNoteListAdapter(
				this, mList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				mContextText = mList.get(position).getFilename();
				File f = new File(mContextText);
				if (f.isFile()) {
					targetFile = f;
					returnTarget();
					// Return target File to activity
				}
			}
		});

	}

	public void returnTarget() {

		Intent returnIntent = new Intent();
		returnIntent.putExtra("file", targetFile);
		setResult(RESULT_OK, returnIntent);
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.file, menu);
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
