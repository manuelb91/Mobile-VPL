package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.object.Project;
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
import android.widget.Toast;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ContextManager.WIDTH = metrics.widthPixels;
        ContextManager.HEIGHT = metrics.heightPixels;
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
        	Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        	startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void createProject(View view) {
    	Intent intent = new Intent(this, NewProjectActivity.class);
        startActivity(intent);
    }
    
    public void openProject(View view) {
    	List<Project> projectList = Project.getAllProjects();
    	if(projectList != null && projectList.size() > 0) {
    		Intent intent = new Intent(this, OpenProjectActivity.class);
            startActivity(intent);
    	} else {
    		Toast.makeText(this, getString(R.string.main_no_existing_projects_string), Toast.LENGTH_SHORT).show();
    	}
    }
}
