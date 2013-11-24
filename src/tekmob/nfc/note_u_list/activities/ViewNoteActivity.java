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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ViewNoteActivity extends Activity implements OnClickListener{
	private ListView listView;
	private List<NameBean> items;
	private List<NameBean> item;
	private NamesAdapter objAdapter = null;
	private Button btnGetSelected;
	
	Cursor c;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		listView = (ListView) findViewById(R.id.listview);
		btnGetSelected = (Button) findViewById(R.id.btnget);
		btnGetSelected.setOnClickListener(ViewNoteActivity.this);
		NameBean objItem;


		DBAdapter db = new DBAdapter(ViewNoteActivity.this);
		db.open();
		c = db.getAllBerkas();
		items = new ArrayList<NameBean>();
		while(c.moveToNext()){
			objItem = new NameBean();
			objItem.setName(c.getString(1));
			items.add(objItem);
		}
//				objItem = new NameBean();
//				objItem.setName("Umar");
//				item.add(objItem);
//		//		objItem = new NameBean();
//		//		objItem.setName("Umar");
//		//		items.add(objItem);
				

		//		if (c.moveToFirst()) {
		//			do {
		//				Toast.makeText(ViewNoteActivity.this, c.getString(1) + ", " + c.getString(2)+ ", " + c.getString(3)
		//						, Toast.LENGTH_SHORT).show();
		//			} while (c.moveToNext());
		//		}
		//		else
		//			Toast.makeText(ViewNoteActivity.this, "No data", Toast.LENGTH_SHORT).show();
		
		// XML Parsing Using AsyncTask...
		setAdapterToListview();
		db.close();
	}
	public void setAdapterToListview() {


		objAdapter = new NamesAdapter(ViewNoteActivity.this, items);
		listView.setAdapter(objAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				CheckBox chk = (CheckBox) view.findViewById(R.id.checkbox);
				NameBean bean = items.get(position);
				if (bean.isSelected()) {
					bean.setSelected(false);
					chk.setChecked(false);
				} else {
					bean.setSelected(true);
					chk.setChecked(true);
				}

			}
		});

	}

	// Toast is here...
	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	public void onClick(View v) {
		StringBuffer sb = new StringBuffer();

		// Retrive Data from list
		for (NameBean bean : items) {

			if (bean.isSelected()) {
				sb.append(bean.getName());
				sb.append(",");
			}
		}

		showAlertView(sb.toString().trim());

	}

	private void showAlertView(String str) {
		AlertDialog alert = new AlertDialog.Builder(this).create();
		if (TextUtils.isEmpty(str)) {
			alert.setTitle("Not Selected");
			alert.setMessage("No One is Seleceted!!!");
		} else {
			// Remove , end of the name
			String strContactList = str.substring(0, str.length() - 1);

			alert.setTitle("Selected");
			alert.setMessage(strContactList);
		}
		alert.setButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
