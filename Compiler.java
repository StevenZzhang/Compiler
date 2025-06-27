import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
public class Compiler {
    public static ArrayList<Variable> variables = new ArrayList<>();

    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            File myObj = new File("/home/steven/IdeaProjects/cs/src/UserInterface");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine())
            {
                String data = myReader.nextLine();
                lines.add(data);
            }
            myReader.close();
            for (int i = 0; i < lines.size(); i++) {
                String code = lines.get(i);
                if (code.isEmpty() || code.startsWith("//")) {
                    continue;
                    //This skips any empty lines or commented lines.
                }
                //Variable declaration
                else if (code.startsWith("int ") || code.startsWith("String ")) {
                    variablecreator(code);
                }
                //print
                else if (code.startsWith("print ")) {
                    print(code.substring(6));
                }
                //input
                else if (code.startsWith("input ")) {
                    input(code.substring(6));
                }
                //if statements
                else if (code.startsWith("if ")) {
                    boolean condition = ifmethod(code.substring(3));
                    if (!condition) {
                        i++;
                        //This skips the next line if the above statement is false.
                    }
                }
                //for loops
                else if (code.startsWith("loop ")) {
                    int count = Integer.parseInt(code.substring(5));
                    i++;
                    for (int j = 0; j < count; j++) {
                        loop(lines.get(i));
                    }
                }
                else {
                    System.out.println("Error in the code: " + code);
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void variablecreator(String code) {
        String[] parts = code.split(" ", 3);
        //Splits the code into 3 segments type, name, and value
        String type = parts[0].trim();
        String name = parts[1].trim();
        String valueStr = parts[2].trim();
        if (type.equals("int")) {
            Integer value = calculate(valueStr);
            //checks if there is a operation
            if (value == null) {
                return;
            }
            variables.add(new Variable(name, Integer.toString(value), "int"));
        } else if (type.equals("String")) {
            String value = valueStr;
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
            variables.add(new Variable(name, value, "string"));
        }
    }
    public static void print(String printed) {
        printed = printed.trim();
        // Checks if printed is a string
        if (printed.startsWith("\"") && printed.endsWith("\"")) {
            System.out.println(printed.substring(1, printed.length() - 1));
            return;
        }
        // Checks if printed is a variable
        Variable var = getVariable(printed);
        if (var != null) {
            System.out.println(var.value);
            return;
        }
        else {
            System.out.println(printed);
        }
    }
    public static void input(String code) {
        System.out.print("Enter the value of " + code + ": ");
        String input = scanner.nextLine();
        Variable existing = getVariable(code);
        if (existing != null) {
            existing.value = input;
        } else {
            variables.add(new Variable(code, input, "string"));
        }
    }
    public static boolean ifmethod(String statement) {
        String[] parts = statement.split("==");
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        if (parts[0].equals(parts[1])) {
            return true;
        }
        if (calculate(parts[0]) == calculate(parts[1])) {
            return true;
        }
        //Checks for equal String and int
        //limits to having to be the exact same, no math functions for this or variables :c
        return false;

    }
    public static void loop(String statement) {
        if (statement.startsWith("print ")) {
            print(statement.substring(6));
        }
        else if (statement.startsWith("input ")) {
            input(statement.substring(6));
        }
        else if (statement.startsWith("int ") || statement.startsWith("string ")) {
            variablecreator(statement);
        }
    }
    public static Integer calculate(String code) {
        code = code.trim();
        String[] parts;
        //Splits into two parts and does the operation
        if (code.contains("+")) {
            parts = code.split("\\+");
            return getIntValue(parts[0]) + getIntValue(parts[1]);
        }
        else if (code.contains("-")) {
            parts = code.split("-");
            return getIntValue(parts[0].trim()) - getIntValue(parts[1].trim());
        }
        else if (code.contains("*")) {
            parts = code.split("\\*");
            return getIntValue(parts[0]) * getIntValue(parts[1]);
        }
        else if (code.contains("/")) {
            parts = code.split("/");
            if (getIntValue(parts[1]) == 0) {
                System.out.println("Math Error in the code: " + code + ". Cannot divide by 0");
                return null;
            }
            return getIntValue(parts[0]) / getIntValue(parts[1]);
        }
        else if (code.contains("%")) {
            parts = code.split("%");
            return getIntValue(parts[0]) % getIntValue(parts[1]);
        }
        else {
            return getIntValue(code);
        }
    }
    //Converts string to ints
    public static int getIntValue(String token) {
        token = token.trim();
        Variable var = getVariable(token);
        if (var != null) {
            return Integer.parseInt(var.value);
        }
        return Integer.parseInt(token);
    }
    //Finds specific variable in variables arraylist
    public static Variable getVariable(String name) {
        //Checks the current variable for "name"
        for (Variable v : variables) {
            if (v.name.equals(name)) {
                return v;
            }
        }
        return null;
    }
}