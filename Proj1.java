import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proj1 {

    public static void main(String[] args) {
        BufferedReader br;
        BufferedWriter bw;

        String inputFilePath;
        String outputFilePath;

        try {
            br = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter input file: ");
            inputFilePath = br.readLine();
            if (inputFilePath.contains("\"")) {
                inputFilePath = inputFilePath.replace("\"", "");
            }

            System.out.println("Enter output file: ");
            outputFilePath = br.readLine();
            if (outputFilePath.contains("\"")) {
                outputFilePath = outputFilePath.replace("\"", "");
            }

            System.out.println("Input File: " + inputFilePath);
            System.out.println("Output File: " + outputFilePath);

            File file = new File(outputFilePath);
            if (file.exists()) {
                file.delete();
            }

            br = new BufferedReader(new FileReader(inputFilePath));
            bw = new BufferedWriter(new FileWriter(outputFilePath, true));

            String myString = br.readLine();

            if (myString.startsWith("0 ")) {
                decompress(inputFilePath, outputFilePath);

            } else {
                compress(inputFilePath, outputFilePath);
            }

            br.close();
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void decompress(String inputFilePath, String outputFilePath) {
        System.out.println("File is compressed!");

        String wordRegex = "([a-zA-Z]+)";
        String notWordNumRegex = "([^a-zA-Z\\d]+)";
        String numRegex = "(\\d+)";
        Pattern p = Pattern.compile(wordRegex + "|" + notWordNumRegex + "|" + numRegex);
        int lineCount = 0;
        String myString;
        Matcher m;
        LinkedList myList = new LinkedList();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true));

            while ((myString = br.readLine()) != null) {
                lineCount++;

                if (myString.startsWith("0 Uncompressed")) {
                    break;
                } else if (lineCount == 1 && myString.startsWith("0 ")) {
                    m = p.matcher((myString.substring(2)));
                } else {
                    m = p.matcher((myString));
                }

                while (m.find()) {
                    String myGroup = m.group();

                    if (myGroup.matches(wordRegex)) {
                        myList.add(myGroup);
                        bw.write(myGroup);
                    } else if (myGroup.matches(notWordNumRegex)) {
                        bw.write(myGroup);
                    } else if (myGroup.matches(numRegex)) {
                        // move node to front, print word
                        bw.write(myList.moveToFront(Integer.parseInt(myGroup)));
                    }
                }
                bw.write("\n");
            }
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void compress(String inputFilePath, String outputFilePath) {
        System.out.println("File is not compressed!");

        String wordRegex = "([a-zA-Z]+)";
        String notWordNumRegex = "([^a-zA-Z\\d]+)";
        String numRegex = "(\\d+)";
        Pattern p = Pattern.compile(wordRegex + "|" + notWordNumRegex + "|" + numRegex);
        int lineCount = 0;
        String myString;
        Matcher m;
        LinkedList myList = new LinkedList();

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath, true));

            while ((myString = br.readLine()) != null) {
                lineCount++;

                if (myString.startsWith("0 Uncompressed")) {
                    br.close();
                    bw.close();
                    return;
                } else if (lineCount == 1) {
                    bw.write("0 ");
                    m = p.matcher(myString);
                } else {
                    m = p.matcher(myString);
                }

                while (m.find()) {
                    String myGroup = m.group();

                    if (myGroup.matches(wordRegex)) {
                        bw.write(myList.add(myGroup));
                    } else if (myGroup.matches(notWordNumRegex)) {
                        bw.write(myGroup);
                    }
                }
                bw.write("\n");
            }
            br.close();
            bw.close();

            File inputFile = new File(inputFilePath);
            File outputFile = new File(outputFilePath);

            long inputLength = inputFile.length() - lineCount;
            long outputLength = outputFile.length() - lineCount - 2;

            bw = new BufferedWriter(new FileWriter(outputFilePath, true));
            bw.write("0 Uncompressed: " + inputLength + " bytes;  Compressed: " + outputLength + " bytes");
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


class LinkedList {

    private static int count;
    private Node dummyHead;

    // constructor
    public LinkedList() {

    }

    // checks if data is in the list. If yes, move to front. If no, add to front.
    public String add(Object data) {

        if (dummyHead == null) {
            // list is empty, so add data to 1st node

            dummyHead = new Node("dummy");

            Node tempNode = new Node(data);

            dummyHead.setNext(tempNode);

            incrementCount();

            return data.toString();
        } else {
            // list is not empty. get index of node containing [data]

            int index = getIndex(data);

            if (index == -1) {
                //object is not in the list, so add it to the beginning

                addToFront(data);
                incrementCount();
                return data.toString();

            } else if (index == 1) {
                // node is already at the front of the list

                return "1";
            } else {
                // node exists, but is not at the front of the list.
                // move it to the front and return its previous index.

                moveToFront(index);
                return Integer.toString(index);
            }
        }
    }

    // inserts the object at the front of the list
    private void addToFront(Object data) {
        Node tempNode = new Node(data);

        tempNode.setNext(dummyHead.getNext());

        dummyHead.setNext(tempNode);

        // we added an item to the list, so increment the number of nodes
        incrementCount();
    }

    // moves the node at specified index from its current position to the front
    public String moveToFront(int index) {

        // return if the index is out of range
        if (index < 1 || index > count)
            return "ERROR: Out of bounds";

        // traverse the list until you find node at desired index
        Node currentNode = dummyHead;
        if (dummyHead != null) {
            for (int i = 1; i < index; i++) {
                if (currentNode.getNext() == null) {
                    return "ERROR: Index not found";
                }
                currentNode = currentNode.getNext();
            }

            Node tempNode = currentNode.getNext();

            currentNode.setNext(currentNode.getNext().getNext());

            tempNode.setNext(dummyHead.getNext());

            dummyHead.setNext(tempNode);

            return tempNode.data.toString();
        }
        return "ERROR: Other problem";
    }

    // returns the index of the first element that matches "data"
    private int getIndex(Object data) {
        Node currentNode = dummyHead.getNext();

        for (int i = 1; i < count + 1; i++) {

            if (currentNode.data.toString().equals(data.toString())) {
                return i;
            } else if (currentNode.getNext() == null) {
                return -1;  // data is not in the list
            }
            currentNode = currentNode.getNext();
        }

        return 0;
    }

    private static void incrementCount() {
        count++;
    }

    private class Node {
        // reference to the next node in the list, or null if it's the end of the list
        Node next;

        // data stored in the node
        Object data;

        // constructor
        public Node(Object value) {
            next = null;
            data = value;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node nextValue) {
            next = nextValue;
        }

    }
}
