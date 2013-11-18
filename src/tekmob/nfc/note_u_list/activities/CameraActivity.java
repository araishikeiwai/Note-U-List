package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.CameraPreview;
import tekmob.nfc.note_u_list.helpers.NoteUListHelper;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements PictureCallback {

	protected static final String TAG = "Note-U-List! Camera";
	private Camera mCamera;
	private CameraPreview mPreview;
	private byte[] mPictureData;
	private ImageButton mCaptureButton;
	private Activity mActivity = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mCamera = getCameraInstance();

		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		mCaptureButton = (ImageButton) findViewById(R.id.button_capture);
		mCaptureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mCamera.takePicture(null, null, CameraActivity.this);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	protected void onPause() {
		super.onPause();
		releaseCamera();
	}

	protected void onResume() {
		super.onResume();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {

		}
		return c;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		((FrameLayout) findViewById(R.id.camera_preview)).removeAllViews();
		mCamera.stopPreview();
		mPictureData = data;
		setContentView(R.layout.activity_camera_captured);

		// get the image and rotate it to display
		final Bitmap image = BitmapFactory
				.decodeByteArray(data, 0, data.length);
		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap scaledBitmap = Bitmap.createScaledBitmap(image,
				image.getWidth(), image.getHeight(), true);
		Bitmap rotatedBitmap = Bitmap
				.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
						scaledBitmap.getHeight(), matrix, true);
		ImageView imageView = (ImageView) findViewById(R.id.camera_result);
		imageView.setImageBitmap(rotatedBitmap);

		ImageView saveButton, discardButton, shareButton;
		saveButton = (ImageView) findViewById(R.id.button_camera_captured_save);
		discardButton = (ImageView) findViewById(R.id.button_camera_captured_discard);
		shareButton = (ImageView) findViewById(R.id.button_camera_captured_share);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent result = new Intent(mActivity.getApplicationContext(),
						ResultActivity.class);
				startActivityForResult(result, ResultActivity.GET_TITLE_TAG);
			}
		});

		discardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO when migrating to API 4.0 and above, change to
				// recreate() method!
				Toast.makeText(mActivity, "Picture note discarded!",
						Toast.LENGTH_SHORT).show();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		});

		shareButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO implement!
				Toast.makeText(mActivity, "NOT YET IMPLEMENTED",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "In onActivityResult()");
		if (data != null) {
			if (requestCode == ResultActivity.GET_TITLE_TAG
					&& resultCode == RESULT_OK) {
				data.putExtra(NoteUListHelper.MEDIA_TYPE, NoteUListHelper.MEDIA_TYPE_IMAGE);
				NoteUListHelper.save(mActivity, data, mPictureData);
				finish();
			}
		}
	}

}