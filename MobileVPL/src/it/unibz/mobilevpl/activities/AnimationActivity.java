package it.unibz.mobilevpl.activities;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.exception.AnimationExecutionException;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.util.ContextManager;

import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.ConfigChooserOptions;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

public class AnimationActivity extends BaseGameActivity {

	private final static int SECONDS_BEFORE_CLOSE = 2;

	private static final String LOG = "anim";
	private Project project;
	private Scene scene;
	
	private Camera camera;

	private boolean animationActive;
	private float elapsedTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		this.project = Project.getProjectById(intent.getLongExtra(EditorActivity.PROJECT, 0));

		ContextManager.X_POSITION = -1;
		ContextManager.Y_POSITION = -1;
		ContextManager.TERMINATE = false;

		this.elapsedTime = 0f;
		this.animationActive = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.animation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.animation_menu_stop_animation) {
			this.animationActive = false;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		Log.d(LOG, "onCreateEngineOptions");
		//DisplayMetrics metrics = new DisplayMetrics();
		//getWindowManager().getDefaultDisplay().getMetrics(metrics);
		//ContextManager.WIDTH = metrics.widthPixels;
		//ContextManager.HEIGHT = metrics.heightPixels;
		Log.d(LOG, "Width: " + ContextManager.WIDTH + " Height: " +  ContextManager.HEIGHT);
		/*
		if(((float)metrics.widthPixels/(float)metrics.heightPixels) == 1.5) {
			ContextManager.WIDTH = CAMERA_OPTIONS[1][0];
			ContextManager.HEIGHT = CAMERA_OPTIONS[1][1];
		} else {
			ContextManager.WIDTH = CAMERA_OPTIONS[0][0];
			ContextManager.HEIGHT = CAMERA_OPTIONS[0][1];
		}
		*/
		this.camera = new Camera(0, 0, ContextManager.WIDTH, ContextManager.HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(ContextManager.WIDTH, ContextManager.HEIGHT), camera);
		//final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		final ConfigChooserOptions configChooserOptions = engineOptions.getRenderOptions().getConfigChooserOptions();
		configChooserOptions.setRequestedRedSize(8);
		configChooserOptions.setRequestedGreenSize(8);
		configChooserOptions.setRequestedBlueSize(8);
		configChooserOptions.setRequestedAlphaSize(8);
		configChooserOptions.setRequestedDepthSize(16);
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		Log.d(LOG, "onCreateResources");
		this.project.prepareResources(this.getTextureManager());

		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException {
		Log.d(LOG, "onCreateScene");
		this.mEngine.registerUpdateHandler(new FPSLogger());
		this.scene = new Scene();
		this.scene.setOnSceneTouchListener(
			new IOnSceneTouchListener() {
				
				@Override
				public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
					if (pSceneTouchEvent.isActionDown())
				    {
				        ContextManager.X_POSITION = pSceneTouchEvent.getX();
				        ContextManager.Y_POSITION = pSceneTouchEvent.getY();
				        
				        Log.d("contextmanager", "x: " + pSceneTouchEvent.getX() + " | y: " + pSceneTouchEvent.getY());
				    }
					return false;
				}
			} 
		);

		this.project.prepareSprites(this.getVertexBufferObjectManager(), this.scene);

		this.project.prepareAll();

		this.animationActive = true;
		Log.d(LOG, "Scene created");
		pOnCreateSceneCallback.onCreateSceneFinished(this.scene);

		this.scene.registerUpdateHandler(
				new IUpdateHandler() {

					@Override
					public void onUpdate(float pSecondsElapsed) {
						if(animationActive) {
							if(!ContextManager.TERMINATE) {
								if(AnimationActivity.this.project.isAnimationFinished()) {
									ContextManager.TERMINATE = true;
								} else {
									try {
										Log.d(LOG, "Perform animation");
										AnimationActivity.this.project.executeAnimation(pSecondsElapsed, AnimationActivity.this.scene, 
												getSoundManager(), getMusicManager(), getResources());
									} catch (AnimationExecutionException e) {
										Toast.makeText(AnimationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							} else {
								Log.d(LOG, "Terminated: " + elapsedTime + " " + pSecondsElapsed);
								elapsedTime += pSecondsElapsed;
								if(elapsedTime >= SECONDS_BEFORE_CLOSE) {
									scene.unregisterUpdateHandler(this);
									returnToEditor();
								}
							}
						}
					}

					@Override
					public void reset() {
						Looper.prepare();
						Looper.loop();
					}

				}
				);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	private void returnToEditor() {
		//finishActivity(RESULT_OK);
		Intent intent = new Intent(this, EditorActivity.class );
		intent.putExtra(EditorActivity.PROJECT, this.project.getId());
		intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		this.startActivity( intent );
	}
}
