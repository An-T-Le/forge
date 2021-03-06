package forge.toolbox;


public class FSpinner extends FTextField {
    private int value, minValue, maxValue;

    public FSpinner(int minValue0, int maxValue0) {
        this(minValue0, maxValue0, minValue0);
    }

    public FSpinner(int minValue0, int maxValue0, int initialValue) {
        minValue = minValue0;
        maxValue = maxValue0;
        value = minValue - 1; //ensure value changes so text updated
        setValue(initialValue);
    }
    
    public int getValue() {
        return value;
    }
    public void setValue(int value0) {
        if (value0 < minValue) {
            value0 = minValue;
        }
        else if (value0 > maxValue) {
            value0 = maxValue;
        }
        if (value == value0) { return; }
        value = value0;
        setText(String.valueOf(value));
    }
}
