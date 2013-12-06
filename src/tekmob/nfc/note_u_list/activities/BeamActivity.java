package tekmob.nfc.note_u_list.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tekmob.nfc.note_u_list.R;
import tekmob.nfc.note_u_list.helpers.DBAdapter;
import tekmob.nfc.note_u_list.helpers.NfcUtils;
import tekmob.nfc.note_u_list.helpers.ViewNoteListObject;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BeamActivity extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private NfcAdapter mNfcAdapter;
	private byte[] mToSend;
	File files;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			Toast.makeText(this, getString(R.string.no_nfc), Toast.LENGTH_SHORT)
					.show();
			onBackPressed();
		}
	}

	private static final String MIME_TYPE = "application/tekmob.nfc.note_u_list";
	private static final String PACKAGE_NAME = "tekmob.nfc.note_u_list";

	/**
	 * Implementation for the CreateNdefMessageCallback interface
	 */
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		// NdefMessage msg = new NdefMessage(new NdefRecord[] {
		// NfcUtils.createRecord(MIME_TYPE, mToSend),
		// NdefRecord.createApplicationRecord(PACKAGE_NAME) });
		String text = files.getName();
		NdefMessage msg = new NdefMessage(
				new NdefRecord[] { NdefRecord.createMime(MIME_TYPE, mToSend),NdefRecord.createMime(MIME_TYPE, text.getBytes()) });
		return msg;
	}

	private static final int MESSAGE_SENT = 1;
	private static final String TAG = "BeamActivity";

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(getApplicationContext(), "Message sent!",
						Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		setIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			try {
				processIntent(getIntent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK && requestCode == 1) {
			File transFile = (File) data.getExtras().get("file");
			files = new File(transFile.getAbsolutePath());
			transFile.setReadable(true, false);
			if (transFile.isFile()) {
				if (transFile.canRead()) {
					Uri fileUri = Uri.fromFile(transFile);
					Log.d(TAG, fileUri.toString());
					try {
						mToSend = getByteFromUri(fileUri);
					} catch (Exception e) {
						e.printStackTrace();
					}
					setTargetFileStatus(transFile.getName()
							+ " selected for file transfer. Now put the Droids in back-to-back position and touch to beam.");
					// Register callback to set NDEF message
					mNfcAdapter.setNdefPushMessageCallback(this, this);
					// Register callback to listen for message-sent success
					mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

				} else {
					setTargetFileStatus("You do not have permission to read the file "
							+ transFile.getName());
				}
			} else {
				setTargetFileStatus("You may not transfer a directory, please select a single file");
			}

		}
	}

	private byte[] getByteFromUri(Uri fileUri) throws Exception {
		InputStream iStream = getContentResolver().openInputStream(fileUri);
		return getBytes(iStream);

	}

	private byte[] getBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
		}
		return byteBuffer.toByteArray();
	}

	public void setTargetFileStatus(String message) {
		TextView targetFileStatus = (TextView) findViewById(R.id.selected_filename);
		targetFileStatus.setText(message);
	}

	/**
	 * Parses the NDEF Message from the intent and toast to the user
	 * 
	 * @throws IOException
	 */
	void processIntent(Intent intent) throws IOException {
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// in this context, only one message was sent over beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String payload = new String(msg.getRecords()[0].getPayload());
		String name = new String(msg.getRecords()[1].getPayload());
		FileOutputStream fileOuputStream = new FileOutputStream(
				new File("/mnt/sdcard/Note-U-List!/", name));
		fileOuputStream.write(msg.getRecords()[0].getPayload());
		fileOuputStream.close();
		Toast.makeText(getApplicationContext(),
				"Message received over beam: " + name, Toast.LENGTH_LONG)
				.show();
		String path = "/mnt/sdcard/Note-U-List!/"+name;
		String ext = "";
		int dot = path.lastIndexOf(".");
		if (dot >= 0)
			ext = path.substring(dot);
		if(ext.equals(".txt"))
			ext = ViewNoteListObject.TYPE_TEXT;
		if(ext.equals(".jpg"))
			ext = ViewNoteListObject.TYPE_IMAGE;
		if(ext.equals(".3ga"))
			ext = ViewNoteListObject.TYPE_AUDIO;
		DBAdapter db = new DBAdapter(this);
		db.open();
		db.insertBerkas(name, path, ext);
	}

}
