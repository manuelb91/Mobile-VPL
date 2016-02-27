package it.unibz.mobilevpl.filter;

import it.unibz.mobilevpl.util.ContextManager;
import android.R.integer;
import android.text.InputFilter;
import android.text.Spanned;

public class SpritePositionFilter implements InputFilter {
	
	private static final String NEGATION = "-";

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		try {
			int input = 0;
			int value = 0;
			String destination = dest.toString();
			String src = source.toString();
			if((destination == null || destination.length() == 0) && (src != null && src.length() > 0)) {
				if(src.equals(NEGATION)) {
					return NEGATION;
				} else {
					return null;
				}
			}
			if(destination.equals(NEGATION) && src.equals(NEGATION)) {
				return NEGATION;
			}
			input = Integer.parseInt(src);
			if(destination.equals(NEGATION)) {
				return null;
			}
			value = Integer.parseInt(destination);
			if(src.equals(NEGATION)) {
				value += -1;
				return String.valueOf(value);
			}
			input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(input))
                return null;
        } catch (NumberFormatException nfe) { } 
		return "";
	}
	
	private boolean isInRange(int input) {
		return (input >= ContextManager.MIN && input <= ContextManager.MAX);
	}

}
