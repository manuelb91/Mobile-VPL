package it.unibz.mobilevpl.util;

import it.unibz.mobilevpl.object.Block;
import it.unibz.mobilevpl.object.Parameter;
import it.unibz.mobilevpl.object.Project;
import it.unibz.mobilevpl.object.Sprite;
import it.unibz.mobilevpl.object.Block.BlockType;
import it.unibz.mobilevpl.object.Parameter.ParameterType;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class XMLManager {

	private static final String BLOCK = "block";
	private static final String BLOCK_ID = "id";
	private static final String BLOCK_X_POSITION = "x";
	private static final String BLOCK_Y_POSITION = "y";
	private static final String BLOCK_TYPE = "type";
	private static final String FIELD = "field";
	private static final String NEXT = "next";
	private static final String VALUE = "value";
	private static final String NAME = "name";
	private static final String NUMERIC_VALUE = "NUM";
	private static final String TRUTH_VALUE = "";
	private static final String TEXT_VALUE = "";
	private static final String OBJECT = "object";

	private static final String PLACEHOLDER = "<PLACEHOLDER>";
	private static final String XML_TEMPLATE = "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><PLACEHOLDER></xml>";
	private static final String BLOCK_TEMPLATE = "<block type=\"<PLACEHOLDER>\" id=\"<PLACEHOLDER>\"><PLACEHOLDER></block>";
	private static final String BLOCK_TEMPLATE_SIMPLE = "<block type=\"<PLACEHOLDER>\"><PLACEHOLDER></block>";
	private static final String BLOCK_TEMPLATE_EXTENDED = 
			"<block type=\"<PLACEHOLDER>\" id=\"<PLACEHOLDER>\" x=\"<PLACEHOLDER>\" y=\"<PLACEHOLDER>\"><PLACEHOLDER></block>";
	private static final String NUMERIC_VALUE_TEMPLATE = 
			"<value name=\"<PLACEHOLDER>\"><block type=\"math_number\" id=\"<PLACEHOLDER>\"><field name=\"NUM\"><PLACEHOLDER></field></block></value>";
	private static final String TRUTH_VALUE_TEMPLATE = 
			"<value name=\"<PLACEHOLDER>\"><block type=\"logic_boolean\" id=\"<PLACEHOLDER>\"><field name=\"BOOL\"><PLACEHOLDER></field></block></value>";
	private static final String TEXT_VALUE_TEMPLATE = 
			"<value name=\"<PLACEHOLDER>\"><block type=\"text\" id=\"<PLACEHOLDER>\"><field name=\"TEXT\"><PLACEHOLDER></field></block></value>";
	private static final String FIELD_TEMPLATE = "<field name=\"<PLACEHOLDER>\"><PLACEHOLDER></field>";
	private static final String NEXT_TEMPLATE = "<next><PLACEHOLDER></next>";

	public static void parseXML(Project project, int selectedScene, int selectedSprite, String xml) 
			throws XmlPullParserException, IOException {
		
		Log.d("xml", "parseXML(" + selectedScene + "|" + selectedSprite +"): " + xml);
		
		Sprite sprite = project.getScenes().get(selectedScene).getSprites().get(selectedSprite);
		sprite.removeBlocks();

		XmlPullParserFactory xmlPullParserFactory;
		XmlPullParser xmlPullParser;

		xmlPullParserFactory = XmlPullParserFactory.newInstance();
		xmlPullParser = xmlPullParserFactory.newPullParser();
		xmlPullParser.setInput(new StringReader(xml));

		Block block = null;
		Parameter parameter = null;
		
		boolean lastBlockReached = false;
		boolean isObject = false;

		int next = xmlPullParser.next();
		while(next != XmlPullParser.END_DOCUMENT) {
			String name = xmlPullParser.getName();

			if(next == XmlPullParser.START_TAG) {
				if(name.equals(BLOCK)) {
					int id = Integer.parseInt(xmlPullParser.getAttributeValue(null, BLOCK_ID));
					String value = xmlPullParser.getAttributeValue(null, BLOCK_TYPE);

					if(parameter == null) {
						block = new Block(id, value);

						String xPosition = xmlPullParser.getAttributeValue(null, BLOCK_X_POSITION);
						String yPosition = xmlPullParser.getAttributeValue(null, BLOCK_Y_POSITION);
						if(xPosition != null && yPosition != null) {
							block.setxPosition(Integer.parseInt(xPosition));
							block.setyPosition(Integer.parseInt(yPosition));
						}
					} else {
						//parameter.setName(value);
						parameter.setParameterId(id);
					}
				} else if(name.equals(FIELD)) {
					String description = xmlPullParser.getAttributeValue(null, NAME);
					if (description.equals(OBJECT))
						isObject = true;
					
					if(parameter == null) {
						parameter = new Parameter();
						parameter.setName(description);
					} else {
						if(description.equals(NUMERIC_VALUE)) {
							parameter.setParameterType(ParameterType.NUMBER);
						} else if(description.equals(TRUTH_VALUE)) {
							parameter.setParameterType(ParameterType.TRUTHVALUE);
						} else if(description.equals(TEXT_VALUE)) {
							parameter.setParameterType(ParameterType.TEXT);
						}
					}
				} else if(name.equals(NEXT)) {
					sprite.addBlock(block);
					block = null;
				} else if(name.equals(VALUE)) {
					String description = xmlPullParser.getAttributeValue(null, NAME);

					parameter = new Parameter();
					parameter.setName(description);
				}
			} else if(next == XmlPullParser.END_TAG) {
//				System.out.println("ENDTAG: " + name);
//				if(name.equals(BLOCK)) {
//					if(block != null) {
//						sprite.addBlock(block);
//						block = null;
//					}
//				}
				if(name.equals(NEXT) && !lastBlockReached) {
					lastBlockReached = true;
					sprite.addBlock(block);
					block = null;
				}
			} else if(next == XmlPullParser.TEXT) {
				String text = xmlPullParser.getText();
				
				if(parameter.getParameterType() == null && !isObject) {
					try {
						float number = Float.parseFloat(text);
						parameter.setParameterType(ParameterType.NUMBER);
						parameter.setNumericValue(number);
					} catch(NumberFormatException e) {
						try {
							boolean truthValue = Boolean.parseBoolean(text.toLowerCase());
							parameter.setParameterType(ParameterType.TRUTHVALUE);
							parameter.setTruthvalue(truthValue);
						} catch(Exception e1) {
							parameter.setParameterType(ParameterType.TEXT);
							parameter.setTextValue(text);
						}
					}
				} else {
					if(parameter.getParameterType() == ParameterType.NUMBER) {
						parameter.setNumericValue(Float.parseFloat(text));
					} else if(parameter.getParameterType() == ParameterType.TRUTHVALUE) {
						parameter.setTruthvalue(Boolean.parseBoolean(text));
					} else if(parameter.getParameterType() == ParameterType.TEXT) {
						parameter.setTextValue(text);
					} else if(isObject) {
						if (block.getBlockType() == BlockType.SOUND) {
							parameter.setParameterType(ParameterType.SOUND);
							parameter.setNumericValue(Float.parseFloat(text));
						} else if (block.getBlockType() == BlockType.MOTION) {
							int index = Integer.parseInt(text);
							parameter.setParameterType((index == -1) ? ParameterType.TOUCH : ParameterType.SPRITE);
							parameter.setNumericValue(Float.parseFloat(text));
						}
						isObject = false;
					}
					
				}

				block.addParameter(parameter);
				parameter = null;
			}

			next = xmlPullParser.next();
		}
		
		sprite.storeAll();
		
		for(Block b : sprite.getBlocks()) {
			Log.d("struct", b.getName() + ": " + b.getParameters().size());
			if(b.getParameters().size() > 0) {
				for(Parameter p : b.getParameters()) {
					Log.d("struct", p.getName());
				}
			}
		}
	}

	public static String generateXML(Project project, int selectedScene, int selectedSprite) {
		String xml = XML_TEMPLATE;

		Sprite sprite = project.getScenes().get(selectedScene).getSprites().get(selectedSprite);
		Log.d("struct", "Blocks: " + sprite.getBlocks().size());
		for (int i = 0; i < sprite.getBlocks().size(); i++) {
			Block block = sprite.getBlocks().get(i);
			Log.d("struct", "" + i + " | " + block.getName() + ": " + block.getParameters().size());
			block.prepareAll();
			String blockString;
			if(block.getxPosition() != -1 && block.getyPosition() != -1) {
				blockString = BLOCK_TEMPLATE_EXTENDED.replaceFirst(PLACEHOLDER, block.getOperationType().getValue())
						.replaceFirst(PLACEHOLDER, String.valueOf(block.getBlockID()))
						.replaceFirst(PLACEHOLDER, String.valueOf(block.getxPosition()))
						.replaceFirst(PLACEHOLDER, String.valueOf(block.getyPosition()));
			} else {
				blockString = BLOCK_TEMPLATE.replaceFirst(PLACEHOLDER, block.getOperationType().getValue())
						.replaceFirst(PLACEHOLDER, String.valueOf(block.getBlockID()));
			}
			//blockString = BLOCK_TEMPLATE_SIMPLE.replaceFirst(PLACEHOLDER, block.getOperationType().getValue());
			String parameterString = "";
			for (Parameter parameter : block.getParameters()) {
				Log.d("struct", parameter.getName());
				if(block.containsValues()) {
					if(parameter.getParameterType() == ParameterType.NUMBER) {
						parameterString += NUMERIC_VALUE_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
								.replaceFirst(PLACEHOLDER, String.valueOf(parameter.getParameterId()))
								.replaceFirst(PLACEHOLDER, String.valueOf((int)parameter.evaluateNumericValue()));
					} else if(parameter.getParameterType() == ParameterType.TEXT) {
						parameterString += TEXT_VALUE_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
								.replaceFirst(PLACEHOLDER, String.valueOf(parameter.getParameterId()))
								.replaceFirst(PLACEHOLDER, parameter.evaluateTextValue());
					} else if(parameter.getParameterType() == ParameterType.TRUTHVALUE) {
						parameterString += TRUTH_VALUE_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
								.replaceFirst(PLACEHOLDER, String.valueOf(parameter.getParameterId()))
								.replaceFirst(PLACEHOLDER, String.valueOf(parameter.evaluateTruthValue()));
					} else if(parameter.getParameterType() == ParameterType.SPRITE) {
						parameterString += FIELD_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
								.replaceFirst(PLACEHOLDER, parameter.valueToString());
					} else if(parameter.getParameterType() == ParameterType.TOUCH) {
						parameterString += FIELD_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
								.replaceFirst(PLACEHOLDER, parameter.valueToString());
					}
				} else {
					parameterString += FIELD_TEMPLATE.replaceFirst(PLACEHOLDER, parameter.getName())
							.replaceFirst(PLACEHOLDER, parameter.valueToString());
				}
			}

			if(i < (sprite.getBlocks().size() - 1)) {
				parameterString += NEXT_TEMPLATE;
			}

			blockString = blockString.replaceFirst(PLACEHOLDER, parameterString);

			xml = xml.replaceFirst(PLACEHOLDER, blockString);
		}
		
		if(xml.contains(PLACEHOLDER))
			xml = xml.replaceFirst(PLACEHOLDER, "");
		
		Log.d("xml", "generateXML(" + selectedScene + "|" + selectedSprite +"): " + xml);
		
		return xml;
	}
}
