package tekmob.nfc.note_u_list.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewNoteActivity extends Activity {
	private ListView listView;
	private List<String> items;
	private static final int MENU_OPEN = Menu.FIRST + 4;
	private static final int MENU_DELETE = Menu.FIRST + 5;
	private static final int MENU_RENAME = Menu.FIRST + 6;
	private File mContextFile = new File("");
	List<String> filename;
	List<String> mime;
	Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		listView = (ListView) findViewById(R.id.list);
		registerForContextMenu(listView);
		DBAdapter db = new DBAdapter(ViewNoteActivity.this);
		db.open();
		c = db.getAllBerkas();
		items = new ArrayList<String>();
		filename = new ArrayList<String>();
		mime = new ArrayList<String>();
		while(c.moveToNext()){
			mime.add(c.getString(4));
			filename.add(c.getString(3));
			items.add(c.getString(1));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, items);
		listView.setAdapter(adapter); 
		//		if (c.moveToFirst()) {
		//			do {
		//				Toast.makeText(ViewNoteActivity.this, c.getString(1) + ", " + c.getString(2)+ ", " + c.getString(3)
		//						, Toast.LENGTH_SHORT).show();
		//			} while (c.moveToNext());
		//		}
		//		else
		//			Toast.makeText(ViewNoteActivity.this, "No data", Toast.LENGTH_SHORT).show();

		// XML Parsing Using AsyncTask...

		db.close();
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, MENU_OPEN, 0, "Open");
		menu.add(0, MENU_DELETE, 0, "Delete");
		menu.add(0, MENU_RENAME, 0, "Rename");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int name = info.position;
		switch (item.getItemId()) {
		case MENU_OPEN:
			openFiles(filename.get(name),mime.get(name));
			return true;
		case MENU_DELETE:
			Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show(); 
			return true;
		case MENU_RENAME:
			Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();  
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	private void openFiles(String filePath,String fileExtension) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(filePath);
		intent.setDataAndType(Uri.fromFile(file), fileExtension);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.application_not_available, Toast.LENGTH_SHORT).show();
		};
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
