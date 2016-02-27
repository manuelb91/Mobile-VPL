package it.unibz.mobilevpl.object;

import it.unibz.mobilevpl.block.OperatorBlock;
import it.unibz.mobilevpl.object.Parameter.ParameterType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Block extends SugarRecord<Block> implements Serializable {
	
	public enum BlockType {
		CONTROL("control"), DATA("data"), EVENT("event"), LOOK("look"), MOTION("motion"), 
		OPERATOR("operator"), PEN("pen"), SENSING("sensing"), SOUND("sound");
		
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
	
	public enum OperationType {
		WHEN_FALG_PRESSED("event", "when_flag_pressed", false), WHEN_SPRITE_CLICKED("event", "when_sprite_clicked", false), 
		WAIT_FOR_SECONDS("event", "wait_for_seconds", false), REPEAT_N_TIMES("event", "repeat_n_times", false), 
		REPEAT_FOREVER("event", "repeat_forever", false), BROADCAST_MESSAGE("event", "broadcast_message", false), 
		BROADCAST_MESSAGE_AND_WAIT("event", "broadcast_message_and_wait", false), 
		WHEN_MESSAGE_RECEIVED("event", "when_message_received", false), 
		STOP_SCRIPT("event", "stop_script", false), STOP_ALL("event", "stop_all", false),
		
		MOVEMENT("motion", "movement", false), TURN_CLOCKWISE("motion", "turn_clockwise", false), 
		TURN_COUNTER_CLOCKWISE("motion", "turn_counter_clockwise", false), POINT_DIRECTION("motion", "point_direction", false), 
		POINT_TOWARDS_TOUCH("motion", "point_towards_touch", false), POINT_TOWARDS_SPRITE("motion", "point_towards_sprite", false),
		GO_TO_XY("motion", "go_to_xy", true), GO_TO("motion", "go_to", false), 
		GLIDE_TO_XY_SECONDS("motion", "glide_to_xy_seconds", true), CHANGE_X("motion", "change_x", true), SET_X("motion", "set_x", true), 
		CHANGE_Y("motion", "change_y", true), SET_Y("motion", "set_y", true), BOUNCE_IF_ON_EDGE("motion", "bounce_if_on_edge", false), 
		ROTATION_STYLE("motion", "RotationStyle", false),
		
		PLAY_SOUND("sound", "play_sound", false), PLAY_SOUND_UNTIL_DONE("sound", "play_sound_until_done", false), 
		STOP_ALL_SOUNDS("sound", "stop_all_sounds", false), CHANGE_VOLUME_BY("sound", "change_volume_by", false), 
		SET_VOLUME_TO_PERCENTAGE("sound", "set_volume_to_percentage", false), 
		
		IS_LESS_THAN("operator", "IsLessThan", false), IS_EQUAL_TO("operator", "IsEqualTo", false), 
		IS_GREATER_THAN("operator", "IsGreaterThan", false), 
		CONDITION_AND("operator", "ConditionAnd", false), CONDITION_OR("operator", "ConditionOr", false), 
		CONDITION_NOT("operator", "ConditionNot", false), 
		ADDITION("operator", "Addition", false), SUBTRACTION("operator", "Subtraction", false), 
		MULTIPLICATION("operator", "Multiplication", false), 
		DIVISION("operator", "Division", false), PICK_RANDOM_INTEGER("operator", "PickRandomInteger", false), 
		PICK_RANDOM_DECIMAL("operator", "PickRandomDecimal", false), JOIN_NUMBER("operator", "JoinNumber", false), 
		JOIN_TEXT("operator", "JoinText", false), 
		LETTER_OF_TEXT("operator", "LetterOfText", false), LETTER_OF_NUMBER("operator", "LetterOfNumber", false), 
		LENGTH_OF_NUMBER("operator", "LengthOfNumber", false), LENGTH_OF_TEXT("operator", "LengthOfText", false),
		MODULO("operator", "Modulo", false), 
		ROUND("operator", "Round", false), OPERATION_OF_ABS("operator", "OperationOfAbs", false), 
		OPERATION_OF_FLOOR("operator", "OperationOfFloor", false), 
		OPERATION_OF_CEILING("operator", "OperationOfCeiling", false), OPERATION_OF_SQRT("operator", "OperationOfSqrt", false), 
		OPERATION_OF_SIN("operator", "OperationOfSin", false), OPERATION_OF_COS("operator", "OperationOfCos", false), 
		OPERATION_OF_TAN("operator", "OperationOfTan", false), OPERATION_OF_ASIN("operator", "OperationOfAsin", false), 
		OPERATION_OF_ACOS("operator", "OperationOfAcos", false), OPERATION_OF_ATAN("operator", "OperationOfAtan", false), 
		OPERATION_OF_LN("operator", "OperationOfLn", false), OPERATION_OF_LOG("operator", "OperationOfLog", false), 
		OPERATION_OF_E("operator", "OperationOfE", false), OPERATION_OF_POW10("operator", "OperationOfPow10", false);
		
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
	
	@Ignore private BlockType blockType;
	@Ignore private OperationType operationType;
	@Ignore private List<Parameter> parameters;
	
    private String name;
    private int position;
    private Sprite sprite;
    
    private int blockID;
    private int xPosition;
    private int yPosition;
    
    public Block() {
    }

    public Block(int blockID) {
    	this.blockID = blockID;
    	this.xPosition = -1;
    	this.yPosition = -1;
    }
    
    public Block(int blockID, String value) {
    	this(blockID);
    	this.setBlockValue(value);
    }

    public Block(int blockID, BlockType blockType, OperationType operationType, int position) {
        this(blockID);
        this.blockType = blockType;
        this.operationType = operationType;
        this.position = position;
    }

    public BlockType getBlockType() {
		return blockType;
	}

	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
	}
	
	public OperationType getOperationType() {
		return operationType;
	}

	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public int getBlockID() {
		return blockID;
	}

	public void setBlockID(int blockID) {
		this.blockID = blockID;
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

	public void setBlockValue(String value) {
		this.operationType = OperationType.get(value);
    	this.blockType = BlockType.get(this.operationType.getType());
	}
	
	public boolean containsValues() {
		return this.operationType.getContainsValues();
	}
	
	public float evaluateNumericBlock() {
		float result = 0f;
		
		if(this.getOperationType() == Block.OperationType.ADDITION) {
			result = OperatorBlock.addition(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.SUBTRACTION) {
			result = OperatorBlock.subtraction(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.MULTIPLICATION) {
			result = OperatorBlock.multiplication(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.DIVISION) {
			result = OperatorBlock.division(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.PICK_RANDOM_INTEGER) {
			result = OperatorBlock.randomInteger((int)this.getParameters().get(0).evaluateNumericValue(), 
					(int)this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.PICK_RANDOM_DECIMAL) {			
			result = OperatorBlock.randomFloating(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.JOIN_NUMBER) {
			Parameter a = this.getParameters().get(0);
			Parameter b = this.getParameters().get(1);
			if(a.getParameterType() == ParameterType.NUMBER) {
				if(b.getParameterType() == ParameterType.NUMBER)
					result = OperatorBlock.joinNumber(a.evaluateNumericValue(), b.evaluateNumericValue());
				else
					result = OperatorBlock.joinNumber(a.evaluateNumericValue(), b.getTextValue());
			} else {
				if(b.getParameterType() == ParameterType.NUMBER)
					result = OperatorBlock.joinNumber(a.getTextValue(), b.evaluateNumericValue());
				else
					result = OperatorBlock.joinNumber(a.getTextValue(), b.getTextValue());
			}
		} else if(this.getOperationType() == Block.OperationType.LETTER_OF_NUMBER) {
			result = OperatorBlock.letterOfNumber((int)this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateTextValue());
		} else if(this.getOperationType() == Block.OperationType.LENGTH_OF_NUMBER) {
			float decimalNumber = this.getParameters().get(0).evaluateNumericValue();
			int integerNumber = (int)decimalNumber;
			result = OperatorBlock.lengthOf((decimalNumber == integerNumber) ? integerNumber : decimalNumber);
		} else if(this.getOperationType() == Block.OperationType.LENGTH_OF_TEXT) {
			result = OperatorBlock.lengthOf(this.getParameters().get(0).evaluateTextValue());
		} else if(this.getOperationType() == Block.OperationType.MODULO) {
			result = OperatorBlock.modulo(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.ROUND) {
			result = OperatorBlock.round(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_ABS) {
			result = OperatorBlock.abs(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_FLOOR) {
			result = OperatorBlock.floor(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_CEILING) {
			result = OperatorBlock.ceil(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_SQRT) {
			result = OperatorBlock.sqrt(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_SIN) {
			result = OperatorBlock.sin(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_COS) {
			result = OperatorBlock.cos(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_TAN) {
			result = OperatorBlock.tan(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_ASIN) {
			result = OperatorBlock.asin(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_ACOS) {
			result = OperatorBlock.acos(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_ATAN) {
			result = OperatorBlock.atan(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_LN) {
			result = OperatorBlock.ln(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_LOG) {
			result = OperatorBlock.log(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_E) {
			result = OperatorBlock.e(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.OPERATION_OF_POW10) {
			result = OperatorBlock.pow10(this.getParameters().get(0).evaluateNumericValue());
		}
		
		return result;
	}
	
	public boolean evaluateTruthBlock() {
		boolean result = false;
		
		if(this.getOperationType() == Block.OperationType.IS_LESS_THAN) {
			result = OperatorBlock.isLessThan(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.IS_EQUAL_TO) {
			result = OperatorBlock.isEqualTo(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.IS_GREATER_THAN) {
			result = OperatorBlock.isGreaterThan(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == Block.OperationType.CONDITION_AND) {
			result = OperatorBlock.conditionAnd(this.getParameters().get(0).evaluateTruthValue(), this.getParameters().get(1).evaluateTruthValue());
		} else if(this.getOperationType() == Block.OperationType.CONDITION_OR) {
			result = OperatorBlock.conditionOr(this.getParameters().get(0).evaluateTruthValue(), this.getParameters().get(1).evaluateTruthValue());
		} else if(this.getOperationType() == Block.OperationType.CONDITION_NOT) {
			result = OperatorBlock.conditionNot(this.getParameters().get(0).evaluateTruthValue());
		}
		
		return result;
	}
	
	public String evaluateTextBlock() {
		String result = null;
		
		if(this.getOperationType() == Block.OperationType.LETTER_OF_TEXT) {
			result = OperatorBlock.letterOfText((int)this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateTextValue());
		}
		
		return result;
	}

	private void createNameForBlock() {
    	this.name = this.blockType.getValue() + ";" + this.operationType.getValue();
    }
    
    public void fillTypeVariables() {
    	String[] parts = name.split(";");
    	this.blockType = BlockType.get(parts[0]);
    	this.operationType = OperationType.get(parts[1]);
    }
    
    public List<Parameter> getParameters(){
    	if(this.parameters == null)
    		this.parameters = Parameter.find(Parameter.class, "block = ?", 
    				new String[] {String.valueOf(this.getId())}, null, "position", null);
		return this.parameters;
    }
    
    public boolean isEmpty() {
		return (this.getParameters() == null) || (this.getParameters().size() == 0);
	}
    
    public void addParameter(Parameter parameter) {
		parameter.setBlock(this);
		this.getParameters().add(parameter);
		this.storeAll();
	}
    
    public void addParameter(float value) {
    	Parameter parameter = new Parameter(value, this);
    	this.getParameters().add(parameter);
    	this.storeAll();
    }
    
    public void addParameter(String value) {
    	Parameter parameter = new Parameter(value, this);
    	this.getParameters().add(parameter);
    	this.storeAll();
    }

	public void addParameter(boolean value) {
		Parameter parameter = new Parameter(value, this);
    	this.getParameters().add(parameter);
    	this.storeAll();
	}
	
	public void addParameter(Sprite sprite) {
		Parameter parameter = new Parameter(sprite, this);
		this.getParameters().add(parameter);
    	this.storeAll();
	}
	
	public void addParameter(Sound sound) {
		Parameter parameter = new Parameter(sound, this);
		this.getParameters().add(parameter);
    	this.storeAll();
	}
    
    public void store() {
    	createNameForBlock();
    	this.save();
    }
    
    public void storeAll() {
    	this.store();
    	for (int i = 0; i < this.getParameters().size(); i++) {
    		Parameter parameter = this.getParameters().get(i);
    		parameter.setPosition(i + 1);
    		parameter.store();
		}
    }
    
    public void remove() {
    	for (int i = 0; i < this.getParameters().size(); i++) {
    		this.getParameters().get(i).remove();
    		this.getParameters().remove(i);
		}
    	this.delete();
    }
    
    public void prepare() {
    	this.fillTypeVariables();
    }
    
    public void prepareAll() {
    	this.prepare();
    	for (Parameter parameter : this.getParameters()) {
			parameter.prepareAll();
		}
    }
}
