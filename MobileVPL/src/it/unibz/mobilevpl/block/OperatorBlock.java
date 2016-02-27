package it.unibz.mobilevpl.block;

import java.util.Random;

public class OperatorBlock {
	
	private final static Random random = new Random();

	public static boolean isLessThan(float a, float b) {
		return a < b;
	}
	
	public static boolean isGreaterThan(float a, float b) {
		return a > b;
	}
	
	public static boolean isEqualTo(float a, float b) {
		return a == b;
	}
	
	public static boolean conditionAnd(boolean conditionA, boolean conditionB) {
		return conditionA && conditionB;
	}
	
	public static boolean conditionOr(boolean conditionA, boolean conditionB) {
		return conditionA || conditionB;
	}
	
	public static boolean conditionNot(boolean condition) {
		return !condition;
	}
	
	public static float addition(float a, float b) {
		return a + b;
	}
	
	public static float subtraction(float a, float b) {
		return a - b;
	}
	
	public static float multiplication(float a, float b) {
		return a * b;
	}
	
	public static float division(float a, float b) {
		return a / b;
	}
	
	public static float randomInteger(int min, int max) {
		return random.nextInt((max - min) + 1) + min;
	}
	
	public static float randomFloating(float min, float max) {
		return (float)((Math.random() * ((max - min) + 1)) + min);
	}
	
	public static String joinText(String a, String b) {
		return a + b;
	}
	
	public static String joinText(String a, float b) {
		return a + b;
	}
	
	public static String joinText(float a, String b) {
		return "" + a + b;
	}
	
	public static String joinText(float a, float b) {
		return "" + a + b;
	}
	
	public static float joinNumber(String a, String b) {
		return Float.parseFloat(joinText(a, b));
	}
	
	public static float joinNumber(float a, String b) {
		return Float.parseFloat(joinText(a, b));
	}
	
	public static float joinNumber(String a, float b) {
		return Float.parseFloat(joinText(a, b));
	}
	
	public static float joinNumber(float a, float b) {
		return Float.parseFloat(joinText(a, b));
	}
	
	public static String letterOfText(int position, String text) {
		return "" + text.charAt(position);
	}
	
	public static float letterOfNumber(int position, String text) {
		return Float.parseFloat(letterOfText(position, text));
	}
	
	public static float lengthOf(String text) {
		return text.length();
	}
	
	public static float lengthOf(float number) {
		return String.valueOf(number).length();
	}
	
	public static float lengthOf(int number) {
		return String.valueOf(number).length();
	}
	
	public static float modulo(float a, float b) {
		return a % b;
	}
	
	public static float round(float a) {
		return Math.round(a);
	}
	
	public static float abs(float a) {
		return Math.abs(a);
	}
	
	public static float sqrt(float a) {
		return (float)Math.sqrt(a);
	}
	
	public static float sin(float a) {
		return (float)Math.sin(a);
	}
	
	public static float cos(float a) {
		return (float)Math.cos(a);
	}
	
	public static float tan(float a) {
		return (float)Math.tan(a);
	}
	
	public static float asin(float a) {
		return (float)Math.asin(a);
	}
	
	public static float acos(float a) {
		return (float)Math.acos(a);
	}
	
	public static float atan(float a) {
		return (float)Math.atan(a);
	}
	
	public static float ln(float a) {
		return (float)Math.log(a);
	}
	
	public static float log(float a) {
		return (float)Math.log10(a);
	}
	
	public static float e(float a) {
		return (float)Math.pow(Math.E, a);
	}
	
	public static float pow10(float a) {
		return (float)Math.pow(10, a);
	}
	
	public static float floor(float a) {
		return (float)Math.floor(a);
	}
	
	public static float ceil(float a) {
		return (float)Math.ceil(a);
	}
}
