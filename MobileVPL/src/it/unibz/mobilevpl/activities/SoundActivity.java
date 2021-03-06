package it.unibz.mobilevpl.activities;

import java.io.File;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.R.id;
import it.unibz.mobilevpl.R.layout;
import it.unibz.mobilevpl.R.menu;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.object.Sound;
import it.unibz.mobilevpl.util.FileManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SoundActivity extends Activity {

	public final static String PROJECT = "PROJECT";

	private final static int RESULT_LOAD_SOUND = 1;

	private final static int DELETE = 0;

	private Project project;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound);

		Intent intent = getIntent();
		this.project = Project.getProjectById(intent.getLongExtra(PROJECT, 0));

		ListView listView = (ListView) findViewById(R.id.sound_list);
		registerForContextMenu(listView);

		updateSoundList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sound, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case R.id.action_add_sound:
				Intent intentAudio = new Intent();
				intentAudio.setType("audio/*");
				intentAudio.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intentAudio, RESULT_LOAD_SOUND);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if(v.getId() == R.id.sound_list) {
			AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;

			menu.add(Menu.NONE, DELETE, 0, getResources().getString(R.string.editor_context_menu_delete));
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if(item.getItemId() == DELETE) {
			this.project.getSounds().remove(info.position).remove();

			updateSoundList();
		}

		return super.onContextItemSelected(item);
	}

	public void updateSoundList() {
		ListView listView = (ListView) findViewById(R.id.sound_list); 

		String[] items = new String[this.project.getSounds().size()];
		for (int i = 0; i < items.length; i++)
			items[i] = this.project.getSounds().get(i).getFilename();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, items);  

		listView.setAdapter(adapter);
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data){


		if(requestCode == RESULT_LOAD_SOUND && resultCode == RESULT_OK){

			Uri selectedAudio = data.getData(); 
			String[] filePathColumn = { MediaStore.Audio.Media.DATA };

			Cursor cursor = this.getContentResolver().query(selectedAudio, filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String audioPath = cursor.getString(columnIndex);
			cursor.close();

			File audioFile = new File(audioPath);

			File destination = FileManager.transferSoundFile(this.getApplicationContext(), audioPath);

			Sound sound = new Sound();
			sound.setPath(destination.getAbsolutePath());
			sound.setFilename(audioFile.getName());

			this.project.addSound(sound);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
