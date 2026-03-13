public class Table {
    private int tableID;
    private int seats;
    private boolean isAvailable;
    private int availableAtTime;
    private int[] combineableWith;

    public Table(int tableID, int seats, int[] combineableWith){ //constructor to intialize table
        this.tableID = tableID;
        this.seats = seats;
        this.combineableWith = combineableWith;
        this.isAvailable = true;
        this.availableAtTime = 0;
    }
    
    public Table(){
        this.tableID = -1;
        this.seats = 0;
        this.combineableWith = null;
        this.isAvailable = true;
        this.availableAtTime = -1;
    }

    public int getTableID(){ 
        return tableID;
    }
    public int getSeats(){
        return seats; 
    }
    public boolean isAvailable(){
        return isAvailable; 
    }
    public int getAvailableAtTime(){
        return availableAtTime;
    }
    public int[] getCombinableWith(){
        return combineableWith; 
    }

    public void setAvailable(boolean isAvailable){
        this.isAvailable = isAvailable;
    }

    public void setAvailableAtTime(int availableAtTime){
        this.availableAtTime = availableAtTime;
    }


}
