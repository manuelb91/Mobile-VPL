package it.unibz.mobilevpl.block;

import it.unibz.mobilevpl.util.ContextManager;

import org.andengine.entity.sprite.Sprite;

import android.util.Log;


public class MotionBlock {

	private enum MotionType { Movement_Position, Movement_Steps, Rotation, Bounce }
	private static final int MOVEMENT_STEP = 1;
	private static final int ROTATION_STEP = 1;
	
	private Sprite sprite;
	private MotionType type;
	
	private float x_step;
	private float y_step;
	private float steps;
	private float x_position;
	private float y_position;
	
	private float elapedTimeInterval;
	
	private float rotation_step;
	private float degrees;
	
	private Sprite goalSprite;

	private boolean arrived;
	
	public MotionBlock(Sprite sprite) {
		this.sprite = sprite;
		
		this.arrived = true;
	}
	
	public void moveSprite(float x_position, float y_position) {
		x_position = ContextManager.convertXCoordinateToSystemFormat(x_position);
		y_position = ContextManager.convertYCoordinateToSystemFormat(y_position);
		float x_difference = Math.abs(sprite.getX() - x_position);
		float y_difference = Math.abs(sprite.getY() - y_position);
		
		float x_step = 0;
		float y_step = 0;
		
		if(x_difference > 0 && y_difference > 0) {
			float difference = y_difference / x_difference;
			x_step = (sprite.getX() <= x_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
			y_step = (sprite.getY() <= y_position) ? (difference * MOVEMENT_STEP) : -(difference * MOVEMENT_STEP);
		} else if(x_difference == 0 && y_difference > 0) {
			x_step = 0;
			y_step = (sprite.getY() <= y_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		} else if(x_difference > 0 && y_difference == 0) {
			y_step = 0;
			x_step = (sprite.getX() <= x_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		}
		
		this.setMotionParameters(x_step, y_step, x_position, y_position);
	}
	
	/**
	 * The sprite is turned by the amount of degrees passed as parameters.
	 * In the case in which the degrees are positive, the sprite is turned clockwise,
	 * otherwise the sprite is turned counter-clockwise.
	 * @param sprite The sprite that has to be turned by the amount of degrees
	 * @param degrees The amount of degrees to turn the sprite, 
	 * 					positive to turn clockwise, negative to turn counter-clockwise.
	 */
	public void turnDegrees(float degrees) {
		float final_rotation = (sprite.getRotation() + degrees) % 360;
			
		if(final_rotation < 0)
			final_rotation = 360 + final_rotation;
		
		float sprite_step = (degrees > 0) ? ROTATION_STEP : -ROTATION_STEP;
		
		this.setMotionParameters(final_rotation, sprite_step);
	}
	
	/**
	 * The sprite is rotated in a way that the degrees correspond to the passed ones.
	 * @param sprite The sprite to rotate in the desired degrees.
	 * @param degrees The degrees in which the sprite has to be rotated.
	 */
	public void pointDirection(float degrees) {
		float sprite_step = (degrees > sprite.getRotation()) ? ROTATION_STEP : -ROTATION_STEP;
		
		this.setMotionParameters(degrees, sprite_step);
	}
	
	public void pointDirection(Sprite spriteGoal) {
		float x_difference = Math.abs(sprite.getX() - spriteGoal.getX());
		float y_difference = Math.abs(sprite.getY() - spriteGoal.getY());
		float rotation_difference = 0;
		
		float degrees = 0;
		
		if(x_difference > 0 && y_difference > 0)
			rotation_difference = y_difference / x_difference;
		
		if(sprite.getX() < spriteGoal.getX()) {
			if(sprite.getY() < spriteGoal.getY()) {
				degrees = 90 - rotation_difference;
			} else {
				degrees = 90 + rotation_difference;
			}
		} else {
			if(sprite.getY() < spriteGoal.getY()) {
				degrees = 270 + rotation_difference;
			} else {
				degrees = 270 - rotation_difference;
			}
		}
		
		pointDirection(degrees);
	}
	
	public void moveForward(float steps) {
		float x_step = 0;
		float y_step = 0;
		
		if(sprite.getRotation() % 90 == 0) {
			if(sprite.getRotation() == 0)
				y_step = MOVEMENT_STEP;
			if(sprite.getRotation() == 180)
				y_step = -MOVEMENT_STEP;
			if(sprite.getRotation() == 90)
				x_step = MOVEMENT_STEP;
			if(sprite.getRotation() == 270)
				x_step = -MOVEMENT_STEP;
		} else {
			float degrees = 2 - ((sprite.getRotation() % 90) / 45);
			
			x_step = (sprite.getRotation() > 0 && sprite.getRotation() < 180) ? MOVEMENT_STEP : -MOVEMENT_STEP;
			y_step = (sprite.getRotation() < 360 && sprite.getRotation() > 180) ? (degrees * MOVEMENT_STEP) : -(degrees * MOVEMENT_STEP);
		}
		
		this.setMotionParameters(x_step, y_step, steps);
	}
	
	public void goTo(Sprite spriteGoal) {
		float x_position = spriteGoal.getX();
		float y_position = spriteGoal.getY();
		
		float x_difference = Math.abs(sprite.getX() - x_position);
		float y_difference = Math.abs(sprite.getY() - y_position);
		
		float x_step = 0;
		float y_step = 0;
		
		if(x_difference > 0 && y_difference > 0) {
			float difference = y_difference / x_difference;
			x_step = (sprite.getX() <= x_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
			y_step = (sprite.getY() <= y_position) ? (difference * MOVEMENT_STEP) : -(difference * MOVEMENT_STEP);
		} else if(x_difference == 0 && y_difference > 0) {
			x_step = 0;
			y_step = (sprite.getY() <= y_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		} else if(x_difference > 0 && y_difference == 0) {
			y_step = 0;
			x_step = (sprite.getX() <= x_position) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		}
		
		this.setMotionParameters(x_step, y_step, spriteGoal);
	}
	
	public void glideToXYSeconds(float x_position, float y_position, float time) {
		x_position = ContextManager.convertXCoordinateToSystemFormat(x_position);
		y_position = ContextManager.convertYCoordinateToSystemFormat(y_position);
		float x_difference = Math.abs(sprite.getX() - x_position);
		float y_difference = Math.abs(sprite.getY() - y_position);
		float steps = time /((float) (Math.round(this.elapedTimeInterval * 100.0) / 100.0));
		
		float move_x = x_difference / steps;
		float move_y = y_difference / steps;
		
		float x_step = 0;
		float y_step = 0;
		
		if(x_difference > 0) 
			x_step = (sprite.getX() <= x_position) ? move_x : -move_x;
		if(y_difference > 0) 
			y_step = (sprite.getY() <= y_position) ? move_y : -move_y;
		
		this.setMotionParameters(x_step, y_step, x_difference, y_difference);
	}
	
	public void changeXBy(float x) {
		float x_step = (x > 0) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		this.setMotionParameters(x_step, 0, ContextManager.convertXCoordinateToSystemFormat(x) - ContextManager.WIDTH);
	}
	
	public void setXTo(float x) {
		float x_step = (sprite.getX() < ContextManager.convertXCoordinateToSystemFormat(x)) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		this.setMotionParameters(x_step, 0, ContextManager.convertXCoordinateToSystemFormat(x), sprite.getY());
	}
	
	public void changeYBy(float y) {
		float y_step = (y > 0) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		this.setMotionParameters(0, y_step, ContextManager.HEIGHT - ContextManager.convertYCoordinateToSystemFormat(y));
	}
	
	public void setYTo(float y) {
		float y_step = (sprite.getY() < ContextManager.convertYCoordinateToSystemFormat(y)) ? MOVEMENT_STEP : -MOVEMENT_STEP;
		this.setMotionParameters(0, y_step, sprite.getX(), ContextManager.convertYCoordinateToSystemFormat(y));
	}
	
	public void bounceIfOnEdge() {
		this.type = MotionType.Bounce;
		
		float width = this.sprite.getWidth()/2;
		float height = this.sprite.getHeight()/2;
		float xPosition = this.sprite.getX();
		float yPosition = this.sprite.getY();
		
		//TODO
		
		this.arrived = true;
	}
	
	private void setMotionParameters(float x_step, float y_step, float x_position, float y_position) {
		this.type = MotionType.Movement_Position;
		this.x_step = x_step;
		this.y_step = y_step;
		this.x_position = x_position;
		this.y_position = y_position;
	}
	
	private void setMotionParameters(float x_step, float y_step, float steps) {
		this.type = MotionType.Movement_Steps;
		this.x_step = x_step;
		this.y_step = y_step;
		this.steps = steps;
	}
	
	private void setMotionParameters(float degrees, float rotation_step) {
		this.type = MotionType.Rotation;
		this.degrees = degrees;
	}
	
	private void setMotionParameters(float x_step, float y_step, Sprite goalSprite) {
		this.type = MotionType.Movement_Position;
		this.x_step = x_step;
		this.y_step = y_step;
		this.x_position = goalSprite.getX();
		this.y_position = goalSprite.getY();
		this.goalSprite = goalSprite;
	}
	
	public boolean isArrived() {
		return this.arrived;
	}
	
	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
	
	public void execute() {
		if(this.type == MotionType.Movement_Position) { //Movement to position
			float x_difference = (this.sprite.getX() > this.x_position) ? (this.sprite.getX() - this.x_position) : 
				(this.x_position - this.sprite.getX());
			float y_difference = (this.sprite.getY() > this.y_position) ? (this.sprite.getY() - this.y_position) : 
				(this.y_position - this.sprite.getY());
			if(x_difference < 1 && y_difference < 1) {
				this.arrived = true;
			} else {
				this.sprite.setPosition(this.sprite.getX() + this.x_step, this.sprite.getY() + this.y_step);
				
				if(this.goalSprite != null && this.goalSprite.isVisible() && this.sprite.collidesWith(this.goalSprite)) {
					this.sprite.setPosition(this.sprite.getX() - this.x_step, this.sprite.getY() - this.y_step);
					this.arrived = true;
				}
			}
		} else if(this.type == MotionType.Movement_Steps) { //Movement in steps
			if(this.steps <= 0f) {
				this.arrived = true;
			} else {
				this.sprite.setPosition(this.sprite.getX() + this.x_step, this.sprite.getY() + this.y_step);
			}
		} else if(this.type == MotionType.Rotation) { //Rotation
			float rotation_difference = Math.abs(this.sprite.getRotation() - this.degrees);
			if(rotation_difference < 1)
				this.arrived = true;
			else
				this.sprite.setRotation(this.sprite.getRotation() + this.rotation_step);
		} else if(this.type == MotionType.Bounce) { //Bounce
			float rotation_difference = 0;
			
			if(this.sprite.getX() <= this.sprite.getWidth()/2) {
				rotation_difference = Math.abs(270 - this.sprite.getRotation());
			} else if(this.sprite.getX() >= ContextManager.WIDTH - this.sprite.getWidth()/2) {
				rotation_difference = Math.abs(90 - this.sprite.getRotation());
			}
			if(this.sprite.getY() <= this.sprite.getHeight()/2) {
				rotation_difference = Math.abs(this.sprite.getRotation());
			} else if(this.sprite.getY() >= ContextManager.HEIGHT - this.sprite.getHeight()/2) {
				rotation_difference = Math.abs(180 - this.sprite.getRotation());
			}
			
			float rotation =  (180 - (rotation_difference * 2)) % 360;
			
			this.sprite.setRotation(rotation);
		}
	}
	
	public void setElapedTimeInterval(float elapedTimeInterval) {
		this.elapedTimeInterval = elapedTimeInterval;
	}
	
	public void reset() {
		this.sprite = null;
		this.type = null;
		this.x_step = 0f;
		this.y_step = 0f;
		this.steps = 0f;
		this.x_position = 0f;
		this.y_position = 0f;
		this.degrees = 0f;
		this.arrived = false;
		this.goalSprite = null;
	}
}
