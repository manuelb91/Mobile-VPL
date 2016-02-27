package it.unibz.mobilevpl.object;

import it.unibz.mobilevpl.block.MotionBlock;
import it.unibz.mobilevpl.block.SoundBlock;
import it.unibz.mobilevpl.exception.AnimationExecutionException;
import it.unibz.mobilevpl.object.Block.BlockType;
import it.unibz.mobilevpl.object.Block.OperationType;
import it.unibz.mobilevpl.util.ContextManager;
import it.unibz.mobilevpl.util.FileManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import android.content.res.Resources;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Sprite extends SugarRecord<Sprite> implements Serializable {

	private Scene scene;
	private String picture;
	private int xPosition;
	private int yPosition;
	private int position; //Position of the sprite inside the list of sprites of the scene
	
	@Ignore private boolean active;
	
	@Ignore private List<Block> blocks;
	@Ignore private List<Music> stoppableMusicList;
	@Ignore private List<Music> allMusicList;
	
	@Ignore private boolean broadcastMessageSent;
	
	@Ignore private boolean waitingTimeSet;
	@Ignore private float waitingTime;

	@Ignore private MotionBlock motion;
	@Ignore private int index;
	@Ignore private boolean operationsTerminated;
	@Ignore private boolean endingBlockExecuted;
	@Ignore private boolean spriteClicked;

	@Ignore private org.andengine.entity.sprite.Sprite sprite;
	@Ignore private ITexture texture;
	@Ignore private ITextureRegion textureRegion;

	public Sprite() {
	}
	
	public Sprite(int x, int y) {
		this();
		this.xPosition = x;
		this.yPosition = y;
	}

	public Sprite(Scene scene, String picture, int position) {
		this();
		this.scene = scene;
		this.picture = picture;
		this.position = position;
	}

	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getxPosition() {
		return xPosition;
	}

	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getyPosition() {
		return yPosition;
	}

	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}

	private Sprite getSpriteFromSameScene(int index) {
		return Sprite.find(Sprite.class, "scene = ?", new String[] {String.valueOf(this.getScene().getId())}, null, "position", null)
				.get(index);
	}

	public List<Block> getBlocks(){
		if(this.blocks == null)
			this.blocks = Block.find(Block.class, "sprite = ?", new String[] {String.valueOf(this.getId())}, null, "position", null);
		return this.blocks;
	}
	
	public boolean isEmpty() {
		return (this.getBlocks() == null) || (this.getBlocks().size() == 0);
	}
	
	public void addBlock(Block block) {
		block.setSprite(this);
		this.getBlocks().add(block);
		this.storeAll();
	}

	public void store() {
		this.save();
	}

	public void storeAll() {
		this.store();
		for (int i = 0; i < this.getBlocks().size(); i++) {
			Block block = this.getBlocks().get(i);
			block.setPosition(i + 1);
			block.storeAll();
		}
	}
	
	public void remove() {
		removeBlocks();
		if(this.picture != null && this.picture.length() > 0)
			FileManager.removeFile(this.picture);
		this.delete();
	}
	
	public void removeBlocks() {
		for (int i = 0; i < this.getBlocks().size(); i++) {
			Block block = this.getBlocks().get(i);
			block.setSprite(null);
			block.store();
			block.delete();
		}
		this.getBlocks().clear();
	}

	public void prepare() {
		this.index = 0;
		this.active = false;
		this.operationsTerminated = false;
		this.endingBlockExecuted = false;
		this.motion = new MotionBlock(this.sprite);
		this.waitingTimeSet = false;
		this.waitingTime = 0f;
		this.broadcastMessageSent = false;
		this.spriteClicked = false;
	}

	public void prepareAll() {
		this.prepare();
		for (Block block : this.getBlocks()) {
			block.prepareAll();
		}
	}

	public void prepareResource(TextureManager textureManager) {
		try {
			this.texture = new BitmapTexture(textureManager, new IInputStreamOpener() {
				@Override
				public InputStream open() throws IOException {
					return new FileInputStream(picture);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.textureRegion = TextureRegionFactory.extractFromTexture(this.texture);
		this.texture.load();
	}

	public void prepareSprite(VertexBufferObjectManager vertexBufferObjectmanager, org.andengine.entity.scene.Scene scene) {
		this.sprite = new org.andengine.entity.sprite.Sprite(ContextManager.convertXCoordinateToSystemFormat(this.xPosition), 
				ContextManager.convertYCoordinateToSystemFormat(this.yPosition), this.textureRegion, vertexBufferObjectmanager)
		{
			@Override
		    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float X, float Y) 
		    {
		        if (pSceneTouchEvent.isActionUp())
		        {
		            Sprite.this.spriteClicked = true;
		        }
		        return true;
		    };
		};
		scene.registerTouchArea(sprite);
		scene.attachChild(this.sprite);
		this.sprite.setVisible(false);
	}

	public void executeOperation(float elapsedTime, SoundManager soundManager, MusicManager musicManager, Resources resources) 
			throws AnimationExecutionException {
		this.motion.setElapedTimeInterval(elapsedTime);
		if(this.index < this.getBlocks().size() && !this.operationsTerminated) {
			Block block = this.getBlocks().get(this.index);
			List<Parameter> parameters = block.getParameters();

			if(block.getBlockType() == Block.BlockType.CONTROL) {

			} else if(block.getBlockType() == Block.BlockType.DATA) {
				
			} else if(block.getBlockType() == Block.BlockType.EVENT) {
				if(block.getOperationType() == Block.OperationType.WHEN_FALG_PRESSED) {
					this.activate();
					this.index++;
				} else if(block.getOperationType() == Block.OperationType.WHEN_SPRITE_CLICKED) {
					float xStart = this.sprite.getX() - (this.sprite.getWidth()/2);
					float xStop = this.sprite.getX() + (this.sprite.getWidth()/2);
					float yStart = this.sprite.getY() - (this.sprite.getHeight()/2);
					float yStop = this.sprite.getY() + (this.sprite.getHeight()/2);
					
					if(ContextManager.X_POSITION >= xStart && ContextManager.X_POSITION <= xStop && 
							ContextManager.Y_POSITION >= yStart && ContextManager.Y_POSITION <= yStop) {
						this.activate();
						this.index++;
					}
					
					if(this.spriteClicked) {
						this.activate();
						this.index++;
						this.spriteClicked = false;
					}
				} else if(block.getOperationType() == Block.OperationType.WAIT_FOR_SECONDS) {
					if(!this.waitingTimeSet) {
						this.waitingTime = parameters.get(0).evaluateNumericValue();
						this.waitingTimeSet = true;
					}
					if(this.waitingTime <= 0) {
						this.waitingTimeSet = false;
						this.index++;
					} else {
						this.waitingTime -= elapsedTime;
					}
				} else if(block.getOperationType() == Block.OperationType.REPEAT_N_TIMES) {
					
				} else if(block.getOperationType() == Block.OperationType.REPEAT_FOREVER) {
					
				} else if(block.getOperationType() == Block.OperationType.BROADCAST_MESSAGE) {
					this.activateOtherSceneSpritesWithMessage(parameters.get(0).evaluateTextValue());
					this.index++;
				} else if(block.getOperationType() == Block.OperationType.BROADCAST_MESSAGE_AND_WAIT) {
					if(!this.broadcastMessageSent) {
						this.activateOtherSceneSpritesWithMessage(parameters.get(0).evaluateTextValue());
						this.broadcastMessageSent = true;
					}
					if(!this.waitingTimeSet) {
						this.waitingTime = parameters.get(0).evaluateNumericValue();
						this.waitingTimeSet = true;
					}
					if(this.waitingTime <= 0) {
						this.waitingTimeSet = false;
						this.index++;
						this.broadcastMessageSent = false;
					} else {
						this.waitingTime -= elapsedTime;
					}
				} else if(block.getOperationType() == Block.OperationType.WHEN_MESSAGE_RECEIVED) {
					//Empty since sprite is activated when message is sent
				} else if(block.getOperationType() == Block.OperationType.STOP_SCRIPT) {
					this.operationsTerminated = true;
				} else if(block.getOperationType() == Block.OperationType.STOP_ALL) {
					this.operationsTerminated = true;
					ContextManager.TERMINATE = true;
				}
			} else if(block.getBlockType() == Block.BlockType.LOOK) {
				
			} else if(block.getBlockType() == Block.BlockType.MOTION) {
				if(this.motion.isArrived()) {
					this.motion.setArrived(false);
					if(block.getOperationType() == Block.OperationType.MOVEMENT) {
						this.motion.moveSprite(parameters.get(0).evaluateNumericValue(), parameters.get(1).evaluateNumericValue());
					} else if(block.getOperationType() == Block.OperationType.TURN_CLOCKWISE) {
						this.motion.turnDegrees(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.TURN_COUNTER_CLOCKWISE) {
						this.motion.turnDegrees(parameters.get(0).getNumericValue() * -1);
					} else if(block.getOperationType() == Block.OperationType.POINT_DIRECTION) {
						this.motion.pointDirection(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.POINT_TOWARDS_TOUCH) {
						this.motion.moveSprite(ContextManager.X_POSITION, ContextManager.Y_POSITION);
					} else if(block.getOperationType() == Block.OperationType.POINT_TOWARDS_SPRITE) {
						this.motion.pointDirection(parameters.get(0).getSprite().getSprite());
					} else if(block.getOperationType() == Block.OperationType.GO_TO_XY) {
						this.motion.moveSprite(parameters.get(0).getNumericValue(), parameters.get(1).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.GO_TO) {
						int spriteIndex = (int)parameters.get(0).evaluateNumericValue();
						if (spriteIndex == -1) {
							this.motion.moveSprite(ContextManager.X_POSITION, ContextManager.Y_POSITION);
						} else {
							this.motion.goTo(this.getSpriteFromSameScene(spriteIndex).getSprite());
						}
					} else if(block.getOperationType() == Block.OperationType.GLIDE_TO_XY_SECONDS) {
						this.motion.glideToXYSeconds(parameters.get(0).evaluateNumericValue(), parameters.get(1).evaluateNumericValue(), 
								parameters.get(2).evaluateNumericValue());
					} else if(block.getOperationType() == Block.OperationType.CHANGE_X) {
						this.motion.changeXBy(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.SET_X) {
						this.motion.setXTo(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.CHANGE_Y) {
						this.motion.changeYBy(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.SET_Y) {
						this.motion.setYTo(parameters.get(0).getNumericValue());
					} else if(block.getOperationType() == Block.OperationType.BOUNCE_IF_ON_EDGE) {
						this.motion.bounceIfOnEdge();
					} else if(block.getOperationType() == Block.OperationType.ROTATION_STYLE) {

					}
				} else {
					this.motion.execute();
					if(this.motion.isArrived())
						this.index++;
				}
			} else if(block.getBlockType() == Block.BlockType.PEN) {

			} else if(block.getBlockType() == Block.BlockType.SENSING) {

			} else if(block.getBlockType() == Block.BlockType.SOUND) {
				if(block.getOperationType() == Block.OperationType.PLAY_SOUND) {
					if(this.stoppableMusicList == null)
						this.stoppableMusicList = new LinkedList<Music>();
					this.stoppableMusicList.add(SoundBlock.playSound(musicManager, parameters.get(0).evaluateTextValue()));
					
					if(this.allMusicList == null)
						this.allMusicList = new LinkedList<Music>();
					this.allMusicList.add(SoundBlock.playSound(musicManager, parameters.get(0).evaluateTextValue()));
				} else if(block.getOperationType() == Block.OperationType.PLAY_SOUND_UNTIL_DONE) {
					//The music file is not added to the list since it has not to be stopped
					if(this.allMusicList == null)
						this.allMusicList = new LinkedList<Music>();
					this.allMusicList.add(SoundBlock.playSound(musicManager, parameters.get(0).evaluateTextValue()));
				} else if(block.getOperationType() == Block.OperationType.STOP_ALL_SOUNDS) {
					SoundBlock.stopSound(this.stoppableMusicList);
				} else if(block.getOperationType() == Block.OperationType.CHANGE_VOLUME_BY) {
					SoundBlock.setVolumeToPercentage(musicManager, soundManager, parameters.get(0).evaluateNumericValue());
				} else if(block.getOperationType() == Block.OperationType.SET_VOLUME_TO_PERCENTAGE) {
					SoundBlock.changeVolumeBy(musicManager, soundManager, parameters.get(0).evaluateNumericValue());
				}
			}
		} else {
			this.operationsTerminated = true;
			SoundBlock.stopSound(this.allMusicList);
		}
	}
	
	public void activate() {
		this.active = true;
	}
	
	public void setEndingBlockExecuted() {
		this.endingBlockExecuted = true;
	}
	
	public boolean isActive() {
		return this.active;
	}

	public boolean hasTerminatedOperationsToExecute() {
		return this.operationsTerminated;
	}
	
	public boolean hasEndingBlockBeenExecuted() {
		return this.endingBlockExecuted;
	}
	
	public org.andengine.entity.sprite.Sprite getSprite() {
		return this.sprite;
	}
	
	private void activateOtherSceneSpritesWithMessage(String message) {
		List<Sprite> otherSprites = 
				Sprite.find(Sprite.class, "scene = ?", new String[] {String.valueOf(this.getScene().getId())}, null, "position", null);
		for(Sprite sprite : otherSprites) {
			if(sprite.getId() != this.getId()) {
				sprite.activateSpriteWithMessage(message);
			}
		}
	}
	
	public void activateSpriteWithMessage(String message) {
		if(!this.isActive() && this.getBlocks().get(0).getBlockType() == BlockType.EVENT && 
				this.getBlocks().get(0).getOperationType() == OperationType.WHEN_MESSAGE_RECEIVED) {
			String activationMessage = this.getBlocks().get(0).getParameters().get(0).evaluateTextValue().trim().toUpperCase();
			if(activationMessage.equals(message.trim().toUpperCase())) {
				this.activate();
				this.index++;
			}
		}
	}
}
