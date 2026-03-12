public class Party {
    private String name;
    private int partySize;
    private int arrivalTime;
    private int seatingTime;

    public Party(String name, int partySize, int arrivalTime, int seatingTime){
        this.name = name;
        this.partySize = partySize;
        this.arrivalTime = arrivalTime;
        this.seatingTime = -1;
    }

    public Party(){
        this.name = "";
        this.partySize = 0;
        this.arrivalTime = 0;
        this.seatingTime = -1;
    }

    public String getName(){
        return name;
    }

    public int getPartySize(){
        return partySize;
    }

    public int arrivalTime(){
        return arrivalTime;
    }

    public int seatingTime(){
        return seatingTime;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPartySize(int partySize){
        this.partySize = partySize;
    }

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    public void setSeatingTime(int seatingTime){
        this.seatingTime = seatingTime;
    }


}
