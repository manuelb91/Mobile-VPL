package it.unibz.mobilevpl.definition;

import java.util.HashMap;
import java.util.Map;

public class BlockDefinition {

	public static enum BlockType {
		CONTROL("control"), 
		DATA("data"), 
		EVENT("event"), 
		LOOK("look"), 
		MOTION("motion"), 
		OPERATOR("operator"), 
		PEN("pen"), 
		SENSING("sensing"), 
		SOUND("sound");
		
		private String value;
		
		private static final Map<String, BlockType> lookup = new HashMap<String, BlockType>();
        static {
            for (BlockType blockType : BlockType.values())
                lookup.put(blockType.getValue(), blockType);
        }
		
		private BlockType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public static BlockType get(String value) {
            return lookup.get(value);
        }
	}
	
	public static enum OperationType {
		WHEN_FALG_PRESSED(BlockType.EVENT.getValue(), "when_flag_pressed", false), 
		WHEN_SPRITE_CLICKED(BlockType.EVENT.getValue(), "when_sprite_clicked", false), 
		WAIT_FOR_SECONDS(BlockType.EVENT.getValue(), "wait_for_seconds", false), 
		REPEAT_N_TIMES(BlockType.EVENT.getValue(), "repeat_n_times", false), 
		REPEAT_FOREVER(BlockType.EVENT.getValue(), "repeat_forever", false), 
		BROADCAST_MESSAGE(BlockType.EVENT.getValue(), "broadcast_message", false), 
		BROADCAST_MESSAGE_AND_WAIT(BlockType.EVENT.getValue(), "broadcast_message_and_wait", false), 
		WHEN_MESSAGE_RECEIVED(BlockType.EVENT.getValue(), "when_message_received", false), 
		STOP_SCRIPT(BlockType.EVENT.getValue(), "stop_script", false), 
		STOP_ALL(BlockType.EVENT.getValue(), "stop_all", false),
		
		MOVEMENT(BlockType.MOTION.getValue(), "movement", false), 
		TURN_CLOCKWISE(BlockType.MOTION.getValue(), "turn_clockwise", false), 
		TURN_COUNTER_CLOCKWISE(BlockType.MOTION.getValue(), "turn_counter_clockwise", false), 
		POINT_DIRECTION(BlockType.MOTION.getValue(), "point_direction", false), 
		POINT_TOWARDS_TOUCH(BlockType.MOTION.getValue(), "point_towards_touch", false), 
		POINT_TOWARDS_SPRITE(BlockType.MOTION.getValue(), "point_towards_sprite", false),
		GO_TO_XY(BlockType.MOTION.getValue(), "go_to_xy", true), 
		GO_TO(BlockType.MOTION.getValue(), "go_to", false), 
		GLIDE_TO_XY_SECONDS(BlockType.MOTION.getValue(), "glide_to_xy_seconds", true), 
		CHANGE_X(BlockType.MOTION.getValue(), "change_x", true), 
		SET_X(BlockType.MOTION.getValue(), "set_x", true), 
		CHANGE_Y(BlockType.MOTION.getValue(), "change_y", true), 
		SET_Y(BlockType.MOTION.getValue(), "set_y", true), 
		BOUNCE_IF_ON_EDGE(BlockType.MOTION.getValue(), "bounce_if_on_edge", false), 
		ROTATION_STYLE(BlockType.MOTION.getValue(), "rotation_style", false),
		
		PLAY_SOUND(BlockType.SOUND.getValue(), "play_sound", false), 
		PLAY_SOUND_UNTIL_DONE(BlockType.SOUND.getValue(), "play_sound_until_done", false), 
		STOP_ALL_SOUNDS(BlockType.SOUND.getValue(), "stop_all_sounds", false), 
		CHANGE_VOLUME_BY(BlockType.SOUND.getValue(), "change_volume_by", false), 
		SET_VOLUME_TO_PERCENTAGE(BlockType.SOUND.getValue(), "set_volume_to_percentage", false), 
		
		IS_LESS_THAN(BlockType.OPERATOR.getValue(), "IsLessThan", false), 
		IS_EQUAL_TO(BlockType.OPERATOR.getValue(), "IsEqualTo", false), 
		IS_GREATER_THAN(BlockType.OPERATOR.getValue(), "IsGreaterThan", false), 
		CONDITION_AND(BlockType.OPERATOR.getValue(), "ConditionAnd", false), 
		CONDITION_OR(BlockType.OPERATOR.getValue(), "ConditionOr", false), 
		CONDITION_NOT(BlockType.OPERATOR.getValue(), "ConditionNot", false), 
		ADDITION(BlockType.OPERATOR.getValue(), "Addition", false), 
		SUBTRACTION(BlockType.OPERATOR.getValue(), "Subtraction", false), 
		MULTIPLICATION(BlockType.OPERATOR.getValue(), "Multiplication", false), 
		DIVISION(BlockType.OPERATOR.getValue(), "Division", false), 
		PICK_RANDOM_INTEGER(BlockType.OPERATOR.getValue(), "PickRandomInteger", false), 
		PICK_RANDOM_DECIMAL(BlockType.OPERATOR.getValue(), "PickRandomDecimal", false), 
		JOIN_NUMBER(BlockType.OPERATOR.getValue(), "JoinNumber", false), 
		JOIN_TEXT(BlockType.OPERATOR.getValue(), "JoinText", false), 
		LETTER_OF_TEXT(BlockType.OPERATOR.getValue(), "LetterOfText", false), 
		LETTER_OF_NUMBER(BlockType.OPERATOR.getValue(), "LetterOfNumber", false), 
		LENGTH_OF_NUMBER(BlockType.OPERATOR.getValue(), "LengthOfNumber", false), 
		LENGTH_OF_TEXT(BlockType.OPERATOR.getValue(), "LengthOfText", false),
		MODULO(BlockType.OPERATOR.getValue(), "Modulo", false), 
		ROUND(BlockType.OPERATOR.getValue(), "Round", false), 
		OPERATION_OF_ABS(BlockType.OPERATOR.getValue(), "OperationOfAbs", false), 
		OPERATION_OF_FLOOR(BlockType.OPERATOR.getValue(), "OperationOfFloor", false), 
		OPERATION_OF_CEILING(BlockType.OPERATOR.getValue(), "OperationOfCeiling", false), 
		OPERATION_OF_SQRT(BlockType.OPERATOR.getValue(), "OperationOfSqrt", false), 
		OPERATION_OF_SIN(BlockType.OPERATOR.getValue(), "OperationOfSin", false), 
		OPERATION_OF_COS(BlockType.OPERATOR.getValue(), "OperationOfCos", false), 
		OPERATION_OF_TAN(BlockType.OPERATOR.getValue(), "OperationOfTan", false), 
		OPERATION_OF_ASIN(BlockType.OPERATOR.getValue(), "OperationOfAsin", false), 
		OPERATION_OF_ACOS(BlockType.OPERATOR.getValue(), "OperationOfAcos", false), 
		OPERATION_OF_ATAN(BlockType.OPERATOR.getValue(), "OperationOfAtan", false), 
		OPERATION_OF_LN(BlockType.OPERATOR.getValue(), "OperationOfLn", false), 
		OPERATION_OF_LOG(BlockType.OPERATOR.getValue(), "OperationOfLog", false), 
		OPERATION_OF_E(BlockType.OPERATOR.getValue(), "OperationOfE", false), 
		OPERATION_OF_POW10(BlockType.OPERATOR.getValue(), "OperationOfPow10", false);
		
		private String value;
		private String type;
		private boolean containsValues;
		private boolean containsOptions;
		
		private static final Map<String, OperationType> lookup = new HashMap<String, OperationType>();
        static {
            for (OperationType operationType : OperationType.values())
                lookup.put(operationType.getValue(), operationType);
        }
		
		private OperationType(String type, String value, boolean containsValues) {
			this.type = type;
			this.value = value;
			this.containsValues = containsValues;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public String getType() {
			return this.type;
		}
		
		public boolean getContainsValues() {
			return this.containsValues;
		}
		
		public static OperationType get(String value) {
            return lookup.get(value);
        }
	}
	
}
