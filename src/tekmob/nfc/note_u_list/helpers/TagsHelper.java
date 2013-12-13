package tekmob.nfc.note_u_list.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class TagsHelper {

	public static final String TAG = "TagsHelper";

	public static String[] getAvailableTags(Context context) {
		DBAdapter db = new DBAdapter(context);
		db.open();
		Cursor tags = db.getAllAvailableTags();
		ArrayList<String> tagsList = new ArrayList<String>();
		while (tags.moveToNext()) {
			tagsList.add(tags.getString(1));
			// Log.d(TAG, tags.getString(0) + ":::" + tags.getString(1));
		}
		db.close();
		String[] toRet = new String[tagsList.size()];
		for (int i = 0; i < toRet.length; i++) {
			toRet[i] = tagsList.get(i);
		}
		return toRet;
	}
}
