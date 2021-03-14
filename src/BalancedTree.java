public class BalancedTree{

    Node root = null;

    public BalancedTree(){}


    // update key according to equal biggest key of its children
    private void updateKey(Node node){
        node.size = 0;

        if (node.l != null) {
            node.key = node.l.key;
            node.size += node.l.size;
            node.value = node.l.value.createCopy();
        }

        if (node.m != null){
            node.key = node.m.key;
            node.size += node.m.size;
            node.value.addValue(node.m.value);

        }
        if (node.r != null){
            node.key = node.r.key;
            node.size += node.r.size;
            node.value.addValue(node.r.value);
        }
    }


    // set the children of a parent node
    private void setChildren(Node parentNode , Node l, Node m, Node r){
        parentNode.l = l;
        parentNode.m = m;
        parentNode.r = r;

        if (l != null) l.parent = parentNode;
        if (m != null) m.parent = parentNode;
        if (r != null) r.parent = parentNode;

        updateKey(parentNode);
    }


    // used to insert a new leaf
    private Node insertAndSplit(Node parentNode, Node newNode){
        if (parentNode.r == null){ // if parent has 2 children
            if(newNode.key.compareTo(parentNode.l.key) < 0)
                setChildren(parentNode, newNode, parentNode.l, parentNode.m);

            else if(newNode.key.compareTo(parentNode.m.key) < 0)
                setChildren(parentNode, parentNode.l, newNode, parentNode.m);

            else
                setChildren(parentNode, parentNode.l, parentNode.m, newNode);

            return null;
        }

        // else parent has 3 children then do
        Node newParent = new Node();
        if(newNode.key.compareTo(parentNode.l.key) < 0) {
            setChildren(newParent, parentNode.m, parentNode.r, null);
            setChildren(parentNode, newNode, parentNode.l, null);
        }
        else if (newNode.key.compareTo(parentNode.m.key) < 0){
            setChildren(newParent, parentNode.m, parentNode.r,null);
            setChildren(parentNode, parentNode.l, newNode,null);
        }
        else if(newNode.key.compareTo(parentNode.r.key) < 0){
            setChildren(newParent,newNode,parentNode.r,null);
            setChildren(parentNode, parentNode.l, parentNode.m,null);
        }
        else {
            setChildren(newParent, parentNode.r, newNode,null);
            setChildren(parentNode, parentNode.l,parentNode.m,null);
        }
        return newParent;
    }


    // insert to tree
    public void insert(Key newKey, Value newValue){
        Node newLeaf = new Node(newKey, newValue);
        if (root == null){ // tree is empty
            root = new Node();
            setChildren(root, newLeaf, null, null);
        }
        else if(root.m == null){ // tree has only one value
            if (root.l.key.compareTo(newLeaf.key) < 0)
                setChildren(root, root.l , newLeaf, null);
            else
                setChildren(root, newLeaf,  root.l, null);
            updateKey(root);
        }
        else{ // for every Node there are 2-3 children
            // search downwards for the correct parent of newLeaf
            Node currentNode = downwardSearch(newLeaf.key);
            Node newParent = insertAndSplit(currentNode, newLeaf);

            while (currentNode != root){
                currentNode = currentNode.parent;
                if(newParent != null)
                    newParent = insertAndSplit(currentNode, newParent);

                else updateKey(currentNode);
            }

            if (newParent != null){
                Node newRoot = new Node();
                setChildren(newRoot, currentNode, newParent, null);
                root = newRoot;
            }
        }
    }


    // used to delete a node in case we have a parent-node with a single child
    public Node borrowOrMerge(Node currentNode){
        Node parent = currentNode.parent;
        // current is the left child
        if(currentNode == parent.l){
            Node rightSibling = parent.m;
            // borrow since right sibling has 3 children
            if (rightSibling.r != null){
                setChildren(currentNode, currentNode.l, rightSibling.l, null);
                setChildren(rightSibling, rightSibling.m, rightSibling.r, null);
            }
            // merge since right sibling has 2 children
            // parent might have 1 child after this
            else{
                setChildren(rightSibling,currentNode.l,rightSibling.l,rightSibling.m);
                setChildren(parent,rightSibling,parent.r,null);
            }
            return parent;
        }
        // current is the middle child
        if(currentNode == parent.m){
            Node leftSibling = parent.l;
            // borrow since left sibling has 3 children
            if (leftSibling.r != null){
                setChildren(currentNode, leftSibling.r, currentNode.l,null);
                setChildren(leftSibling, leftSibling.l, leftSibling.m, null);
            }
            // merge since left sibling has 2 children
            // parent might have 1 child after this
            else{
                setChildren(leftSibling,leftSibling.l,leftSibling.m,currentNode.l);
                setChildren(parent,leftSibling,parent.r,null);
            }
            return parent;
        }
        // current is the right child if we get here
        Node leftSibling = parent.m;
        // borrow since left sibling has 3 children
        if (leftSibling.r != null){
            setChildren(currentNode, leftSibling.r, currentNode.l,null);
            setChildren(leftSibling, leftSibling.l, leftSibling.m, null);
        }
        // merge since left sibling has 2 children
        // parent might have 1 child after this
        else{
            setChildren(leftSibling,leftSibling.l,leftSibling.m,currentNode.l);
            setChildren(parent,parent.l,leftSibling,null);
        }
        return parent;
    }


    // delete leaf from tree
    public void delete(Key key){

        Node nodeToDelete = findLeaf(searchParent(key), key);
        if(nodeToDelete == null) return;

        Node parent = nodeToDelete.parent;
        if(nodeToDelete == parent.l) setChildren(parent,parent.m,parent.r,null);
        else if(nodeToDelete == parent.m) setChildren(parent,parent.l,parent.r,null);
        else setChildren(parent,parent.l,parent.m,null);

        if (parent.l == null){
            root = null;
            return;
        }

        while(parent != null){
            if (parent.m == null){
                if (parent != root){
                    parent = borrowOrMerge(parent);
                }
                else if (parent.l != null){
                    root = parent.l;
                    parent.l.parent = null;
                    if (root.l == null){ // we have 1 node in the tree
                        Node newRoot = new Node();
                        setChildren(newRoot, root, null, null);
                        root = newRoot;
                    }
                    return;
                }
            }
            else{
                updateKey(parent);
                parent = parent.parent;
            }
        }
    }


    // find the parent of existing leaf or parent where a new leaf should be inserted
    public Node downwardSearch(Key key) {
        Node currentNode = root;
        while (currentNode.l != null) {
            // go to the left sub-tree
            if (key.compareTo(currentNode.l.key) <= 0 || currentNode.m == null)
                currentNode = currentNode.l;

            // go to the middle sub-tree
            else if (key.compareTo(currentNode.m.key) <= 0 || currentNode.r == null)
                currentNode = currentNode.m;

            // go to the right sub-tree
            else currentNode = currentNode.r;
        }
        return currentNode.parent;
    }


    // search for the parent of a key using function downwardSearch
    public Node searchParent(Key key){
        if (root == null) return null; // tree is empty

        else if(root.m == null) return root; // tree has only one value

        else{ // for every Node there are 2-3 children
            Node currentNode = root;
            if(currentNode.r != null && key.compareTo(currentNode.r.key) > 0)
                return null;

            // search downwards for the correct parent of newLeaf
            return downwardSearch(key);
        }
    }


    // get the lead with a given key of a given parent if exists
    public Node findLeaf(Node parent, Key key){
        if (parent == null) return null;

        if (parent.l != null && parent.l.key.compareTo(key) == 0)
            return (parent.l);

        else if (parent.m != null && parent.m.key.compareTo(key) == 0)
            return (parent.m);

        else if (parent.r != null && parent.r.key.compareTo(key) == 0)
            return (parent.r);

        return null;
    }


    // get a leaf with a given key
    public Value search(Key key){
        Node leaf = findLeaf(searchParent(key), key);
        if (leaf == null) return null;
        return leaf.value.createCopy();
    }


    // get the rank (order statistic) of the given key
    public int rank(Key key){
        Node child = findLeaf(searchParent(key), key);
        if(child == null) return 0;

        Node parent = child.parent;
        if(parent.m == null) return 1;

        int rank = 1;
        while (parent != null){
            if (child == parent.m)
                rank += parent.l.size;
            else if(child == parent.r)
                rank += (parent.l.size + parent.m.size);

            child = parent;
            parent = parent.parent;
        }
        return rank;
    }


    // get the key of a lead in a given index
    public Key select(int index){
        if(root == null || root.size < index || index < 1)
            return null;

        if(root.size == 1 && index == 1) return root.l.key.createCopy();

        Node currentNode = root;

        // search for parent of given index
        while(currentNode.size>3){
            if(currentNode.l.size >= index)
                currentNode = currentNode.l;

            else if(currentNode.l.size + currentNode.m.size >= index){
                index -= currentNode.l.size;
                currentNode = currentNode.m;
            }

            else{
                index -= (currentNode.l.size + currentNode.m.size);
                currentNode = currentNode.r;
            }
        }

        // return copy of key of the matching leaf
        if (index == 1) return currentNode.l.key.createCopy();
        else if (index == 2) return currentNode.m.key.createCopy();
        else return currentNode.r.key.createCopy();
    }


    // find the biggest key which is less or equal to the given key
    public Node findLowerBound(Key key){

        if (root.key.compareTo(key) < 0) return null;

        if (root.m == null) return root.l;

        Node parent = downwardSearch(key);
        Node lowerBound = findLeaf(parent, key);

        if(lowerBound != null) return lowerBound; // key is a leaf
        else{
            if (parent.l.key.compareTo(key) >= 0) {
                return parent.l;
            }
            else if (parent.m.key.compareTo(key) >= 0) {
                return parent.m;
            }
            else if (parent.r != null && parent.l.key.compareTo(key) >= 0) {
                return parent.r;
            }
            else { // bound is in the next parent
                Key lowerBoundKey = select(rank(parent.m.key) + 1);
                return findLeaf(searchParent(lowerBoundKey), lowerBoundKey);
            }
        }

    }


    // find the smallest key which is more or equal to the given key
    public Node findUpperBound(Key key){

        if (key.compareTo(minKey()) < 0) return null;

        if (root.m == null) return root.l;

        Node parent = downwardSearch(key);
        Node upperBound = findLeaf(parent, key);

        if(upperBound != null) return upperBound; // key is a leaf
        else {
            if (parent.r != null && parent.r.key.compareTo(key) <= 0)
                return parent.r;
            else if (parent.m.key.compareTo(key) <= 0)
                return parent.m;
            else if (parent.l.key.compareTo(key) <= 0)
                return parent.l;
            else { // bound is in previous parent
                Key upperBoundKey = select(rank(parent.l.key) - 1);
                return findLeaf(searchParent(upperBoundKey), upperBoundKey);
            }
        }
    }


    // get the smallest key in the tree
    public Key minKey(){
        if (root == null)
            return null;
        Node currentNode = root;

        while(currentNode.l != null)
            currentNode = currentNode.l;

        return currentNode.key.createCopy();
    }


    // sums the all values within the interval of keys
    public Value sumValuesInInterval(Key key1, Key key2){

        if(root == null) return null;


        if (key1.compareTo(key2) > 0){
            return null;
        }

        Node lowerBound = findLowerBound(key1);
        Node upperBound = findUpperBound(key2);

        if (lowerBound == null || upperBound == null || lowerBound.key.compareTo(upperBound.key) > 0)
            return null;

        Value sumValuesInInterval = lowerBound.value.createCopy();

        while(lowerBound.key.compareTo(upperBound.key) != 0){
            // go to right sibling if exists, else go to father
            if (lowerBound.key.compareTo(upperBound.key) < 0){
                // if left child go to middle child
                if (lowerBound == lowerBound.parent.l){
                    lowerBound = lowerBound.parent.m;
                    if (lowerBound.key.compareTo(upperBound.key) <= 0) sumValuesInInterval.addValue(lowerBound.value);
                }
                // if middle child and right child exists go to right child
                else if (lowerBound.parent.r != null && lowerBound == lowerBound.parent.m){
                    lowerBound = lowerBound.parent.r;
                    if (lowerBound.key.compareTo(upperBound.key) <= 0) sumValuesInInterval.addValue(lowerBound.value);
                }
                else lowerBound = lowerBound.parent; // go to parent
            }
            // go to left child and add value if equals
            else{
                lowerBound = lowerBound.l;
                if (lowerBound.key.compareTo(upperBound.key) <= 0) sumValuesInInterval.addValue(lowerBound.value);
            }
        }
        return sumValuesInInterval;
    }


    public class Node {

        public Key key;
        public Node parent;
        public Node l;
        public Node m;
        public Node r;
        public int size = 1;
        public Value value;

        public Node() {}

        public Node(Key key, Value value) {
            this.value = value.createCopy();
            this.key = key.createCopy();
        }
    }
}