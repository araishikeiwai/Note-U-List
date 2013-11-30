package tekmob.nfc.note_u_list.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ViewNoteActivity extends Activity {
	private ListView listView;
	private List<String> items;
	private static final int MENU_OPEN = Menu.FIRST + 4;
	private static final int MENU_DELETE = Menu.FIRST + 5;
	private static final int MENU_RENAME = Menu.FIRST + 6;
	private static final int DIALOG_DELETE = 2;
	private static final int DIALOG_RENAME = 3;
	private File mContextFile = new File("");
	private String mContextText = "";
	private int id = 0;
	List<String> filename;
	List<String> mime;
	Cursor c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		refresh();

	}

	public void refresh() {
		listView = (ListView) findViewById(R.id.list);
		registerForContextMenu(listView);
		DBAdapter db = new DBAdapter(ViewNoteActivity.this);
		db.open();
		c = db.getAllBerkas();
		items = new ArrayList<String>();
		filename = new ArrayList<String>();
		mime = new ArrayList<String>();
		while (c.moveToNext()) {
			mime.add(c.getString(4));
			filename.add(c.getString(3));
			items.add(c.getString(1));
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, items);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				int name = position;
				mContextText = filename.get(name);
				openFiles(filename.get(name), mime.get(name));
			}
		});
		// if (c.moveToFirst()) {
		// do {
		// Toast.makeText(ViewNoteActivity.this, c.getString(1) + ", " +
		// c.getString(2)+ ", " + c.getString(3)
		// , Toast.LENGTH_SHORT).show();
		// } while (c.moveToNext());
		// }
		// else
		// Toast.makeText(ViewNoteActivity.this, "No data",
		// Toast.LENGTH_SHORT).show();

		// XML Parsing Using AsyncTask...

		db.close();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Context Menu");
		menu.add(0, MENU_DELETE, 0, "Delete");
		menu.add(0, MENU_RENAME, 0, "Rename");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		DBAdapter db = new DBAdapter(this);
		int count = db.getBerkasCount();
		int name = info.position;
		id = count - name;
		mContextText = filename.get(name);
		switch (item.getItemId()) {
		case MENU_DELETE:
			showDialog(DIALOG_DELETE);
			return true;
		case MENU_RENAME:
			showDialog(DIALOG_RENAME);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void openFiles(String filePath, String fileExtension) {
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(filePath);
		intent.setDataAndType(Uri.fromFile(file), fileExtension);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.application_not_available,
					Toast.LENGTH_SHORT).show();
		}
		;
	}

	private void deleteFileOrFolder(File file) {
		DBAdapter db = new DBAdapter(this);

		if (file.delete()) {
			db.open();
			db.deleteBerkas(file.getName());
			db.close();
			// Delete was successful.
			Toast.makeText(this, R.string.file_deleted, Toast.LENGTH_SHORT)
					.show();

		} else {
			Toast.makeText(this, R.string.error_deleting_file,
					Toast.LENGTH_SHORT).show();

		}
		refresh();
	}

	private void renameFileOrFolder(File file, String newFileName) {
		File newFile = new File(newFileName);
		rename(file, newFile);
		DBAdapter db = new DBAdapter(this);
		db.open();
		db.updateBerkas(newFile.getName(), newFile.getAbsolutePath(),
				file.getName());
		refresh();

	}

	/**
	 * @param oldFile
	 * @param newFile
	 */
	private void rename(File oldFile, File newFile) {
		int toast = 0;
		if (oldFile.renameTo(newFile)) {
			// Rename was successful.
			toast = R.string.file_renamed;
		} else {
			toast = R.string.error_renaming_file;

		}
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final File file = new File(mContextText);
		switch (id) {
		case DIALOG_DELETE:
			AlertDialog alert_back = new AlertDialog.Builder(this).create();
			alert_back
					.setTitle(getString(R.string.really_delete, mContextText));

			alert_back.setButton2("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alert_back.setButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteFileOrFolder(file);
				}
			});
			alert_back.show();
			break;
		case DIALOG_RENAME:
			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.dialog_new_folder, null);
			final EditText et2 = (EditText) view.findViewById(R.id.foldername);
			et2.setText(mContextText);
			AlertDialog alert_bac = new AlertDialog.Builder(this).create();
			alert_bac.setTitle(getString(R.string.menu_rename));
			alert_bac.setView(view);
			alert_bac.setButton2("No", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alert_bac.setButton("Yes", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					renameFileOrFolder(file, et2.getText().toString());
				}
			});
			alert_bac.show();
			break;
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
