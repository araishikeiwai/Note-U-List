package tekmob.nfc.note_u_list.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tekmob.nfc.note_u_list.activities.ResultActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class NoteUListHelper {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_TEXT = 2;
	public static final int MEDIA_TYPE_AUDIO = 3;
	public static final int MEDIA_OTHERS = 4;
	public static final String MEDIA_TYPE = "mediaType";
	protected static final String TAG = "Note-U-List! Helper";
	public static String ext = "";

	public NoteUListHelper() {
	}

	public static void save(Context context, Intent data, byte[] toSave) {
		// Log.d(TAG, "YEAAYY::" +
		// data.getExtras().get(ResultActivity.NOTE_TITLE)
		// + "::" + data.getExtras().get(ResultActivity.NOTE_TAG));
		File fileLocation = getOutputMediaFile(
				(Integer) data.getExtras().get(MEDIA_TYPE), (String) data
						.getExtras().get(ResultActivity.NOTE_TITLE));
		if (fileLocation == null) {
			// Log.d(TAG,
			// "Error creating media file, check storage permissions");
			return;
		}
		String[] tags = ((String) data.getExtras().get(ResultActivity.NOTE_TAG))
				.split(",");
		String judul = fileLocation.getName();
		String path = fileLocation.getAbsolutePath();

		String sem = "";
		int dot = path.lastIndexOf(".");
		if (dot >= 0)
			sem = path.substring(dot);
		DBAdapter db = new DBAdapter(context);

		try {
			db.open();
			long id = db.insertBerkas(judul, path, ext);
			for (String tag : tags) {
				tag = tag.trim();
				if (tag.length() > 0) {
					db.insertTag(tag);
					db.insertTagRelationship(path, tag);
				}
			}
			FileOutputStream fos = new FileOutputStream(fileLocation);
			fos.write(toSave);
			fos.close();
			// Log.d(TAG, "FILE SAVED!");
			Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			// Log.d(TAG, "Error accessing file: " + e.getMessage());
		} finally {
			db.close();
		}
	}

	public static void save_audio(Context context, Intent data,
			File fileLocation) {
		// Log.d(TAG, "YEAAYY::" +
		// data.getExtras().get(ResultActivity.NOTE_TITLE)
		// + "::" + data.getExtras().get(ResultActivity.NOTE_TAG));

		if (fileLocation == null) {
			// Log.d(TAG,
			// "Error creating media file, check storage permissions");
			return;
		}
		String[] tags = ((String) data.getExtras().get(ResultActivity.NOTE_TAG))
				.split(",");
		String judul = fileLocation.getName();
		// Log.d("judul awal di database", judul);
		String path = fileLocation.getAbsolutePath();

		String sem = "";
		int dot = path.lastIndexOf(".");
		if (dot >= 0)
			sem = path.substring(dot);
		DBAdapter db = new DBAdapter(context);

		db.open();
		long id = db.insertBerkas(judul, path, ViewNoteListObject.TYPE_AUDIO);
		for (String tag : tags) {
			tag = tag.trim();
			if (tag.length() > 0) {
				db.insertTag(tag);
				db.insertTagRelationship(path, tag);
			}
		}
		// Log.d(TAG, "FILE SAVED!");
		Toast.makeText(context, "Note saved!", Toast.LENGTH_SHORT).show();
		db.close();
	}

	private static File getOutputMediaFile(int type, String noteTitle) {
		File mediaStorageDir = new File(
				Environment.getExternalStorageDirectory(), "Note-U-List!");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				// Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmm")
				.format(new Date());
		String mediaName = mediaStorageDir.getPath() + File.separator
				+ timeStamp + "_" + noteTitle;
		File mediaFile = null;
		if (type == MEDIA_TYPE_IMAGE) {
			ext = ViewNoteListObject.TYPE_IMAGE;
			mediaFile = new File(mediaName + ".jpg");
		} else if (type == MEDIA_TYPE_TEXT) {
			mediaFile = new File(mediaName + ".txt");
			ext = ViewNoteListObject.TYPE_TEXT;
		} else if (type == MEDIA_TYPE_AUDIO) {
			ext = ViewNoteListObject.TYPE_AUDIO;
			mediaFile = new File(mediaName + ".3ga");
		} else if (type == MEDIA_OTHERS) {
			// TODO
		} else {
			return null;
		}

		// if duplicate, append "_" at the end of the filename
		while (mediaFile.exists()) {
			mediaFile = new File(mediaFile.getPath().substring(0,
					mediaFile.getPath().length() - 4)
					+ "_"
					+ mediaFile.getPath().substring(
							mediaFile.getPath().length() - 4));
		}

		// Log.d(TAG, mediaFile.getPath());
		return mediaFile;
	}
}
