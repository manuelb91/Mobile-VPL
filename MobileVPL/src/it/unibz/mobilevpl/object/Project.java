package it.unibz.mobilevpl.object;

import it.unibz.mobilevpl.exception.AnimationExecutionException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.res.Resources;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Project extends SugarRecord<Project> implements Serializable {

    private String name;
    private Date creationDate;
    
    @Ignore private List<Scene> scenes;
    @Ignore private List<Sound> sounds;
    
    @Ignore private int index;
    @Ignore private boolean animationFinished;

    public Project() {
    }

    public Project(String name) {
        this();
        this.name = name;
        this.creationDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public static List<Project> getAllProjects() {
    	return Project.listAll(Project.class);
    }
    
    public static Project getProjectById(long id) {
    	return Project.findById(Project.class, id);
    }
    
    public static boolean projectExists(String name) {
    	boolean exists = false;
    	List<Project> projectList = Project.listAll(Project.class);
    	
    	int i = 0;
    	while((i < projectList.size()) && (!exists)) {
    		exists = (projectList.get(i).getName().toUpperCase().equals(name.trim().toUpperCase()));
    		i++;
    	}
    	
    	return exists;
    }
    
    public List<Scene> getScenes() {
    	if(this.scenes == null)
    		this.scenes = Scene.find(Scene.class, "project = ?", new String[] {String.valueOf(this.getId())}, null, "position", null);
		return this.scenes;
    }
    
    public boolean isEmpty() {
		return (this.getScenes() == null) || (this.getScenes().size() == 0);
	}
    
    public List<Sound> getSounds() {
    	if(this.sounds == null)
    		this.sounds = Sound.find(Sound.class, "project = ?", new String[] {String.valueOf(this.getId())}, null, null, null);
		return this.sounds;
    }
    
    public void addScene() {
    	Scene scene = new Scene();
    	this.addScene(scene);
    }
    
    public void addScene(Scene scene) {
    	scene.setProject(this);
    	this.getScenes().add(scene);
    	this.storeAll();
    }
    
    public void addSound(Sound sound) {
    	sound.setProject(this);
    	this.getSounds().add(sound);
    	this.storeAll();
    }
    
    public void store() {
    	this.save();
    }
    
    public void storeAll() {
    	//this.store();
    	for (int i = 0; i < this.getScenes().size(); i++) {
    		Scene scene = this.getScenes().get(i);
    		scene.setPosition(i + 1);
    		scene.storeAll();
		}
    	
    	for (Sound sound : this.getSounds()) {
			sound.store();
		}
    }
    
    public void remove() {
    	for (int i = 0; i < this.getScenes().size(); i++) {
    		this.getScenes().get(i).remove();
    		this.getScenes().remove(i);
		}
    	this.delete();
    }
    
    public void removeScene(int index) {
    	this.getScenes().get(index).remove();
    	this.getScenes().remove(index);
    	this.storeAll();
    }
    
    public void prepare() {
    	this.index = 0;
    	this.animationFinished = false;
    }
    
    public void prepareAll() {
    	this.prepare();
    	for (Scene scene : this.getScenes()) {
    		scene.prepareAll();
		}
    }
    
    public void prepareResources(TextureManager textureManager) {
		for (Scene scene : this.getScenes()) {
			scene.prepareResources(textureManager);
		}
	}
	
	public void prepareSprites(VertexBufferObjectManager vertexBufferObjectmanager, org.andengine.entity.scene.Scene scene) {
		for (Scene sceneElement : this.getScenes()) {
			sceneElement.prepareSprites(vertexBufferObjectmanager, scene);
		}
	}
	
	public boolean allScenesAndSpritesHaveImagesSet() {
		boolean imagesSet = true;
		for(Scene scene : this.getScenes()) {
			if(imagesSet && scene.getBackgroundPicture() != null && scene.getBackgroundPicture().length() > 0) {
				for(Sprite sprite : scene.getSprites()){
					if(imagesSet && (sprite.getPicture() == null || sprite.getPicture().length() == 0))
						imagesSet = false;
				}
			} else {
				imagesSet = false;
			}
		}	
		return imagesSet;
	}
	
	public void setSpritesInCurrentSceneVisible() {
		if(this.index > 0)
			this.getScenes().get(this.index - 1).setSpritesVisible(false);
		this.getScenes().get(this.index).setSpritesVisible(true);
	}
	
	public void executeAnimation(float elapsedTime, org.andengine.entity.scene.Scene scene, 
			SoundManager soundManager, MusicManager musicManager, Resources resources) throws AnimationExecutionException {
		if(this.index < this.getScenes().size()) {
			if(this.getScenes().get(this.index).sceneExecutionTerminated()) {
				this.index++;
			} else {
				if(this.getScenes().get(this.index).backgroundPictureUpdated()) {
					this.getScenes().get(this.index).executeSceneAnimation(elapsedTime, soundManager, musicManager, resources);
				} else {
					this.setSpritesInCurrentSceneVisible();
					this.getScenes().get(this.index).updateBackgroundPicture(scene);
				}
			}
		} else {
			this.animationFinished = true;
		}
	}
	
	public boolean isAnimationFinished() {
		return this.animationFinished;
	}
}
