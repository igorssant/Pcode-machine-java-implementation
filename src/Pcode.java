public class Pcode {
    private final int maximumLevel = 3;
    private final int maximumAddressNumber = 2047;
    private final int stackSize = 5012;
    private Instruction[] instructionArray;
    private int[] stack;
    private int programRegister;
    private int baseRegister;
    private int stackTopRegister;

    public Pcode(int numberOfInstructions) {
        if(isValidAddress(numberOfInstructions)) {
            throw new RuntimeException(
                "Critical error.\n" +
                "The maximum numbers of possible addresses is " +
                this.maximumAddressNumber + ".\n"
            );
        }

        this.instructionArray = new Instruction[numberOfInstructions];
    }

    public Pcode(Instruction[] instructionArray) {
        if(instructionArray.length > (maximumAddressNumber - 1)) {
            throw new RuntimeException(
                "Critical error.\n" +
                "The maximum numbers of possible addresses is " +
                this.maximumAddressNumber + ".\n" +
                "Too many commands.\n"
            );
        }

        this.instructionArray = instructionArray;
    }

    public void setInstructionArray(Instruction[] instructionArray) {
        if(this.instructionArray != null) {
            throw new RuntimeException("The Instruction array is not null, thus must not be altered.");
        }

        this.instructionArray = instructionArray;
    }

    public int begin() {
        this.stack = new int[stackSize];
        Instruction instruction;

        if(this.instructionArray == null) {
            throw new RuntimeException(
                "Fatal error.\n" +
                "Cannot begin the P-code procedure with an empty Instruction array.\n"
            );
        }

        this.stackTopRegister = -1;
        this.baseRegister = 0;
        this.programRegister = 0;

        do {
            instruction = this.instructionArray[this.programRegister];
            this.programRegister++;
            processOperation(instruction);

        } while(this.programRegister != 0);

        return this.stack[
            this.maximumLevel +
            instruction.getLevelBuffer() +
            instruction.getAddressBuffer()
        ];
    }

    private boolean isValidAddress(int number) {
        return number > (maximumAddressNumber - 1);
    }

    private int findBase(int level) {
        int newBase = this.baseRegister;

        while(level > 0) {
            newBase = this.stack[newBase];
            level--;
        }

        return newBase;
    }

    private int numberIsOdd(int number) {
        // 1 stands for true
        // 0 stands for false
        return ((number % 2) == 1) ? 1 : 0;
    }

    private void processOperation(Instruction instruction) {
        switch(instruction.getOperation()) {
            case Operation.LIT:
                this.stackTopRegister++;
                this.stack[this.stackTopRegister] = instruction.getAddressBuffer();
                break;

            case Operation.OPR:
                handleOperation(instruction.getAddressBuffer());
                break;

            case Operation.LOD:
                this.stackTopRegister++;
                this.stack[this.stackTopRegister] = this.stack[
                    findBase(instruction.getLevelBuffer()) + instruction.getAddressBuffer()
                ];
                break;

            case Operation.STO:
                this.stack[
                    findBase(instruction.getLevelBuffer()) + instruction.getAddressBuffer()
                ] = this.stack[this.stackTopRegister];
                this.stackTopRegister--;
                break;

            case Operation.CAL:
                this.stack[this.stackTopRegister + 1] = findBase(instruction.getLevelBuffer());
                this.stack[this.stackTopRegister + 2] = this.baseRegister;
                this.stack[this.stackTopRegister + 3] = this.programRegister;
                this.baseRegister = this.stackTopRegister + 1;
                this.programRegister = instruction.getAddressBuffer();
                break;

            case Operation.INT:
                this.stackTopRegister += instruction.getAddressBuffer();
                break;

            case Operation.JMP:
                this.programRegister = instruction.getAddressBuffer();
                break;

            case Operation.JPC:
                // 1 stands for true
                if(this.stack[this.stackTopRegister] == 1) {
                    this.programRegister = instruction.getAddressBuffer();
                }

                this.stackTopRegister--;
                break;

            default:
                throw new RuntimeException(
                    "Runtime error.\n" +
                    "Bad expression in instruction {\n" +
                    "\t" + instruction.getOperation() + ";\n" +
                    "\t" + instruction.getAddressBuffer() + ";\n" +
                    "\t" + instruction.getLevelBuffer() + ";\n}\n"
                );
        }
    }

    private void handleOperation(int operation) {
        switch(operation) {
            case 0: // return
                this.stackTopRegister = this.baseRegister - 1;
                this.programRegister = this.stack[this.stackTopRegister + 3];
                this.baseRegister = this.stack[this.stackTopRegister + 2];
                break;

            case 1: // NEGATE
                this.stack[this.stackTopRegister] = -this.stack[this.stackTopRegister];
                break;

            case 2: // ADD
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] += this.stack[this.stackTopRegister + 1];
                break;

            case 3: // SUB
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] -= this.stack[this.stackTopRegister + 1];
                break;

            case 4: // MULTIPLY
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] *= this.stack[this.stackTopRegister + 1];
                break;

            case 5: // DIVIDE
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] /= this.stack[this.stackTopRegister + 1];
                break;

            case 6: // IS ODD ?
                this.stack[this.stackTopRegister] = numberIsOdd(this.stack[this.stackTopRegister]);
                break;

            case 7: // EQUALS
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = equals(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            case 8: // NOT EQUALS
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = 1 - equals(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            case 9: // LESS THAN (<)
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = lessThan(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            case 10: // LESS EQUAL THAN (<=)
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = lessOrEqualsThan(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            case 11: // GRATER THAN (>)
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = 1 - lessThan(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            case 12: // GREATER EQUAL THAN (>=)
                this.stackTopRegister--;
                this.stack[this.stackTopRegister] = greaterOrEqualsThan(
                    this.stack[this.stackTopRegister],
                    this.stack[this.stackTopRegister + 1]
                );
                break;

            default:
                throw new RuntimeException(
                    "Runtime error.\n" +
                    "Unknown operation: Intruction.addressBuffer = " +
                    operation + ".\n"
                );
        }
    }

    private int equals(int a, int b) {
        return (a == b) ? 1 : 0;
    }

    private int lessThan(int a, int b) {
        return (a < b) ? 1 : 0;
    }

    private int lessOrEqualsThan(int a, int b) {
        return (a <= b) ? 1 : 0;
    }

    private int greaterOrEqualsThan(int a, int b) {
        return (a >= b) ? 1 : 0;
    }
}
