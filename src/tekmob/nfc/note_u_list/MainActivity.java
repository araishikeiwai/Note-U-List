package tekmob.nfc.note_u_list;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity implements OnClickListener {

	ImageButton mNoteButton, mCameraButton, mViewNoteButton, mVoiceRecButton, mShareButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mNoteButton = (ImageButton) findViewById(R.id.noteButton);
        mNoteButton.setOnClickListener(this);
        mCameraButton = (ImageButton) findViewById(R.id.cameraButton);
        mCameraButton.setOnClickListener(this);
        mViewNoteButton = (ImageButton) findViewById(R.id.viewNoteButton);
        mViewNoteButton.setOnClickListener(this);
        mVoiceRecButton = (ImageButton) findViewById(R.id.voiceRecButton);
        mVoiceRecButton.setOnClickListener(this);
        mShareButton = (ImageButton) findViewById(R.id.shareButton);
        mShareButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public void onClick(View v) {
    	Intent next = null;
    	switch (v.getId()) {
    	case R.id.noteButton:
    		next = new Intent(getApplicationContext(), NoteActivity.class);
    		break;
    	case R.id.cameraButton:
    		next = new Intent(getApplicationContext(), CameraActivity.class);
    		break;
    	case R.id.viewNoteButton:
    		next = new Intent(getApplicationContext(), ViewNoteActivity.class);
    		break;
    	case R.id.voiceRecButton:
    		next = new Intent(getApplicationContext(), VoiceRecActivity.class);
    		break;
    	case R.id.shareButton:
    		next = new Intent(getApplicationContext(), ShareActivity.class);
    		break;
    	}
    	startActivity(next);
    }
}
