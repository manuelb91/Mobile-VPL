package it.unibz.mobilevpl.object;

import it.unibz.mobilevpl.block.OperatorBlock;
import it.unibz.mobilevpl.definition.BlockDefinition.BlockType;
import it.unibz.mobilevpl.definition.BlockDefinition.OperationType;
import it.unibz.mobilevpl.object.Parameter.ParameterType;

import java.io.Serializable;
import java.util.List;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Block extends SugarRecord<Block> implements Serializable {
	
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
		
		if(this.getOperationType() == OperationType.ADDITION) {
			result = OperatorBlock.addition(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.SUBTRACTION) {
			result = OperatorBlock.subtraction(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.MULTIPLICATION) {
			result = OperatorBlock.multiplication(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.DIVISION) {
			result = OperatorBlock.division(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.PICK_RANDOM_INTEGER) {
			result = OperatorBlock.randomInteger((int)this.getParameters().get(0).evaluateNumericValue(), 
					(int)this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.PICK_RANDOM_DECIMAL) {			
			result = OperatorBlock.randomFloating(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.JOIN_NUMBER) {
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
		} else if(this.getOperationType() == OperationType.LETTER_OF_NUMBER) {
			result = OperatorBlock.letterOfNumber((int)this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateTextValue());
		} else if(this.getOperationType() == OperationType.LENGTH_OF_NUMBER) {
			float decimalNumber = this.getParameters().get(0).evaluateNumericValue();
			int integerNumber = (int)decimalNumber;
			result = OperatorBlock.lengthOf((decimalNumber == integerNumber) ? integerNumber : decimalNumber);
		} else if(this.getOperationType() == OperationType.LENGTH_OF_TEXT) {
			result = OperatorBlock.lengthOf(this.getParameters().get(0).evaluateTextValue());
		} else if(this.getOperationType() == OperationType.MODULO) {
			result = OperatorBlock.modulo(this.getParameters().get(0).evaluateNumericValue(), 
					this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.ROUND) {
			result = OperatorBlock.round(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_ABS) {
			result = OperatorBlock.abs(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_FLOOR) {
			result = OperatorBlock.floor(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_CEILING) {
			result = OperatorBlock.ceil(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_SQRT) {
			result = OperatorBlock.sqrt(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_SIN) {
			result = OperatorBlock.sin(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_COS) {
			result = OperatorBlock.cos(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_TAN) {
			result = OperatorBlock.tan(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_ASIN) {
			result = OperatorBlock.asin(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_ACOS) {
			result = OperatorBlock.acos(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_ATAN) {
			result = OperatorBlock.atan(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_LN) {
			result = OperatorBlock.ln(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_LOG) {
			result = OperatorBlock.log(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_E) {
			result = OperatorBlock.e(this.getParameters().get(0).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.OPERATION_OF_POW10) {
			result = OperatorBlock.pow10(this.getParameters().get(0).evaluateNumericValue());
		}
		
		return result;
	}
	
	public boolean evaluateTruthBlock() {
		boolean result = false;
		
		if(this.getOperationType() == OperationType.IS_LESS_THAN) {
			result = OperatorBlock.isLessThan(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.IS_EQUAL_TO) {
			result = OperatorBlock.isEqualTo(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.IS_GREATER_THAN) {
			result = OperatorBlock.isGreaterThan(this.getParameters().get(0).evaluateNumericValue(), this.getParameters().get(1).evaluateNumericValue());
		} else if(this.getOperationType() == OperationType.CONDITION_AND) {
			result = OperatorBlock.conditionAnd(this.getParameters().get(0).evaluateTruthValue(), this.getParameters().get(1).evaluateTruthValue());
		} else if(this.getOperationType() == OperationType.CONDITION_OR) {
			result = OperatorBlock.conditionOr(this.getParameters().get(0).evaluateTruthValue(), this.getParameters().get(1).evaluateTruthValue());
		} else if(this.getOperationType() == OperationType.CONDITION_NOT) {
			result = OperatorBlock.conditionNot(this.getParameters().get(0).evaluateTruthValue());
		}
		
		return result;
	}
	
	public String evaluateTextBlock() {
		String result = null;
		
		if(this.getOperationType() == OperationType.LETTER_OF_TEXT) {
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
