package org.hit.fintech2018.katz;

public class ISO8583Field {
    private int id, length;
    private String name, className;
    private boolean isFixed;

    public ISO8583Field(int id, int length, String name, String className, boolean isFixed) {
        this.id = id;
        this.length = length;
        this.name = name;
        this.className = className;
        this.isFixed = isFixed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }
}
