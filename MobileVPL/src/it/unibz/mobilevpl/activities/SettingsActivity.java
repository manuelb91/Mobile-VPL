package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.object.Setup;
import it.unibz.mobilevpl.util.ContextManager;

import java.util.List;

import org.xwalk.core.XWalkPreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        Setup setup = Setup.getSetup();
    	RadioButton buttonLocal = (RadioButton)findViewById(R.id.settings_html_source_local);
    	RadioButton buttonRemote = (RadioButton)findViewById(R.id.settings_html_source_remote);
    	if(setup.isExecuteLocal())
    		buttonLocal.setChecked(true);
    	else
    		buttonRemote.setChecked(true);
    	EditText address = (EditText)findViewById(R.id.settings_html_source_remote_address);
    	address.setText(setup.getRemoteAddress());
    	address.setEnabled(!setup.isExecuteLocal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
	public void onBackPressed() {
    	storeUpdatedSetup();
		Intent intent = new Intent(this, MainActivity.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		this.startActivity( intent );
	}
    
    public void setLocal(View view) {
    	EditText address = (EditText)findViewById(R.id.settings_html_source_remote_address);
    	address.setEnabled(false);
    }
    
    public void setRemote(View view) {
    	EditText address = (EditText)findViewById(R.id.settings_html_source_remote_address);
    	address.setEnabled(true);
    }
    
    private void storeUpdatedSetup() {
    	Setup setup = Setup.getSetup();
    	RadioButton button = (RadioButton)findViewById(R.id.settings_html_source_local);
    	setup.setExecuteLocal(button.isChecked());
    	EditText address = (EditText)findViewById(R.id.settings_html_source_remote_address);
    	setup.setRemoteAddress(address.getText().toString());
    	setup.save();
    }
}
