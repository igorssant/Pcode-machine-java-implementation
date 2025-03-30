import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static Operation translateOperation(String stringOperation) {
        return switch(stringOperation) {
            case "LIT" -> Operation.LIT;
            case "OPR" -> Operation.OPR;
            case "LOD" -> Operation.LOD;
            case "STO" -> Operation.STO;
            case "CAL" -> Operation.CAL;
            case "INT" -> Operation.INT;
            case "JMP" -> Operation.JMP;
            case "JPC" -> Operation.JPC;
            default -> throw new IllegalStateException("Unexpected value: " + stringOperation);
        };
    }

    public static Instruction readFromFile(BufferedReader bufferedReader, String separator) throws IOException {
        String lineRead = bufferedReader.readLine().replaceAll("\\s*", ""),
            operation = lineRead.substring(0, lineRead.indexOf(separator));
        int level = Integer.parseInt(
            lineRead.substring(lineRead.indexOf(separator) + 1, lineRead.lastIndexOf(separator))
        ),
            address = Integer.parseInt(lineRead.substring(lineRead.lastIndexOf(separator) + 1));

        return new Instruction(translateOperation(operation), level, address);
    }

    public static void main(String[] args) {
        String filePath = "./input.pcode",
            separator = ";";
        int numberOfOperations;
        Instruction[] instructions;
        Pcode pcode;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            numberOfOperations = Integer.parseInt(bufferedReader.readLine());
            instructions = new Instruction[numberOfOperations];

            for(int i = 0; i < numberOfOperations; i++) {
               instructions[i] = readFromFile(bufferedReader, separator);
            }
        } catch(FileNotFoundException e) {
            throw new RuntimeException("File does not exist.\n" + e);
        } catch (IOException e) {
            throw new RuntimeException("Could not read from file.\n" + e);
        }

        pcode = new Pcode(instructions);
        System.out.println(pcode.begin());
    }
}