package tekmob.nfc.note_u_list.activities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.ViewNoteListObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewNoteDetailActivity extends Activity {

	public static final String TAG = "ViewNoteDetailActivity";
	private FrameLayout frameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();
		setContentView(R.layout.activity_view_note_detail);

		String type = getIntent().getExtras().getString(
				ViewNoteListObject.FILETYPE);
		String path = getIntent().getExtras().getString(
				ViewNoteListObject.FILENAME);
		String title = getIntent().getExtras().getString(
				ViewNoteListObject.FILETITLE);

		((TextView) findViewById(R.id.noteViewTitle)).setText(title);

		View toBeInFrameLayout = new View(getApplicationContext());

		if (type.equals(ViewNoteListObject.TYPE_TEXT)) {
			StringBuffer sb = new StringBuffer();

			try {
				File file = new File(path);

				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
				reader.close();
			} catch (Exception e) {
				Log.e(TAG, "Failed to load text note!", e);
			}

			toBeInFrameLayout = new TextView(getApplicationContext());
			((TextView) toBeInFrameLayout).setTextSize(15.0f);
			((TextView) toBeInFrameLayout).setText(sb.toString());
			((TextView) toBeInFrameLayout).setVisibility(View.VISIBLE);
		} else if (type.equals(ViewNoteListObject.TYPE_IMAGE)) {
			Log.d(TAG, path);
			Bitmap image = BitmapFactory.decodeFile(path);
			toBeInFrameLayout = new ImageView(getApplicationContext());
			if (image.getWidth() > image.getHeight()) {
				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(image,
						image.getWidth(), image.getHeight(), true);
				Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
						scaledBitmap.getWidth(), scaledBitmap.getHeight(),
						matrix, true);
				((ImageView) toBeInFrameLayout).setImageBitmap(rotatedBitmap);
			} else {
				((ImageView) toBeInFrameLayout).setImageBitmap(image);
			}
		}

		frameLayout = (FrameLayout) findViewById(R.id.noteViewLayout);
		frameLayout.addView(toBeInFrameLayout);
	}
}
