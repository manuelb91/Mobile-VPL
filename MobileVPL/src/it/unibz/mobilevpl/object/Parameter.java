package it.unibz.mobilevpl.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Parameter extends SugarRecord<Parameter> implements Serializable {

	public enum ParameterType {
		NUMBER("Number"), TRUTHVALUE("Truthvalue"), TEXT("Text"), SPRITE("Sprite"), TOUCH("Touch"), OPERATOR("Operator"), SOUND("Sound");
		
		private String value;
		
		private static final Map<String, ParameterType> lookup = new HashMap<String, ParameterType>();
        static {
            for (ParameterType parameterType : ParameterType.values())
                lookup.put(parameterType.getValue(), parameterType);
        }
		
		private ParameterType(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public static ParameterType get(String value) {
            return lookup.get(value);
        }
	}
	
	@Ignore private ParameterType parameterType;
	
    private String description;
    private String name;
    private float numericValue;
    private boolean truthvalue;
    private String textValue;
    private Sprite sprite;
    private Sound sound;
    private Block operatorBlock;
    private int position;
    private Block block;
    private int parameterId;

    public Parameter() {
    	this.parameterId = -1;
    }
    
    public Parameter(float numericValue, Block block) {
    	this();
    	this.numericValue = numericValue;
    	this.block = block;
    }
    
    public Parameter(boolean truthvalue, Block block) {
    	this();
    	this.truthvalue = truthvalue;
    	this.block = block;
    }
    
    public Parameter(String textValue, Block block) {
    	this();
    	this.textValue = textValue;
    	this.block = block;
    }
    
    public Parameter(Sprite sprite, Block block) {
    	this();
    	this.sprite = sprite;
    	this.block = block;
    }
    
    public Parameter(Sound sound, Block block) {
    	this();
    	this.sound = sound;
    	this.block = block;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public int getPosition() {
		return position;
	}
    
	public void setPosition(int position) {
		this.position = position;
	}
	
	public ParameterType getParameterType() {
		return parameterType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public float getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(float numericValue) {
		this.numericValue = numericValue;
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
	
	public Block getOperatorBlock() {
		return operatorBlock;
	}

	public void setOperatorBlock(Block operatorBlock) {
		this.operatorBlock = operatorBlock;
	}
	
	public int getParameterId() {
		return parameterId;
	}

	public void setParameterId(int parameterId) {
		this.parameterId = parameterId;
	}

	public boolean isTruthvalue() {
		return truthvalue;
	}

	public void setTruthvalue(boolean truthvalue) {
		this.truthvalue = truthvalue;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sound getSound() {
		return sound;
	}

	public void setSound(Sound sound) {
		this.sound = sound;
	}

	public float evaluateNumericValue() {
		if(this.parameterType == ParameterType.OPERATOR) {
			return this.operatorBlock.evaluateNumericBlock();
		}
		return this.numericValue;
	}
	
	public boolean evaluateTruthValue() {
		if(this.parameterType == ParameterType.OPERATOR) {
			return this.operatorBlock.evaluateTruthBlock();
		}
		return this.truthvalue;
	}
	
	public String evaluateTextValue() {
		if(this.parameterType == ParameterType.OPERATOR) {
			return this.operatorBlock.evaluateTextBlock();
		}
		return this.textValue;
	}
	
	public String valueToString() {
		if(this.parameterType == ParameterType.NUMBER) {
			return String.valueOf((int)this.evaluateNumericValue());
		} else if(this.parameterType == ParameterType.TRUTHVALUE) {
			return String.valueOf(this.evaluateTruthValue());
		} else if(this.parameterType == ParameterType.TEXT) {
			return String.valueOf(this.evaluateTextValue());
		} else if (this.parameterType == ParameterType.SPRITE) {
			return String.valueOf((int)this.evaluateNumericValue());
		} else if (this.parameterType == ParameterType.TOUCH) {
			return String.valueOf((int)this.evaluateNumericValue());
		} else if (this.parameterType == ParameterType.SOUND) {
			return String.valueOf((int)this.evaluateNumericValue());
		} else {
			return "";
		}
	}

	private void createDescriptionForParameter() {
    	this.description = this.parameterType.getValue();
    }
	
	public void fillTypeVariable() {
    	this.parameterType = ParameterType.get(this.description);
    }
	
	public void store() {
		this.createDescriptionForParameter();
    	this.save();
    }
	
	public void remove() {
		if(this.parameterType == ParameterType.OPERATOR) {
			this.operatorBlock.remove();
			this.block = null;
			this.sprite = null;
		}
		this.delete();
	}
	
    public void prepare() {
    	this.fillTypeVariable();
    }
    
    public void prepareAll() {
    	this.prepare();
    	if((this.parameterType == ParameterType.OPERATOR) && (this.operatorBlock != null)) {
    		this.operatorBlock.prepareAll();
    	}
    }
}
