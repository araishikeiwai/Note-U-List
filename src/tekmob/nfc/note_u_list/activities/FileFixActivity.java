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

public class FileFixActivity extends Activity {
	private File targetFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String path = "/mnc/sdcard/Note-U-List!";
		targetFile = new File(path);
		returnTarget();
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