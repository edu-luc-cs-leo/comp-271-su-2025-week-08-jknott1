/**
 * A simple hasht table is an array of linked lists. In its simplest form, a
 * linked list is represented by its first node. Typically we label this node as
 * "head". Here, however, we'll know it's the first node of the list because it
 * will be placed in an array element. For example, if we have 4 linked lists,
 * we know that the "head" of the third one can be found in position [2] of the
 * underlying array.
 */
public class HashTable<E extends Comparable<E>> {

    /**
     * Underlying array of nodes. Each non empty element of this array is the first
     * node of a linked list.
     */
    private Node<E>[] underlying;

    /** Counts how many places in the underlying array are occupied */
    private int usage;

    /** Counts how many nodes are stored in this hashtable */
    private int totalNodes;

    /** Tracks underlying array's load factor */
    private double loadFactor;

    /**
     * Default size for the underlying array.
     */
    private static final int DEFAULT_SIZE = 4;

    /** Default load factor threshold for resizing */
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    /** Default resize factor */
    private final int REFACTOR_SIZE = 2;

    /**
     * Basic constructor with user-specified size. If size is absurd, the
     * constructor will revert to the default size.
     */
    public HashTable(int size) {
        if (size <= 0)
            size = DEFAULT_SIZE;
        this.underlying = new Node[size];
        this.usage = 0;
        this.totalNodes = 0;
        this.loadFactor = 0.0;
    } // basic constructor

    /** Default constructor, passes defauilt size to basic constructor */
    public HashTable() {
        this(DEFAULT_SIZE);
    } // default constructor

    /**
     * Adds a node, with the specified content, to a linked list in the underlying
     * array.
     * 
     * @param content E The content of a new node, to be placed in the array.
     */
    public void add(E content) {
        // if the list is overloaded, expands the array before adding
        if (!this.addLoadCheck()) {
            // doubles the size of the list (default refactor)
            this.refactor();
        }
        // Create the new node to add to the hashtable
        Node<E> newNode = new Node<E>(content);
        // Use the hashcode for the new node's contents to find where to place it in the
        // underlying array. Use abs in case hashcode < 0.
        int position = Math.abs(content.hashCode()) % this.underlying.length;
        // Check if selected position is already in use
        if (this.underlying[position] == null) {
            // Selected position not in use. Place the new node here and update the usage of
            // the underlying array.
            this.underlying[position] = newNode;
            this.usage += 1;
        } else {
            // Selected position in use. We will append its contents to the new node first,
            // then place the new node in the selected position. Effectively the new node
            // becomes the first node of the existing linked list in this position.
            newNode.setNext(this.underlying[position]);
            this.underlying[position] = newNode;
        }
        // Update the number of nodes
        this.totalNodes += 1;
        // updates the load factor of the underlying list
        this.reLoad();
    } // method add

    /**
     * increases the size of the underlying array by the specified factor.
     * Then assigns the contents of the old hash table to the new one depending on
     * the bin.
     * @param size - factor by which the list is resized
     */

    public void refactor(int size) {
        // Initialize variables:
        // Size of the new underlying array depends on the size of the old
        // Array and the resize factor.
        int newSize = this.underlying.length * size;
        // variable to track cursor's content's index in the new hash table.
        int newIndex;
        // New temporary hashtable with the specified size.
        HashTable<E> temp = new HashTable<E>(newSize);
        // Cursor node object - traverses through linked lists.
        Node<E> cursor;
        // Can't rely on get next to keep track of the next node in the old list, so
        // This keeps track of the next node in the old array.
        Node<E> nextNode;
        // For loop traverses the array, ensuring every element is checked.
        for (int i = 0; i < this.underlying.length; i++) {
            // cursor starts at the head of every linked list.
            cursor = this.underlying[i];
            // While loop traverses the linked list attached to the current head. Stops when the
            // cursor is null. Only executes if the element is not null.
            while (cursor != null) {
                // Sets the next node as the next in the list.
                nextNode = cursor.getNext();
                // the new index is calculated using the modulus of the absolute value of it's content's hash code
                newIndex = Math.abs(cursor.getContent().hashCode()) % newSize;
                // links the cursor to the requisite linked list; the cursor is the new head.
                cursor.setNext(temp.underlying[newIndex]);
                // fixes the cursor to its appropriate place in the new underlying array.
                temp.underlying[newIndex] = cursor;
                cursor = nextNode;
            }
        }
        // replaces the old hash table with the new one.
        this.underlying = temp.underlying;
        // defaults the usage to zero -
        this.usage = 0;
        // - but recounts it using a for loop.
        for (int i = 0; i < this.underlying.length; i++) {
            if (this.underlying[i] != null) {
                this.usage += 1;
            }
        }
    }

    /**
     * overloaded method for refactor using a default value
     */

    public void refactor() {
        refactor(REFACTOR_SIZE);
    }

    /**
     * Searches the underlying array of linked lists for the target value. If the
     * target value is stored in the underlying array, the position of its
     * corresponding linked list can be obtained immediately through the target's
     * hashcode. The linked list must then be traversed to determine if a node with
     * similar content and the target value is present or not.
     * 
     * @param target E value to searc for
     * @return true if target value is present in one of the linked lists of the
     *         underlying array; false otherwise.
     */

    public boolean contains(E target) {
        // initialize a return value and assumes that the value isn't found
        boolean found = false;
        // initialize an index value. If the specified target is in the list, then it should follow that
        // it could only be found at the node at the element responding to its hash code
        int targetIndex = Math.abs(target.hashCode()) % this.underlying.length;
        Node cursor = this.underlying[targetIndex];
        // while loop traverses the linked list
        while (cursor != null) {
            // if a node matching the target is found, changes the return value to true
            if (cursor.getContent() == target){
                found = true;
            }
            // updates cursor
            cursor = cursor.getNext();
        }
        // return the cursor
        return found;
    } // method contains

    /**
     * Calculates the load factor of the current hashtable. Thought this would be useful in other
     * circumstances as well.
     */
    public void reLoad() {
        // assigns the value of use to a double variable (for math)
        double use = this.usage;
        // changes the load factor to reflect current load.
        this.loadFactor =  use/this.underlying.length;
    } // method reLoad

    /**
     * Checks to see if a hash table needs to be refactored if a new node is added. For readability.
     * @return true if the load factor with an additional node is less than or equal to
     * the load factor threshold.
     */

    public boolean addLoadCheck(){
        return ((this.usage + 1)/this.underlying.length <= LOAD_FACTOR_THRESHOLD);
    }

    /** Constants for toString */
    private static final String LINKED_LIST_HEADER = "\n[ %2d ]: ";
    private static final String EMPTY_LIST_MESSAGE = "null";
    private static final String ARRAY_INFORMATION = "Underlying array usage / length: %d/%d";
    private static final String NODES_INFORMATION = "\nTotal number of nodes: %d";
    private static final String NODE_CONTENT = "%s --> ";

    /** String representationf for the object */
    public String toString() {
        // Initialize the StringBuilder object with basic info
        StringBuilder sb = new StringBuilder(
                String.format(ARRAY_INFORMATION,
                        this.usage, this.underlying.length));
        sb.append(String.format(NODES_INFORMATION, this.totalNodes));
        // Iterate the array
        for (int i = 0; i < underlying.length; i++) {
            sb.append(String.format(LINKED_LIST_HEADER, i));
            Node head = this.underlying[i];
            if (head == null) {
                // message that this position is empty
                sb.append(EMPTY_LIST_MESSAGE);
            } else {
                // traverse the linked list, displaying its elements
                Node cursor = head;
                while (cursor != null) {
                    // update sb
                    sb.append(String.format(NODE_CONTENT, cursor));
                    // move to the next node of the ll
                    cursor = cursor.getNext();
                } // done traversing the linked list
            } // done checking the current position of the underlying array
        } // done iterating the underlying array
        return sb.toString();
    } // method toString

} // class HashTable

