package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.object.Project;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewProjectActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_project);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_project, menu);
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

	public void createProject(View view) {
		EditText name_field = (EditText)findViewById(R.id.new_project_name_input);
		String project_name = name_field.getText().toString().trim();
		name_field.setText("");
		if(project_name != null && project_name.length() > 0) {
			if(Project.projectExists(project_name)) {
				Toast.makeText(this, getString(R.string.new_project_project_exists), Toast.LENGTH_SHORT).show();
			} else {
				Project project = new Project(project_name);
				project.store();
				Intent intent = new Intent(this, EditorActivity.class);
				intent.putExtra(EditorActivity.PROJECT, project.getId());
				startActivity(intent);
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.new_project_dialog_title_string)
			.setTitle(R.string.new_project_dialog_message_string);
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
}
