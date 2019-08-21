package se.kry.codetest;

public class Service {
    String url;
    String name;
    String addedAt;
    Status status = Status.UNKOWN;

    enum Status {
        UNKOWN,
        FAIL,
        OK
      }

    Service(String url, String name, String addedAt){
        this.url = url;
        this.name = name;
        this.addedAt = addedAt;
    }

    public void setStatus(Status status){
        this.status = status;
    }
}