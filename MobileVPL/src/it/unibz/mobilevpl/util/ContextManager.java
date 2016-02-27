package it.unibz.mobilevpl.util;

import android.util.Log;

public class ContextManager {
	
	public static final int MIN = -100;
	public static final int MAX = 100;
	
	public static int WIDTH;
	public static int HEIGHT;
	
	public static float X_POSITION;
	public static float Y_POSITION;
	public static boolean TERMINATE;
	
	public static float convertXCoordinateToUserFormat(float xCoordinate) {
		return (((xCoordinate / (WIDTH)) * (MAX - MIN)) - ((MAX - MIN) / 2));
	}
	
	public static float convertYCoordinateToUserFormat(float yCoordinate) {
//		return -(((yCoordinate / (HEIGHT * 2)) * (MAX - MIN)) - ((MAX - MIN) / 2));
		return (((yCoordinate / (HEIGHT)) * (MAX - MIN)) - ((MAX - MIN) / 2));
	}
	
	public static float convertXCoordinateToSystemFormat(float xCoordinate) {
		return (((xCoordinate + ((MAX - MIN)/2)) / (MAX - MIN)) * (WIDTH));
	}
	
	public static float convertYCoordinateToSystemFormat(float yCoordinate) {
//		return (((-yCoordinate + (MAX - MIN)) / (MAX - MIN)/2)) * (HEIGHT*2));
		return (((yCoordinate + ((MAX - MIN)/2)) / (MAX - MIN)) * (HEIGHT));
	}
}
