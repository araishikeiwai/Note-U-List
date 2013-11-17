package tekmob.nfc.note_u_list;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements PictureCallback {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
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

		ImageButton saveButton, discardButton, shareButton;
		saveButton = (ImageButton) findViewById(R.id.button_camera_captured_save);
		discardButton = (ImageButton) findViewById(R.id.button_camera_captured_discard);
		shareButton = (ImageButton) findViewById(R.id.button_camera_captured_share);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent result = new Intent(mActivity.getApplicationContext(),
						ResultActivity.class);
				startActivityForResult(result, ResultActivity.GET_TITLE_TAG);
			}
		});
		
		discardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO when migrating to API 4.0 and above, change to recreate() method!
				Toast.makeText(mActivity, "Picture note discarded!", Toast.LENGTH_SHORT).show();
				Intent intent = getIntent();
				finish();
				startActivity(intent);
			}
		});
		
		shareButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO implement!
				Toast.makeText(mActivity, "NOT YET IMPLEMENTED", Toast.LENGTH_SHORT).show();
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
				Log.d(TAG,
						"YEAAYY SAVED::"
								+ data.getExtras().get(
										ResultActivity.NOTE_TITLE) + "::"
								+ data.getExtras().get(ResultActivity.NOTE_TAG));

				File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE,
						(String) data.getExtras()
								.get(ResultActivity.NOTE_TITLE));
				if (pictureFile == null) {
					Log.d(TAG,
							"Error creating media file, check storage permissions");
					return;
				}

				// TODO organize tags into database
				// TODO put link into database

				try {
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(mPictureData);
					fos.close();
					Log.d(TAG, "FILE SAVED!");
					Toast.makeText(mActivity, "Picture note saved!", Toast.LENGTH_SHORT).show();
				} catch (FileNotFoundException e) {
					Log.d(TAG, "File not found: " + e.getMessage());
				} catch (IOException e) {
					Log.d(TAG, "Error accessing file: " + e.getMessage());
				}
				
				finish();
			}
		}
	}

	private static File getOutputMediaFile(int type, String noteTitle) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Note-U-List!");
		
		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ timeStamp + "_" + noteTitle + ".jpg");
		} else {
			return null;
		}

		// if duplicate, append "_" at the end of the filename
		while (mediaFile.exists()) {
			mediaFile = new File(mediaFile.getPath().substring(0,
					mediaFile.getPath().length() - 4)
					+ "_.jpg");
		}

		Log.d(TAG, mediaFile.getPath());
		return mediaFile;
	}

}
