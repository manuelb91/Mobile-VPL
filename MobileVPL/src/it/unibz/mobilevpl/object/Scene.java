package it.unibz.mobilevpl.object;

import it.unibz.mobilevpl.definition.BlockDefinition.BlockType;
import it.unibz.mobilevpl.definition.BlockDefinition.OperationType;
import it.unibz.mobilevpl.exception.AnimationExecutionException;
import it.unibz.mobilevpl.util.ContextManager;
import it.unibz.mobilevpl.util.FileManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.res.Resources;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Scene extends SugarRecord<Scene> implements Serializable {

	private String backgroundPicture;
	private Project project;
	private int position;

	@Ignore private List<Sprite> sprites;

	@Ignore private boolean backgroundUpdated;
	@Ignore private boolean sceneExecutionTerminated;

	@Ignore private ITexture backgroundTexture;
	@Ignore private TextureRegion backgroundTextureRegion;
	@Ignore private BitmapTextureAtlas backgroundTextureAtlas;
	@Ignore private SpriteBackground background;

	public Scene() {
	}

	public Scene(String backgroundPicture, int position, Project project) {
		this();
		this.backgroundPicture = backgroundPicture;
		this.position = position;
		this.project = project;
	}

	public String getBackgroundPicture() {
		return backgroundPicture;
	}

	public void setBackgroundPicture(String backgroundPicture) {
		this.backgroundPicture = backgroundPicture;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Sprite> getSprites(){
		if(this.sprites == null)
			this.sprites = Sprite.find(Sprite.class, "scene = ?", new String[] {String.valueOf(this.getId())}, null, "position", null);
		return this.sprites;
	}
	
	public boolean isEmpty() {
		return (this.getSprites() == null) || (this.getSprites().size() == 0);
	}

	public int addSprite(Sprite sprite) {
		sprite.setScene(this);
		this.getSprites().add(sprite);
		this.storeAll();
		return this.getSprites().size() - 1;
	}

	public void store() {
		this.save();
	}

	public void storeAll() {
		this.store();
		for (int i = 0; i < this.getSprites().size(); i++) {
			Sprite sprite = this.getSprites().get(i);
			sprite.setPosition(i + 1);
			sprite.storeAll();
		}
	}
	
	public void remove() {
		for (int i = 0; i < this.getSprites().size(); i++) {
			this.getSprites().get(i).remove();
			this.getSprites().remove(i);
		}
		if(this.backgroundPicture != null && this.backgroundPicture.length() > 0)
			FileManager.removeFile(this.backgroundPicture);
		this.delete();
	}
	
	public void removeSprite(int index) {
    	this.getSprites().get(index).remove();
    	this.getSprites().remove(index);
    	this.storeAll();
    }

	public void prepare() {
		this.backgroundUpdated = false;
		this.sceneExecutionTerminated = false;
	}

	public void prepareAll() {
		this.prepare();
		for (Sprite sprite : this.getSprites()) {
			sprite.prepareAll();
		}
	}

	public void prepareResources(TextureManager textureManager) {
		try {
			this.backgroundTexture = new BitmapTexture(textureManager, new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return new FileInputStream(backgroundPicture);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.backgroundTextureRegion = TextureRegionFactory.extractFromTexture(this.backgroundTexture); 
		this.backgroundTexture.load();

		for (Sprite sprite : this.getSprites())
			sprite.prepareResource(textureManager);
	}

	public void prepareSprites(VertexBufferObjectManager vertexBufferObjectmanager, org.andengine.entity.scene.Scene scene) {
		this.background = new SpriteBackground(new org.andengine.entity.sprite
				.Sprite(ContextManager.WIDTH/2, ContextManager.HEIGHT/2, this.backgroundTextureRegion, vertexBufferObjectmanager));

		for (Sprite sprite : this.getSprites())
			sprite.prepareSprite(vertexBufferObjectmanager, scene);
	}
	
	public void setSpritesVisible(boolean visible) {
		for(Sprite sprite : this.getSprites())
			sprite.getSprite().setVisible(visible);
	}

	public void updateBackgroundPicture(org.andengine.entity.scene.Scene scene) {
		scene.setBackground(this.background);
		this.backgroundUpdated = true;
	}

	public boolean backgroundPictureUpdated() {
		return this.backgroundUpdated;
	}

	public void executeSceneAnimation(float elapsedTime, SoundManager soundManager, MusicManager musicManager, Resources resources) 
			throws AnimationExecutionException {
		boolean isInterrupted = false;
		int i = 0;
		while(i < this.getSprites().size() && !isInterrupted) {
			Sprite sprite = this.getSprites().get(i);
			if(!sprite.hasTerminatedOperationsToExecute()) {
				sprite.executeOperation(elapsedTime, soundManager, musicManager, resources);
			} else {
				if(!sprite.hasEndingBlockBeenExecuted()) {
					if(sprite.getBlocks().get(sprite.getBlocks().size() - 1).getOperationType() == OperationType.BROADCAST_MESSAGE) {
						broadcastMessage(sprite, i);
					} else if(sprite.getBlocks().get(sprite.getBlocks().size() - 1).getOperationType() == 
							OperationType.BROADCAST_MESSAGE_AND_WAIT) {
						broadcastMessage(sprite, i);
						//TODO
					} else if(sprite.getBlocks().get(sprite.getBlocks().size() - 1).getOperationType() == 
							OperationType.STOP_SCRIPT) {
						sprite.setEndingBlockExecuted();
					} else if(sprite.getBlocks().get(sprite.getBlocks().size() - 1).getOperationType() == 
							OperationType.STOP_ALL) {
						for (Sprite s : this.getSprites()) {
							s.setEndingBlockExecuted();
						}
						isInterrupted = true;
						this.sceneExecutionTerminated = true;
						this.backgroundUpdated = false;
					}
				}
			}
			i++;
		}
	}

	public boolean sceneExecutionTerminated() {
		if(this.sceneExecutionTerminated)
			return true;
		
		boolean terminated = true;
		int i = 0;
		while(i < this.sprites.size() && terminated) {
			Block endingBlock = this.sprites.get(i).getBlocks().get(this.sprites.get(i).getBlocks().size() - 1);
			if((!this.sprites.get(i).hasTerminatedOperationsToExecute()) || 
					(this.sprites.get(i).hasTerminatedOperationsToExecute() && endingBlock != null && 
					endingBlock.getBlockType() == BlockType.EVENT && !this.sprites.get(i).hasEndingBlockBeenExecuted())) {
				terminated = false;
			}
		}
		return terminated;
	}
	
	private void broadcastMessage(Sprite sprite, int i) {
		for (int j = 0; j < this.getSprites().size(); j++) {
			if(i != j && !this.getSprites().get(j).isActive() && this.getSprites().get(j).getBlocks().get(0)
					.getOperationType() == OperationType.WHEN_MESSAGE_RECEIVED) {
				String sentMessage = sprite.getBlocks().get(sprite.getBlocks().size() - 1)
						.getParameters().get(0).evaluateTextValue();
				String receivedMessage = this.getSprites().get(j).getBlocks().get(0).evaluateTextBlock();
				
				if(sentMessage.equals(receivedMessage)) {
					this.getSprites().get(j).activate();
				}
			}
		}
	}
}
