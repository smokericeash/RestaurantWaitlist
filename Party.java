public class Party {
    private String name;
    private int partySize;
    private int arrivalTime;
    private int seatingTime;

    public Party(String name, int partySize, int arrivalTime){
        this.name = name;
        this.partySize = partySize;
        this.arrivalTime = arrivalTime;
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

    public int getSeatingTime(){
        return seatingTime;
    }

    public int getWaitTime(){
        return seatingTime - arrivalTime;
    }

    public int getArrivalTime(){
        return arrivalTime;
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
