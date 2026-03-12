public class LinkedQueue<T> implements QueueInterface<T> {
    private Node firstNode;
    private Node lastNode;

    public LinkedQueue(){
        firstNode=null;
        lastNode=null;
    }

    private void enqueue(T newEntry){

    }

    private T dequeue(){

    }

    private T getFront(){

    }

    private boolean isEmpty(){

    }

    private void clear(){

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
