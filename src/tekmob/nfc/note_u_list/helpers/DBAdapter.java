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
	private static final String KEY_ID = "_id";
	private static final String TABLE_BERKAS = "berkas";
	public static final String KEY_ROWID="_id";
	public static final String KEY_JUDUL="judul";
	public static final String KEY_TAG="tag";
	public static final String KEY_PATH="path";
	public static final String KEY_EXT="ext";
	private final Context context;
	private static final String TABLE_CREATE = "create table IF NOT EXISTS berkas (_id integer primary key autoincrement, "
		+ "judul text not null, tag text not null, "
		+ "path text not null, ext text not null)";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS berkas";

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
	public DBAdapter opens() throws SQLException {
		db = dbHelper.getReadableDatabase();
		return this;
	}
	public void close() {
		dbHelper.close();
	}
	
	public long insertBerkas(String judul, String path, String tag,String ext) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_JUDUL, judul);
		initialValues.put(KEY_TAG, tag);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_EXT, ext);
		
		return db.insert("berkas", null, initialValues);
	}
	public void deleteBerkas(String judul){
		db.delete(TABLE_BERKAS, KEY_JUDUL + " = ?",
				new String[] { judul });
	}
	public void delete(int id){
		db.delete(TABLE_BERKAS, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}
	public int getID(){
		int id = 0;
		return id;
	}
	public int getBerkasCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BERKAS;
		SQLiteDatabase db=  dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}
	public int updateBerkas(String judul, String path,String judulAsli) {

		ContentValues values = new ContentValues();
		values.put(KEY_JUDUL, judul);
		values.put(KEY_PATH, path);

		// updating row
		return db.update(TABLE_BERKAS, values, KEY_JUDUL + " = ?",
				new String[] { judulAsli });
	}
	public Cursor getAllBerkas() {
		return db.query("berkas", new String[] {
				KEY_ROWID, KEY_JUDUL, KEY_TAG, KEY_PATH, KEY_EXT
		}, null, null, null, null, KEY_ROWID + " DESC");
	}
}
