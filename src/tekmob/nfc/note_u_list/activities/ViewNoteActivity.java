package tekmob.nfc.note_u_list.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewNoteActivity extends Activity {
	private ListView listView;
	private List<String> items;
	
	Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		listView = (ListView) findViewById(R.id.list);

		DBAdapter db = new DBAdapter(ViewNoteActivity.this);
		db.open();
		c = db.getAllBerkas();
		items = new ArrayList<String>();
		while(c.moveToNext()){
			
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
