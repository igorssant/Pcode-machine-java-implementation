public class Instruction {
    private Operation operation;
    private int levelBuffer;
    private int addressBuffer;

    public Instruction() {}

    public Instruction(Operation operation, int levelBuffer, int addressBuffer) {
        this.operation = operation;
        this.levelBuffer = levelBuffer;
        this.addressBuffer = addressBuffer;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public int getLevelBuffer() {
        return this.levelBuffer;
    }

    public void setLevelBuffer(int levelBuffer) {
        this.levelBuffer = levelBuffer;
    }

    public int getAddressBuffer() {
        return this.addressBuffer;
    }

    public void setAddressBuffer(int addressBuffer) {
        this.addressBuffer = addressBuffer;
    }
}
