package beans;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class Event {
    private int id;
    private Client client;
    private User user;
    private long date;
    private long dateCreate;
    private long dateUpdate;
    private String memo;
    private int tbl;
    private int guests;
    private int guestsExtra;
    private int status;
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGuestsExtra() {
        return guestsExtra;
    }

    public void setGuestsExtra(int guestsExtra) {
        this.guestsExtra = guestsExtra;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Client getClient() {
        return client;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(long dateCreate) {
        this.dateCreate = dateCreate;
    }

    public long getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(long dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getTbl() {
        return tbl;
    }

    public void setTbl(int tbl) {
        this.tbl = tbl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
