package generatebarcode.com.scanhotelmenu.utils;

public class EventClicked {
    private final String position;

    public EventClicked(String position) {
        this.position = position;
    }

    public String getPosition() {
        return position;
    }
}