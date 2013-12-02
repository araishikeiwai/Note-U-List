package tekmob.nfc.note_u_list.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import tekmob.nfc.note_u_list.helpers.TagsHelper;
import tekmob.nfc.note_u_list.helpers.ViewNoteListAdapter;
import tekmob.nfc.note_u_list.helpers.ViewNoteListObject;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ViewNoteActivity extends ActionBarActivity {
	private ListView listView;
	private static final int MENU_OPEN = Menu.FIRST + 4;
	private static final int MENU_DELETE = Menu.FIRST + 5;
	private static final int MENU_RENAME = Menu.FIRST + 6;
	private static final int DIALOG_DELETE = 2;
	private static final int DIALOG_RENAME = 3;
	private static final String TAG = "ViewNoteActivity";
	private File mContextFile = new File("");
	private String mContextText = "", mContextTitle;
	private int id = 0;
	private List<ViewNoteListObject> mList;
	private AlertDialog dialog;
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
		mList = new ArrayList<ViewNoteListObject>();
		while (c.moveToNext()) {
			Log.d(TAG,
					c.getString(0) + "," + c.getString(1) + ","
							+ c.getString(2) + "," + c.getString(3));
			mList.add(new ViewNoteListObject(getFileName(c.getString(1)), c
					.getString(3), c.getString(2)));

		}
		ViewNoteListAdapter adapter = new ViewNoteListAdapter(
				ViewNoteActivity.this, mList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				mContextText = mList.get(position).getFilename();
				openFiles(mList.get(position).getFilename(), mList
						.get(position).getType(), mList.get(position)
						.getTitle());
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

	private String getFileName(String rawFileName) {
		return rawFileName.substring(13, rawFileName.length() - 4);
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
		mContextText = mList.get(name).getFilename();
		mContextTitle = mList.get(name).getTitle();
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

	private void openFiles(String filePath, String fileExtension,
			String fileTitle) {
		if (fileExtension.equals(ViewNoteListObject.TYPE_TEXT)
				|| fileExtension.equals(ViewNoteListObject.TYPE_IMAGE)) {
			Intent intent = new Intent(getApplicationContext(),
					ViewNoteDetailActivity.class);
			intent.putExtra(ViewNoteListObject.FILENAME, filePath);
			intent.putExtra(ViewNoteListObject.FILETYPE, fileExtension);
			intent.putExtra(ViewNoteListObject.FILETITLE, fileTitle);
			startActivity(intent);
		} else {
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
		}
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
		newFileName = file.getName().substring(0, 13)
				+ newFileName
				+ file.getName().substring(file.getName().length() - 4,
						file.getName().length());
		File newFile = new File(file.getParentFile(), newFileName);
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
					.setTitle(getString(R.string.really_delete, mContextTitle));

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
			et2.setText(mContextTitle);
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

	public boolean onOptionsItemSelected(MenuItem menu) {
		if (menu.getItemId() == R.id.action_filter) {
			String[] items = TagsHelper.getAvailableTags(getApplicationContext());
			// arraylist to keep the selected items
			final ArrayList seletedItems = new ArrayList();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.select_tag));
			builder.setMultiChoiceItems(items, null,
					new DialogInterface.OnMultiChoiceClickListener() {
						// indexSelected contains the index of item (of which
						// checkbox checked)
						@Override
						public void onClick(DialogInterface dialog,
								int indexSelected, boolean isChecked) {
							if (isChecked) {
								// If the user checked the item, add it to the
								// selected items
								// write your code when user checked the
								// checkbox
								seletedItems.add(indexSelected);
							} else if (seletedItems.contains(indexSelected)) {
								// Else, if the item is already in the array,
								// remove it
								// write your code when user Uchecked the
								// checkbox
								seletedItems.remove(Integer
										.valueOf(indexSelected));
							}
						}
					})
					// Set the action buttons
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// Your code when user clicked on OK
									// You can write the code to save the
									// selected item here

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// Your code when user clicked on Cancel

								}
							});

			dialog = builder.create();// AlertDialog dialog; create like this
										// outside onClick
			dialog.show();
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}

}
