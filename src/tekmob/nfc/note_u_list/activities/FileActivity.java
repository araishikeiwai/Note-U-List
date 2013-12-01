package tekmob.nfc.note_u_list.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tekmob.nfc.note_u_list.helpers.DBAdapter;
import tekmob.nfc.note_u_list.helpers.ViewNoteListObject;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FileActivity extends Activity {
	private ArrayList<String> list;
	private ArrayList<String> real;
	private ListView listView;
	private File targetFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		showNotes();
	}
	private String getFileName(String rawFileName) {
		return rawFileName.substring(13, rawFileName.length() - 4);
	}
	private void showNotes() {
		DBAdapter db = new DBAdapter(this);
		db.open();
		Cursor c = db.getAllBerkas();
		list = new ArrayList<String>();
		real = new ArrayList<String>();
		while(c.moveToNext()){
			list.add(getFileName(c.getString(1)));
			real.add(c.getString(1));
		}
 		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
 				android.R.layout.simple_list_item_1, android.R.id.text1, list);
 		listView.setAdapter(adapter);
 		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				File f = new File(real.get(position));
				if(f.isFile()) {
					  targetFile = f;
					  returnTarget();
					  //Return target File to activity
				}
			}
		});
		
	}
	public void returnTarget(){
		
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

}
