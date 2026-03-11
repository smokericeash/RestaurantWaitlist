public class WaitListQueue<T> implements QueueInterface<T> {
    private Node firstNode;
    private Node lastNode;


    public void enqueue(T newEntry){

    }

    public T dequeue(){

    }

    public T getFront(){

    }

    public boolean isEmpty(){

    }

    public void clear(){

    }

    private class Node {
        private Node next;
        private T data;

        Node(T data){
            this.data = data;
            this.next = null;
        }

        public T getData(){
            return data;
        }

        public void setData(T data){
            this.data = data;
        }

        public Node getNext(){
            return next;
        }

        public void setNext(Node next){
            this.next = next;
        }

    }
}
