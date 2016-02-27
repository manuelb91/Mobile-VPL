package it.unibz.mobilevpl.block;

import org.andengine.entity.sprite.Sprite;

public class LookBlock {

	public static void show(Sprite sprite) {
		sprite.setVisible(true);
	}
	
	public static void hide(Sprite sprite) {
		sprite.setVisible(false);
	}
	
	public static void changeSizeBy(Sprite sprite, float size) {
		sprite.setScale(1 + (size / 100));
	}
	
	public static void setSizeTo(Sprite sprite, float size) {
		sprite.setScale(size / 100);
	}
	
	public static void goToFront(Sprite sprite) {
		sprite.setZIndex(0);
	}
	
	public static void goOneTireBack(Sprite sprite) {
		sprite.setZIndex(sprite.getZIndex() + 1);
	}
}
