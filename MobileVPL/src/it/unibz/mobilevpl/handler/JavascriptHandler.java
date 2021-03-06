package it.unibz.mobilevpl.handler;

import it.unibz.mobilevpl.activities.EditorActivity;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.util.XMLManager;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;

public class JavascriptHandler {
	
	private enum Action { START_ACTIVITY, LOAD_WEB_VIEW };

	private Project project;
	private int selectedScene;
	private int selectedSprite;
	
	private int selectedSceneUpdate;
	private int selectedSpriteUpdate;
	
	private Action action;
	
	private Context context;
	private Intent intent;
	
	private EditorActivity editor;
	
	public JavascriptHandler(Project project) {
		this.project = project;
	}
	
	@org.xwalk.core.JavascriptInterface
	public void sendXMLToAndroid(String xmlContent) {
		try {
			XMLManager.parseXML(this.project, this.selectedScene, this.selectedSprite, xmlContent);
//			this.project.storeAll();
			this.selectedScene = this.selectedSceneUpdate;
			this.selectedSprite = this.selectedSpriteUpdate;
			
			if(this.action == Action.START_ACTIVITY) {
				Context c = this.context;
				Intent i = this.intent;
				this.resetParameters();
				c.startActivity(i);
			} else if(this.action == Action.LOAD_WEB_VIEW) {
				this.editor.prepareWebView(selectedSceneUpdate, selectedSpriteUpdate);
				this.resetParameters();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSelectedScene(int selectedScene) {
		this.selectedScene = selectedScene;
	}

	public void setSelectedSprite(int selectedSprite) {
		this.selectedSprite = selectedSprite;
	}
	
	public void setUpdatedValues(int selectedSceneUpdate, int selectedSpriteUpdate) {
		this.selectedSceneUpdate = selectedSceneUpdate;
		this.selectedSpriteUpdate = selectedSpriteUpdate;
	}
	
	public void setActivityParameters(Context context, Intent intent) {
		this.context = context;
		this.intent = intent;
		this.action = Action.START_ACTIVITY;
	}
	
	public void loadWebViewWhenFinishedToStore(EditorActivity editor) {
		this.editor = editor;
		this.action = Action.LOAD_WEB_VIEW;
	}
	
	private void resetParameters() {
		this.context = null;
		this.intent = null;
		this.editor = null;
		this.action = null;
	}
}
