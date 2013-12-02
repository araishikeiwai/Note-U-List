package tekmob.nfc.note_u_list.helpers;

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
	private static final String TABLE_CREATE_TAGFILES = "create table IF NOT EXISTS tag_rel (_idtag integer, _idberkas integer, foreign key (_idtag) REFERENCES tag(tagid) ON DELETE CASCADE ON UPDATE CASCADE, foreign key (_idberkas) REFERENCES berkas(_id) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(_idtag, _idberkas))";
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

	public Cursor getAllAvailableTags() {
		return db.rawQuery("SELECT * FROM tag ORDER BY " + KEY_IDTAG + " ASC", null);
	}

	public boolean isTagInTable(String tagName) {
		Cursor tags = db.rawQuery("SELECT * FROM tag WHERE " + KEY_TAGNAME
				+ "= '" + tagName + "'", null);

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

	public long insertTagRel(String tagName, String judul) {
		return 0;
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
}
