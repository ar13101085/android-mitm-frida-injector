package model;

public class KeyValue {
    public String keyName;
    public String value;

    public KeyValue(String keyName, String value) {
        this.keyName = keyName;
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValue{" +
                "keyName='" + keyName + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
