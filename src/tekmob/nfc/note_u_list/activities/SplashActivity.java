package tekmob.nfc.note_u_list.activities;

import tekmob.nfc.note_u_list.R;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class SplashActivity extends Activity {

	//buat halaman pertama yang muncul waktu aplikasi dibuka
	//ricky tolong bikin asset splashnya juga
	// buat load data dari database
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		ActionBar actionBar = getActionBar();
		actionBar.hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
