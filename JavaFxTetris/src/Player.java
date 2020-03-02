public class Player {
    private String name;
    private int score;
    private int rank;
    private long time;

    Player(String name, int score,long time){
        this.name = name;
        this.score = score;
        this.time = time;
    }

    String getName(){
        return name;
    }

    int getScore(){
        return score;
    }

    long getTime() {return time;}

    int getRank(){
        return rank;
    }

    void setRank(int rank){
        this.rank = rank;
    }

    private String getTimeRank(){
        int hour = (int) time / (60 * 60 * 1000);
        int minute = ((int) time / 60000) % 60;
        int sec = ((int) time / 1000) % 60;
        return String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(sec);
    }

    public String toStringScore(){
        return getName() + " " + getScore();
    }

    public String toStringTime(){
        return getName() + " " + getTimeRank();
    }
}
