package tekmob.nfc.note_u_list.activities;

import java.io.File;

import tekmob.nfc.note_u_list.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class BeamActivity_ extends Activity {

	private NfcAdapter mNfcAdapter;
	// private static final String MIME_TYPE =
	// "application/tekmob.nfc.note_u_list";
	// private static final String PACKAGE_NAME = "tekmob.nfc.note_u_list";

	private Uri[] mFileUris = new Uri[10];
	private FileUriCallback mFileUriCallback;

	private File mParentPath;
	private Intent mIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)
				|| Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
			mFileUriCallback = new FileUriCallback();
			mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
			// if (mNfcAdapter == null) {
			// Toast.makeText(this, getString(R.string.no_nfc),
			// Toast.LENGTH_SHORT).show();
			// }
			// mNfcAdapter.setNdefPushMessageCallback(this, this);
			// mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
		} else {
			((TextView) findViewById(R.id.selected_filename))
					.setText(getString(R.string.no_nfc));
		}
	}

	private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
		public FileUriCallback() {
		}

		@Override
		public Uri[] createBeamUris(NfcEvent event) {
			return mFileUris;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.beam, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void browseForFile(View view) {
		Intent clientStartIntent = new Intent(this, FileActivity.class);
		startActivityForResult(clientStartIntent, 1);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			File transFile = (File) data.getExtras().get("file");
			transFile.setReadable(true, false);
			if (transFile.isFile()) {
				Uri fileUri = Uri.fromFile(transFile);
				mFileUris[0] = fileUri;
				if (transFile.canRead()) {
					setTargetFileStatus(transFile.getName()
							+ " selected for file transfer");
				} else {
					setTargetFileStatus("You do not have permission to read the file "
							+ transFile.getName());
				}
			} else {
				setTargetFileStatus("You may not transfer a directory, please select a single file");
			}

		}
	}

	public void setTargetFileStatus(String message) {
		TextView targetFileStatus = (TextView) findViewById(R.id.selected_filename);
		targetFileStatus.setText(message);
	}

	private void handleViewIntent() {
		mIntent = getIntent();
		String action = mIntent.getAction();

		if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
			Uri beamUri = mIntent.getData();

			if (TextUtils.equals(beamUri.getScheme(), "file")) {
				mParentPath = handleFileUri(beamUri);
			} else if (TextUtils.equals(beamUri.getScheme(), "content")) {
				mParentPath = handleContentUri(beamUri);
			}
		}
	}

	public File handleFileUri(Uri beamUri) {
		String fileName = beamUri.getPath();
		File copiedFile = new File(fileName);
		return copiedFile.getParentFile();
	}

	public File handleContentUri(Uri beamUri) {
		int filenameIndex;
		File copiedFile;
		String fileName;

		if (TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
			String[] projection = { MediaStore.MediaColumns.DATA };
			Cursor pathCursor = getContentResolver().query(beamUri, projection,
					null, null, null);

			if (pathCursor != null && pathCursor.moveToFirst()) {
				filenameIndex = pathCursor
						.getColumnIndex(MediaStore.MediaColumns.DATA);
				fileName = pathCursor.getString(filenameIndex);
				copiedFile = new File(fileName);
				return new File(copiedFile.getParent());
			} else {
				return null;
			}
		}
		return null;
	}

}
