package tekmob.nfc.note_u_list.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private static final String TAG="DBAdapter";
	private static final String DATABASE_NAME="mycompany.sqlite";
	private static final int DATABASE_VERSION=1;
	private static final String TABLE_CREATE = "create table IF NOT EXISTS berkas (_id integer primary key autoincrement, "
		+ "judul text not null, tag text not null, "
		+ "path text not null)";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS berkas";
	
	public static final String KEY_ROWID="_id";
	public static final String KEY_JUDUL="judul";
	public static final String KEY_TAG="tag";
	public static final String KEY_PATH="path";
	private final Context context;
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
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion 
					+ ", which will destroy all old data");
			db.execSQL(TABLE_DROP);
			onCreate(db);
		}
	}
	
	public DBAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public long insertBerkas(String judul, String path, String tag) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_JUDUL, judul);
		initialValues.put(KEY_TAG, tag);
		initialValues.put(KEY_PATH, path);
		
		return db.insert("berkas", null, initialValues);
	}
	
	public Cursor getAllBerkas() {
		return db.query("berkas", new String[] {
				KEY_ROWID, KEY_JUDUL, KEY_TAG, KEY_PATH
		}, null, null, null, null, KEY_ROWID + " DESC");
	}
}
