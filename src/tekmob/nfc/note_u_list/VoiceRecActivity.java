package tekmob.nfc.note_u_list;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class VoiceRecActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_rec);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.voice_rec, menu);
        return true;
    }
    
}
