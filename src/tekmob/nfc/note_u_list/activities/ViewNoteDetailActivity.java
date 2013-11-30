package tekmob.nfc.note_u_list.activities;

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
import android.widget.Toast;

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
			Toast.makeText(getApplicationContext(), "OHHOHO",
					Toast.LENGTH_SHORT).show();
		} else if (type.equals(ViewNoteListObject.TYPE_IMAGE)) {
			Log.d(TAG, path);
			Bitmap image = BitmapFactory.decodeFile(path);
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Bitmap scaledBitmap = Bitmap.createScaledBitmap(image,
					image.getWidth(), image.getHeight(), true);
			Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);

			toBeInFrameLayout = new ImageView(getApplicationContext());
			((ImageView) toBeInFrameLayout).setImageBitmap(rotatedBitmap);
		}

		frameLayout = (FrameLayout) findViewById(R.id.noteViewLayout);
		frameLayout.addView(toBeInFrameLayout);
	}
}
