package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.core.Vertx;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceList {

    HashMap<String, Service> services = new HashMap<String, Service>();
    private DBConnector connector;

    ServiceList(){
        Vertx vertx = Vertx.vertx();
        connector = new DBConnector(vertx);
        addService("https://www.kry.se", "LIVI");
        connector.query("SELECT * from service").setHandler(done -> {
            if(done.succeeded()){
              System.out.println("loaded services");
              List<JsonObject> savedServices = done.result().getRows();
              for (JsonObject s : savedServices) {
                String url = s.getString("url");
                Service service = new Service( url, s.getString("name"), s.getString("addedAt"));
                services.put(url, service);
              }
            } else {
              done.cause().printStackTrace();
            }
        });
    }

    public void addService(String url, String name){
        String addedAt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        System.out.println(addedAt);
        Service service = new Service(url, name, addedAt);
        services.put(url, service);
        saveService(service);
    }

    public void removeService(String url){
      System.out.println("removing service url : " + url);
      services.remove(url);
      JsonArray params = new JsonArray().add(url);
      connector.update("DELETE FROM service WHERE url = ?", params).setHandler(done -> {
        if (done.succeeded()) {
          System.out.println("Removed service url : " + url);
      
        } else {
          done.cause().printStackTrace();
        }
      });
    }

    public void saveService(Service service){
        String update = "INSERT OR REPLACE INTO service VALUES (?,?,?)";
        JsonArray params = new JsonArray().add(service.url).add(service.name).add(service.addedAt);
        connector.update(update, params).setHandler(done -> {
            if(done.succeeded()){
                UpdateResult result = done.result();
                System.out.println("Updated no. of rows: " + result.getUpdated());
                System.out.println(" saved service " + "name: " + service.name + " url: " + service.url + " addedAt: " + service.addedAt);
              } else {
                done.cause().printStackTrace();
              }
        });
    }

}
