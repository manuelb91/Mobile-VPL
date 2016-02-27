package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.adapter.ExpandableListAdapter;
import it.unibz.mobilevpl.exception.AnimationExecutionException;
import it.unibz.mobilevpl.filter.SpritePositionFilter;
import it.unibz.mobilevpl.handler.JavascriptHandler;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.object.Scene;
import it.unibz.mobilevpl.object.Setup;
import it.unibz.mobilevpl.object.Sound;
import it.unibz.mobilevpl.object.Sprite;
import it.unibz.mobilevpl.util.FileManager;
import it.unibz.mobilevpl.util.XMLManager;

import java.io.File;
import java.util.List;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends Activity {

	private final static String NEGATION = "-";
	private final static String ASSET_PATH = "file:///android_asset/";
	private final static String EMPTY_PAGE = "<html><head><title></title></head><body></body></html>";

	public final static String PROJECT_INTENT = "PROJECT";

	private final static int RESULT_LOAD_IMAGE_SCENE = 1;
	private final static int RESULT_LOAD_IMAGE_SPRITE = 2;

	public final static String PROJECT = "PROJECT";
	public final static String SPRITE = "SPRITE";
	public final static String SPRITE_GROUP = "SPRITE_GROUP";
	public final static String SPRITE_CHILD = "SPRITE_CHILD";

	private final static int ADD_SPRITE = 1;
	private final static int SELECT_PICTURE = 2;
	private final static int DELETE = 3;
	private final static int CHOOSE_COORDINATES = 4;

	private JavascriptHandler javascriptHandler;

	private ExpandableListAdapter adapter;

	private int selectedScene = -1;
	private int selectedSprite = -1;

	private Project project;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);

		Intent intent = getIntent();
		this.project = Project.getProjectById(intent.getLongExtra(PROJECT_INTENT, 0));

		this.prepareExpandableListView((ExpandableListView) findViewById(R.id.editor_control));

		this.javascriptHandler = new JavascriptHandler(this.project);

		XWalkView mXWalkView = (XWalkView) findViewById(R.id.block_editor);
		mXWalkView.addJavascriptInterface(this.javascriptHandler, "cpjs");
		mXWalkView.setResourceClient(new BlocklyResourceClient(mXWalkView));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editor, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_store:
			if(this.selectedScene != -1 && this.selectedSprite != -1) {
				
				this.javascriptHandler.setSelectedScene(this.selectedScene);
				this.javascriptHandler.setSelectedSprite(this.selectedSprite);
				this.storeBlockStructure();
			}
			return true;
		case R.id.action_animation:
			try {
				if(projectReadyForAnimation()) {
					Intent intent_animation = new Intent(this, AnimationActivity.class);
					intent_animation.putExtra(PROJECT, this.project.getId());
					if(this.selectedScene != -1 && this.selectedSprite != -1) {
						this.javascriptHandler.setSelectedScene(this.selectedScene);
						this.javascriptHandler.setSelectedSprite(this.selectedSprite);
						this.javascriptHandler.setActivityParameters(this, intent_animation);
						storeBlockStructure();
					} else {
						startActivity(intent_animation);
					}
				}
			} catch(AnimationExecutionException e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.action_add_scene:
			this.project.addScene();
			this.updateScenesSpritesList();
			return true;
		case R.id.action_add_sound:
			Intent intent_sound = new Intent(EditorActivity.this, SoundActivity.class);
			intent_sound.putExtra(SoundActivity.PROJECT, this.project.getId());
			startActivity(intent_sound);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if(v.getId() == R.id.editor_control) {
			ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
			int type = ExpandableListView.getPackedPositionType(info.packedPosition);


			menu.setHeaderTitle(getResources().getString(R.string.editor_context_menu_title));

			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				menu.add(Menu.NONE, ADD_SPRITE, 0, getResources().getString(R.string.editor_context_menu_add_sprite));
			}

			menu.add(Menu.NONE, SELECT_PICTURE, 0, getResources().getString(R.string.editor_context_menu_select_picture));
			menu.add(Menu.NONE, DELETE, 0, getResources().getString(R.string.editor_context_menu_delete));

			if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
				menu.add(Menu.NONE, CHOOSE_COORDINATES, 0, getResources().getString(R.string.editor_context_menu_choose_coordinates));
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
		int type = ExpandableListView.getPackedPositionType(info.packedPosition);
		//		int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		//		int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);
		this.selectedScene = ExpandableListView.getPackedPositionGroup(info.packedPosition);
		this.selectedSprite = ExpandableListView.getPackedPositionChild(info.packedPosition);

		switch (item.getItemId()) {
		case ADD_SPRITE:

			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_sprite);
			dialog.setTitle(getResources().getString(R.string.dialog_sprite_label_title));
			
			EditText coordinateAdd = (EditText) dialog.findViewById(R.id.dialog_sprite_x);
			coordinateAdd.setFilters(new InputFilter[] { new SpritePositionFilter() });
			coordinateAdd = (EditText) dialog.findViewById(R.id.dialog_sprite_y);
			coordinateAdd.setFilters(new InputFilter[] { new SpritePositionFilter() });

			Button dialogButton = (Button) dialog.findViewById(R.id.dialog_sprite_create);
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int x = 0;
					int y = 0;
					boolean xValid = false;
					boolean yValid = false;

					EditText xCoordinate = (EditText) dialog.findViewById(R.id.dialog_sprite_x);
					xValid = (xCoordinate.getText() != null && xCoordinate.getText().length() > 0 &&
							!xCoordinate.getText().toString().equals(NEGATION));

					EditText yCoordinate = (EditText) dialog.findViewById(R.id.dialog_sprite_y);
					yValid = (yCoordinate.getText() != null && yCoordinate.getText().length() > 0 && 
							!yCoordinate.getText().toString().equals(NEGATION));

					if(xValid && yValid) {
						dialog.dismiss();

						x = Integer.parseInt(xCoordinate.getText().toString());
						y = Integer.parseInt(yCoordinate.getText().toString());

						Sprite newSprite = new Sprite(x, y);
						Scene selectedScene = EditorActivity.this.project.getScenes().get(EditorActivity.this.selectedScene);
						EditorActivity.this.selectedSprite = selectedScene.addSprite(newSprite);
						EditorActivity.this.adapter.notifyDataSetChanged();
						
						prepareWebView(EditorActivity.this.selectedScene, EditorActivity.this.selectedSprite);
					} else {
						Toast.makeText(EditorActivity.this, getString(R.string.dialog_error_x_y), Toast.LENGTH_SHORT).show();
					}
				}
			});

			dialog.show();

			break;
		case SELECT_PICTURE:
			Intent intent_scene_sprite = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			boolean isSprite = (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD);
			startActivityForResult(intent_scene_sprite, isSprite ? RESULT_LOAD_IMAGE_SPRITE : RESULT_LOAD_IMAGE_SCENE);
			break;

		case DELETE:
			if(type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
				this.project.removeScene(this.selectedScene);
				this.selectedScene = -1;
				this.selectedSprite = -1;
			} else {
				this.project.getScenes().get(this.selectedScene).removeSprite(this.selectedSprite);
				this.selectedSprite = -1;
			}
			this.loadEmptyPage();
			this.adapter.notifyDataSetChanged();
			break;

		case CHOOSE_COORDINATES:

			final Dialog dialogCoordinates = new Dialog(this);
			dialogCoordinates.setContentView(R.layout.dialog_sprite);
			
			EditText textView = (EditText) dialogCoordinates.findViewById(R.id.dialog_sprite_x);
			textView.setText(String.valueOf(EditorActivity.this.project.getScenes().get(EditorActivity.this.selectedScene)
					.getSprites().get(EditorActivity.this.selectedSprite).getxPosition()));
			textView.setFilters(new InputFilter[] { new SpritePositionFilter() });
			textView = (EditText) dialogCoordinates.findViewById(R.id.dialog_sprite_y);
			textView.setText(String.valueOf(EditorActivity.this.project.getScenes().get(EditorActivity.this.selectedScene)
					.getSprites().get(EditorActivity.this.selectedSprite).getyPosition()));
			textView.setFilters(new InputFilter[] { new SpritePositionFilter() });
			dialogCoordinates.setTitle(getResources().getString(R.string.dialog_sprite_label_title));

			Button dialogCoordinatesButton = (Button) dialogCoordinates.findViewById(R.id.dialog_sprite_create);
			dialogCoordinatesButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int x = 0;
					int y = 0;
					boolean xValid = false;
					boolean yValid = false;

					EditText xCoordinate = (EditText) dialogCoordinates.findViewById(R.id.dialog_sprite_x);
					xValid = (xCoordinate.getText() != null && xCoordinate.getText().length() > 0 && 
							!xCoordinate.getText().toString().equals(NEGATION));

					EditText yCoordinate = (EditText) dialogCoordinates.findViewById(R.id.dialog_sprite_y);
					yValid = (yCoordinate.getText() != null && yCoordinate.getText().length() > 0 && 
							!yCoordinate.getText().toString().equals(NEGATION));

					if(xValid && yValid) {
						dialogCoordinates.dismiss();

						x = Integer.parseInt(xCoordinate.getText().toString());
						y = Integer.parseInt(yCoordinate.getText().toString());

						Sprite sprite = EditorActivity.this.project.getScenes().get(EditorActivity.this.selectedScene)
								.getSprites().get(EditorActivity.this.selectedSprite);
						sprite.setxPosition(x);
						sprite.setyPosition(y);
						EditorActivity.this.adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(EditorActivity.this, getString(R.string.dialog_error_x_y), Toast.LENGTH_SHORT).show();
					}
				}
			});

			dialogCoordinates.show();

			break;

		}

		return super.onContextItemSelected(item);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (((requestCode == RESULT_LOAD_IMAGE_SCENE) || (requestCode == RESULT_LOAD_IMAGE_SPRITE)) && 
				resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = this.getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			File destination = FileManager.transferImageFile(this.getApplicationContext(), picturePath);

			if(requestCode == RESULT_LOAD_IMAGE_SCENE) {
				Scene scene = this.project.getScenes().get(this.selectedScene);
				scene.setBackgroundPicture(destination.getAbsolutePath());
				scene.store();
			} else {
				Sprite sprite = this.project.getScenes().get(this.selectedScene).getSprites().get(this.selectedSprite);
				sprite.setPicture(destination.getAbsolutePath());
				sprite.store();
			}

			ImageView imageView = (ImageView) findViewById(R.id.editor_image);
			imageView.setImageBitmap(BitmapFactory.decodeFile(destination.getAbsolutePath()));
		}
	}

	private void prepareExpandableListView(ExpandableListView listView) {
		this.adapter = new ExpandableListAdapter(this, this.project);
		listView.setAdapter(this.adapter);
		registerForContextMenu(listView);
		
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if(groupPosition != -1) {
					setPictureOnView(project.getScenes().get(groupPosition).getBackgroundPicture());
				}
				return false;
			}
		});

		listView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				if(groupPosition != -1 && childPosition != -1) {
					setPictureOnView(project.getScenes().get(groupPosition).getSprites().get(childPosition).getPicture());
					
					if(selectedScene != -1 && selectedSprite != -1) {
						javascriptHandler.setSelectedScene(selectedScene);
						javascriptHandler.setSelectedSprite(selectedSprite);
						javascriptHandler.setUpdatedValues(groupPosition, childPosition);
						javascriptHandler.loadWebViewWhenFinishedToStore(EditorActivity.this);
						
						selectedScene = groupPosition;
						selectedSprite = childPosition;
						
						storeBlockStructure();
					} else {
						selectedScene = groupPosition;
						selectedSprite = childPosition;
						prepareWebView(groupPosition, childPosition);
					}
					
				}
				return false;
			}

		});
	}

	public void prepareWebView(int scene, int sprite) {
		XWalkView mXWalkView = (XWalkView) findViewById(R.id.block_editor);

		Setup setup = Setup.getSetup();
		if(setup.isExecuteLocal()) {

			String options = "";
			String sounds = "";

			List<Sprite> spriteList = this.project.getScenes().get(scene).getSprites();
			for(int i = 0; i < spriteList.size(); i++) {
				if(i != sprite) {
					options += ", [\"Sprite " + (i + 1) + "\", \"" + i + "\"]";
				}
			}

			List<Sound> soundList = this.project.getSounds();
			for (int i = 0; i < soundList.size(); i++) {
				Sound sound = soundList.get(i);
				sounds += "[\"" + sound.getFilename() + "\", \"" + sound.getId() + "\"]";
				if(i < (soundList.size() - 1))
					sounds += ", ";
			}
			
			String html = FileManager.readHTMLFile(this.getAssets())
					.replaceAll("<OPTIONS>", options)
					.replaceAll("<SOUNDLIST>", sounds);

			mXWalkView.load(ASSET_PATH, html);
		} else {
			mXWalkView.load(setup.getRemoteAddress(), null);
		}
	}
	
	private void loadEmptyPage() {
		XWalkView mXWalkView = (XWalkView) findViewById(R.id.block_editor);
		mXWalkView.load(ASSET_PATH, EMPTY_PAGE);
	}

	public void storeBlockStructure() {
		XWalkView mXWalkView = (XWalkView) findViewById(R.id.block_editor);
		mXWalkView.load("javascript:generateCodeForBlocks();void(0)", null);
	}

	public void loadBlockStructure() {
		XWalkView mXWalkView = (XWalkView) findViewById(R.id.block_editor);
		mXWalkView.load("javascript:loadCodeForBlocks('" + XMLManager.generateXML(this.project, selectedScene, selectedSprite) + "');void(0)", null);
	}

	public void updateScenesSpritesList() {
		this.adapter.notifyDataSetChanged();
	}

	public boolean isSpriteSelected() {
		return this.selectedSprite >= 0;
	}
	
	private void setPictureOnView(String picture) {
		ImageView imageView = (ImageView)findViewById(R.id.editor_image);
		Bitmap image;

		if(picture != null && picture.length() > 0) {
			image = BitmapFactory.decodeFile(picture);
			imageView.setImageBitmap(image);
		}
	}
	
	private boolean projectReadyForAnimation() throws AnimationExecutionException {
		if(this.project.isEmpty())
			throw new AnimationExecutionException(getResources().getString(R.string.editor_error_empty_project));
		if(!this.project.allScenesAndSpritesHaveImagesSet())
			throw new AnimationExecutionException(getResources().getString(R.string.editor_error_missing_images));
		return true;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class );
		intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		if(this.selectedScene != -1 && this.selectedSprite != -1) {
			this.javascriptHandler.setActivityParameters(this, intent);
			storeBlockStructure();
		} else {
			this.startActivity( intent );
		}
	}

	private class BlocklyResourceClient extends XWalkResourceClient {

		public BlocklyResourceClient(XWalkView view) {
			super(view);
		}

		@Override
		public void onLoadFinished(XWalkView view, String url) {
			loadBlockStructure();
		}
	}
}
