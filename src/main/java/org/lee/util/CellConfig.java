package org.lee.util;

public class CellConfig {
    private int index;

    private String type;

    private String key;

    private final int START_INDEX = 0;

    public enum TYPES{
        NUM("num", "整数"),
        STR("str", "文本");


        private String value;

        private String description;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        private TYPES(String value, String description){
            this.value = value;
            this.description = description;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
