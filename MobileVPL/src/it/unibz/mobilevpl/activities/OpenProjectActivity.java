package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.object.Project;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class OpenProjectActivity extends Activity {

	public final static String OPEN_PROJECT_NAME = "OPEN_PROJECT_NAME";
	private List<Project> projectList;
	private ListView projectListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_open_project);

		this.projectList = Project.getAllProjects();

		updateProjectList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.open_project, menu);
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
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if(v.getId() == R.id.open_project_project_list) {
			AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;

			menu.add(Menu.NONE, acmi.position, 0, getResources().getString(R.string.editor_context_menu_delete));
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		this.projectList.remove(item.getItemId()).remove();
		
		updateProjectList();
		
		return super.onContextItemSelected(item);
	}

	private void updateProjectList() {
		this.projectListView = (ListView) findViewById(R.id.open_project_project_list); 

		String[] items = new String[this.projectList.size()];
		for (int i = 0; i < items.length; i++)
			items[i] = this.projectList.get(i).getName();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, items);  

		this.projectListView.setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						if(position >= 0 && position <= projectList.size()) {
							Intent intent = new Intent(OpenProjectActivity.this, EditorActivity.class);
							intent.putExtra(EditorActivity.PROJECT, projectList.get(position).getId());
							startActivity(intent);
						}
					}
				}
				);

		this.projectListView.setAdapter(adapter);
	}
}
