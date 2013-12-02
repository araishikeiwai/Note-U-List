package tekmob.nfc.note_u_list.helpers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "mycompany.sqlite";
	private static final int DATABASE_VERSION = 1;
	private static final String KEY_ID = "_id";
	private static final String TABLE_BERKAS = "berkas";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_JUDUL = "judul";
	public static final String KEY_PATH = "path";
	public static final String KEY_EXT = "ext";
	public static final String KEY_IDTAG = "tagid";
	public static final String KEY_TAGNAME = "tagname";
	public static final String KEY_IDTAG_REL = "_idtag";
	public static final String KEY_IDBERKAS_REL = "_idberkas";
	private final Context context;
	private static final String TABLE_CREATE = "create table IF NOT EXISTS berkas (_id integer primary key autoincrement, judul text not null, path text not null, ext text not null)";
	private static final String TABLE_CREATE_TAG = "create table IF NOT EXISTS tag (tagid integer primary key autoincrement, tagname text not null)";
	private static final String TABLE_CREATE_TAGFILES = "create table IF NOT EXISTS tag_rel (_idtag integer, _idberkas integer, foreign key (_idtag) REFERENCES tag(tagid) ON DELETE CASCADE ON UPDATE CASCADE, foreign key (_idberkas) REFERENCES berkas(_id) ON DELETE CASCADE ON UPDATE CASCADE)";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS berkas; DROP TABLE IF EXISTS tag; DROP TABLE IF EXISTS tag_rel";

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	public DBAdapter(Context ctx) {
		this.context = ctx;
		dbHelper = new DatabaseHelper(this.context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_CREATE);
			db.execSQL(TABLE_CREATE_TAG);
			db.execSQL(TABLE_CREATE_TAGFILES);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL(TABLE_DROP);
			onCreate(db);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			if (!db.isReadOnly()) {
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}
	}

	public DBAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public DBAdapter opens() throws SQLException {
		db = dbHelper.getReadableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long insertBerkas(String judul, String path, String ext) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_JUDUL, judul);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_EXT, ext);

		return db.insert("berkas", null, initialValues);
	}

	public long insertTag(String tagName) {
		if (!isTagInTable(tagName)) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_TAGNAME, tagName);
			Log.d(TAG, tagName);
			return db.insert("tag", null, initialValues);
		}
		return 0;
	}

	public long insertTagRelationship(String filePath, String tagName) {
		ContentValues initialValues = new ContentValues();
		int filid = getBerkasIdFromPath(filePath);
		int tagid = getTagIdFromTagName(tagName);
		initialValues.put(KEY_IDBERKAS_REL, filid);
		initialValues.put(KEY_IDTAG_REL, tagid);
		Log.d(TAG, "Inserting tag " + tagName + "(" + tagid
				+ ") associated with file " + filePath + "(" + filid + ")");
		return db.insert("tag_rel", null, initialValues);
	}

	private int getTagIdFromTagName(String tagName) {
		// TODO Auto-generated method stub
		String query = "SELECT " + KEY_IDTAG + " FROM tag WHERE " + KEY_TAGNAME
				+ " = '" + tagName + "'";
		Log.d(TAG, query);
		Cursor id = db.rawQuery(query, null);
		id.moveToFirst();
		return id.getInt(0);
	}

	private int getBerkasIdFromPath(String filePath) {
		// TODO Auto-generated method stub
		String query = "SELECT " + KEY_ID + " FROM berkas WHERE " + KEY_PATH
				+ " = '" + filePath + "'";
		Log.d(TAG, query);
		Cursor id = db.rawQuery(query, null);
		id.moveToFirst();
		return id.getInt(0);
	}

	public Cursor getAllAvailableTags() {
		String query = "SELECT * FROM tag ORDER BY " + KEY_IDTAG + " ASC";
		Log.d(TAG, query);
		return db.rawQuery(query, null);
	}

	public boolean isTagInTable(String tagName) {
		String query = "SELECT * FROM tag WHERE " + KEY_TAGNAME + "= '"
				+ tagName + "'";
		Log.d(TAG, query);
		Cursor tags = db.rawQuery(query, null);
		tags.moveToFirst();

		if (tags != null) {
			if (tags.getCount() > 0) {
				tags.close();
				return true;
			} else {
				tags.close();
				return false;
			}
		}
		return false;
	}

	public void deleteBerkas(String judul) {
		db.delete(TABLE_BERKAS, KEY_JUDUL + " = ?", new String[] { judul });
	}

	public void delete(int id) {
		db.delete(TABLE_BERKAS, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public int getID() {
		int id = 0;
		return id;
	}

	public int getBerkasCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BERKAS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	public int updateBerkas(String judul, String path, String judulAsli) {

		ContentValues values = new ContentValues();
		values.put(KEY_JUDUL, judul);
		values.put(KEY_PATH, path);

		// updating row
		return db.update(TABLE_BERKAS, values, KEY_JUDUL + " = ?",
				new String[] { judulAsli });
	}

	public Cursor getAllBerkas() {
		return db.query("berkas", new String[] { KEY_ROWID, KEY_JUDUL,
				KEY_PATH, KEY_EXT }, null, null, null, null, KEY_ROWID
				+ " DESC");
	}

	public Cursor getFiltered(ArrayList<String> type, ArrayList<String> tags) {
		// public Cursor getFiltered(ArrayList<String> tags) {
		// TODO Auto-generated method stub
		String selection_type;
		String selection_tags;
		if (type.size() > 0) {
			selection_type = KEY_EXT + "= ";

			for (String x : type) {
				selection_type += "'" + x + "' OR " + KEY_EXT + "= ";
			}
			selection_type = selection_type.substring(0,
					selection_type.length() - 9);
		} else {
			selection_type = "";
		}

		if (tags.size() > 0) {
			selection_tags = KEY_IDTAG_REL + "= ";

			for (String x : tags) {
				int idtag = getTagIdFromTagName(x);
				selection_tags += idtag + " OR " + KEY_IDTAG_REL + "= ";
			}
			selection_tags = selection_tags.substring(0,
					selection_tags.length() - 12);
		} else {
			selection_tags = "";
		}

		String selection = "";
		if (selection_type.length() > 0) {
			selection_type = "(" + selection_type + ")";
			selection = " where " + selection_type;
		}
		if (selection_tags.length() > 0) {
			selection_tags = "(" + selection_tags + ")";
			selection = " where " + selection_tags;
		}
		if (selection_tags.length() > 0 && selection_type.length() > 0)
			selection = " where " + selection_type + " AND " + selection_tags;

		String query = "SELECT DISTINCT " + KEY_ROWID + "," + KEY_JUDUL + ","
				+ KEY_PATH + "," + KEY_EXT
				+ " from tag_rel a left join berkas b on a." + KEY_IDBERKAS_REL
				+ "=b." + KEY_ROWID + selection;
		Log.d(TAG, query);
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		return c;

		// String query = "SELECT " + KEY_IDBERKAS_REL + " FROM tag_rel"
		// + selection;
		// Log.d(TAG, query);
		// Cursor t = db.rawQuery(query, null);
		// t.moveToFirst();
		//
		// String newSelection = "";
		// do {
		// newSelection += KEY_ROWID + "=" + t.getInt(0) + " OR ";
		// } while (t.moveToNext());
		// if (newSelection.length() > 0) {
		// newSelection = " WHERE "
		// + newSelection.substring(0, newSelection.length() - 4);
		// }
		//
		// query = "SELECT * FROM berkas" + newSelection;
		// Log.d(TAG, query);
		// Cursor c = db.rawQuery(query, null);
		// c.moveToFirst();
		// return c;
	}

	public String[] getTagsForFile(String filepath) {
		int fileId = getBerkasIdFromPath(filepath);
		String query = "SELECT " + KEY_IDTAG_REL + " FROM tag_rel WHERE "
				+ KEY_IDBERKAS_REL + "=" + fileId;
		Log.d(TAG, query);
		Cursor c = db.rawQuery(query, null);
		c.moveToFirst();
		ArrayList<String> res = new ArrayList<String>();
		if (c.getCount() > 0) {
			do {
				query = "SELECT " + KEY_TAGNAME + " from tag where "
						+ KEY_IDTAG + " = " + c.getInt(0);
				Log.d(TAG, query);
				Cursor d = db.rawQuery(query, null);
				d.moveToFirst();
				if (d.getCount() > 0)
					res.add(d.getString(0));
			} while (c.moveToNext());
		}
		String[] toRet = new String[res.size()];
		for (int i = 0; i < toRet.length; i++) {
			toRet[i] = res.get(i);
		}
		return toRet;
	}
}
