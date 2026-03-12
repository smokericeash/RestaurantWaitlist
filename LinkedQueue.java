public class LinkedQueue<T> implements QueueInterface<T> {
    private Node firstNode;
    private Node lastNode;

    public LinkedQueue(){
        firstNode=null;
        lastNode=null;
    }

    public void enqueue(T newEntry){
        Node newNode = new Node(newEntry);
        if(firstNode==null && lastNode==null){
            firstNode = newNode;
            lastNode = newNode;
        }
        else{
            lastNode.setNext(newNode);
            lastNode = newNode;
        }
    }

    public T dequeue(){
        if(firstNode==null){
            return null;
        }
        T data = firstNode.getData();
        firstNode = firstNode.getNext();
        if(firstNode==null){
            lastNode=null;
        }
        return data;
    }

    public T getFront(){
        if(firstNode==null){
            return null;
        }
        return firstNode.getData();
    }

    public boolean isEmpty(){
        boolean result = false;
        if(firstNode == null && lastNode == null){
            result = true;
        }
        return result;
    }

    public void clear(){ //set both Nodes to null so Java's garbage collection can clear the rest, as nothing points to the linkedData after
        firstNode=null;
        lastNode=null;
    }

    private class Node {
        private Node next;
        private T data;

        Node(T data){
            this.data = data;
            this.next = null;
        }

        T getData(){
            return data;
        }

        void setData(T data){
            this.data = data;
        }

        Node getNext(){
            return next;
        }

        void setNext(Node next){
            this.next = next;
        }

    }
}
