import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NabTechTest {
    private static final Logger logger = Logger.getLogger(NabTechTest.class.getName());

    enum CMD { push, pop, clear, add, mul, neg, inv, undo, print, quit, help }

    private static final Stack<Double> dataStack = new Stack<>();
    private static final Stack<Map<CMD, LinkedList<Double>>> operationStack = new Stack<>();

    private static boolean isDouble(String doubleSting){
        try{
            Double.parseDouble(doubleSting);
        }catch (NumberFormatException exception){
            return false;
        }
        return true;
    }

    private static boolean isEmpty(String input){
        return input == null || input.length() ==0;
    }

    public static LinkedList<Double> copyToArray(Stack<Double> stack) {
        return new LinkedList<>(stack);
    }

    private static void processUndo(){
        Map<CMD, LinkedList<Double>> lastOperation = operationStack.pop();
        if(lastOperation.containsKey(CMD.push)){
            dataStack.pop();
        }else if(lastOperation.containsKey(CMD.pop)){
            dataStack.push(lastOperation.get(CMD.pop).get(0));
        }else if(lastOperation.containsKey(CMD.clear)){
            lastOperation.get(CMD.clear).forEach(dataStack::push);
        }else if(lastOperation.containsKey(CMD.add)){
            dataStack.pop();
            Iterator<Double> iterator = lastOperation.get(CMD.add).descendingIterator();
            while (iterator.hasNext()){
                dataStack.push(iterator.next());
            }
        }else if(lastOperation.containsKey(CMD.mul)){
            dataStack.pop();
            Iterator<Double> iterator = lastOperation.get(CMD.mul).descendingIterator();
            while (iterator.hasNext()){
                dataStack.push(iterator.next());
            }
        }else if(lastOperation.containsKey(CMD.neg)){
            dataStack.pop();
            dataStack.push(lastOperation.get(CMD.neg).get(0));
        }else if(lastOperation.containsKey(CMD.inv)){
            dataStack.pop();
            dataStack.push(lastOperation.get(CMD.inv).get(0));
        }
    }

    private static void processHelp(){
        String helpText = new StringBuilder(" Stack Machine supports below list of commands ")
                .append("\n").append("\n")
                .append("PUSH <xyz> or <xyz>   - Pushes the numeric value <xyz> to the top of the stack ( <xyz> is a valid decimal number ).").append("\n")
                .append("POP     - Removes the top element from the stack.").append("\n")
                .append("CLEAR   - Removes all elements from the stack.").append("\n")
                .append("ADD     - Adds the top 2 elements on the stack and pushes the result back to the stack.").append("\n")
                .append("MUL     - Multiplies the top 2 elements on the stack and pushes the result back to the stack.").append("\n")
                .append("NEG     - Negates the top element on the stack and pushes the result back to the stack.").append("\n")
                .append("INV     - Inverts the top element on the stack and pushes the result back to the stack.").append("\n")
                .append("UNDO    - The last instruction is undone leaving the stack in the same state as before that instruction.").append("\n")
                .append("PRINT   - Prints all elements that are currently on the stack.").append("\n")
                .append("QUIT    - Exits the program.").append("\n")
                .append("\n").append("\n").toString()
                ;
        logger.log(Level.INFO, helpText);
        logger.log(Level.INFO,"Please enter command:");
    }

    private static void processInput( String input){
        if(input.startsWith(CMD.push.name())){
            String[] inputs = input.split(" ");
            if(inputs.length != 2 || isEmpty(inputs[1]) || !isDouble(inputs[1])){
                logger.log(Level.WARNING, "Not a valid push command. Please Enter PUSH <xyz> where <xyz> is a valid decimal number.");
            }else{
                double data = Double.parseDouble(inputs[1]);
                dataStack.push(data);
                operationStack.push(Collections.singletonMap(CMD.push,  new LinkedList<>(Arrays.asList(data))));
            }
        }else if(isDouble(input)){
            double data = Double.parseDouble(input);
            dataStack.push(data);
            operationStack.push(Collections.singletonMap(CMD.push,  new LinkedList<>(Arrays.asList(data))));
        }else if(input.equals(CMD.pop.name())){
            if(dataStack.isEmpty()){
                logger.log(Level.INFO, "Stack is empty. Nothing to pop.");
            }else{
                double data = dataStack.pop();
                operationStack.push(Collections.singletonMap(CMD.pop,  new LinkedList<>(Arrays.asList(data))));
            }
        }else if(input.equals(CMD.clear.name())){
            if(dataStack.isEmpty()){
                logger.log(Level.INFO, "Stack is empty. Nothing to clear.");
            }else{
                operationStack.push(Collections.singletonMap(CMD.clear, copyToArray(dataStack)));
                dataStack.clear();
            }
        }else if(input.equals(CMD.add.name())){
            if(dataStack.size() < 2){
                logger.log(Level.WARNING, "Not enough elements in stack to add.");
            }else{
                double element1= dataStack.pop();
                double element2 = dataStack.pop();
                dataStack.push(element1 + element2);
                operationStack.push(Collections.singletonMap(CMD.add,  new LinkedList<>(Arrays.asList(element1, element2))));
            }
        }else if(input.equals(CMD.mul.name())){
            if(dataStack.size() < 2){
                logger.log(Level.WARNING, "Not enough elements in stack to multiply.");
            }else{
                double element1= dataStack.pop();
                double element2 = dataStack.pop();
                dataStack.push(element1 * element2);
                operationStack.push(Collections.singletonMap(CMD.mul,  new LinkedList<>(Arrays.asList(element1, element2))));
            }
        }else if(input.equals(CMD.neg.name())){
            if(dataStack.isEmpty()){
                logger.log(Level.WARNING, "Stack is empty. Cannot negate. ");
            }else{
                double element= dataStack.pop();
                dataStack.push(element * -1);
                operationStack.push(Collections.singletonMap(CMD.neg, new LinkedList<>(Arrays.asList(element))));
            }
        }else if(input.equals(CMD.inv.name())){
            if(dataStack.isEmpty()){
                logger.log(Level.WARNING, "Stack is empty. Cannot invert. ");
            }else{
                double element= dataStack.pop();
                dataStack.push(1/ element);
                operationStack.push(Collections.singletonMap(CMD.inv, new LinkedList<>(Arrays.asList(element))));
            }
        } else if(input.equals(CMD.undo.name())){
            if(operationStack.isEmpty()){
                logger.log(Level.WARNING, "Nothing to undo");
            }else{
                processUndo();
            }
        } else if(input.equals(CMD.print.name())) {
            System.out.println(dataStack);
            logger.log(Level.INFO, "Stack Elements : {0}", dataStack);
        } else if(input.equals(CMD.help.name())) {
            processHelp();
        } else {
            logger.log(Level.INFO, "Can not recognise the command : ''{0}'' . Enter ''Help'' to get the list of commands.", input);
        }
    }

    public static void main (String[] args) {
        logger.log(Level.INFO,"** Welcome to Stack Machine ** ");
        logger.log(Level.INFO,"Please enter command:");
        Scanner sc = new Scanner(System.in);

        while(sc.hasNextLine()) {
            String input = sc.nextLine().toLowerCase();
            if(input.equals(CMD.quit.name())){
                break;
            }
            processInput(input);
        }
        logger.log(Level.INFO,"** Thanks for using Stack Machine. Quitting now. ** ");
    }
}
