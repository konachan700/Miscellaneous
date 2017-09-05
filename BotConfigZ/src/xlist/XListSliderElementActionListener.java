package xlist;

public interface XListSliderElementActionListener {
    public void OnStateChange(String GID, String ID, int addr, int min, int max, int init, String type, int currentValue);
    public void OnWriteIndexButtonClicked(String GID, String ID, int addr, int min, int max, int init, String type, int currentValue);
    public void OnWriteMMIButtonClicked(String GID, String ID, int addr, int min, int max, int init, String type, int currentValue, int index);
}
