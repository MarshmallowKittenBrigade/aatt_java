/**
 * aatt JAVA library
 * @author DoriftoShoes
 * @version .1
 * @date 9/9/2013
**/
package org.marshmallowkittens.aatt_java;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class Aatt {
  private String syncUrl;
  private JSONObject post;
  private JSONObject records;
  private JSONObject checks;
  private JSONObject auth;
  private JSONObject data;
  private JSONObject actions;
  private JSONObject register;
  private String act;
  private String status;
  private HttpParams params;
  private boolean debug;
  
  public Aatt(){
    this.setSyncUrl("https://jarvis.marshmallowkittens.org/sync/");
    post = new JSONObject();
    records = new JSONObject();
    checks = new JSONObject();
    auth = new JSONObject();
    data = new JSONObject();
    actions = new JSONObject();
    register = new JSONObject();
    status = "";
    debug = false;
    
  }
  
  public void setDebug(boolean value){
	  this.debug = value;
  }
  
  public boolean setSyncUrl(String url){
    this.syncUrl = url;
    if(this.debug){
    	System.out.println("The Sync URL was set to: "+ this.syncUrl);
    }
    return true;
  }
  
  public boolean setAccount(int acctId,String acctKey){
    this.auth.put("APP","aatt_java");
    this.auth.put("ACCOUNT",acctId);
    this.auth.put("KEY",acctKey);
    if(this.debug){
    	System.out.println("Creds: "+this.auth.toString());
    }
    return true;
  }
  
  public boolean setDevice(int deviceId){
    this.data.put("DEVICE",deviceId);
    if(this.debug){
    	System.out.println("The devicd ID was set to: "+ deviceId);
    }
    return true;
  }
  
  public boolean setAct(String act){
    if(act.equals("RECORD") || act.equals("CHECK") || act.equals("ACTION") || act.equals("REGISTER")){
      this.act = act;
      if(this.debug){
      	System.out.println("The ACT was set to: "+ act);
      }
      return true;
    }else{
      this.act = "BADACT";
      if(this.debug){
      	System.out.println("Bad Activity");
      }
      return false;
    }
  }
  
  public boolean record(int item, String value){
    this.records.put(Integer.toString(item),value);
    if(this.debug){
    	System.out.println("RECORD - Item:"+item+" Value:"+value);
    }
    return true;
  }
  
  public boolean check(int item){
    this.checks.put(Integer.toString(item),Integer.toString(item));
    if(this.debug){
    	System.out.println("CHECK - Item:"+item);
    }
    return true;
  }
  
  public boolean addAction(int item,String action){
    this.actions.put(Integer.toString(item),action);
    if(this.debug){
    	System.out.println("ACTION - Item:"+item+" Action:"+action);
    }
    return true;
  }
  
  public String compile(){
    this.post.put("AUTH",this.auth);
    switch(this.act){
      case "RECORD":
        this.data.put("RECORDS",this.records);
        break;
      case "CHECK":
        this.data.put("CHECKS",this.checks);
        break;
      case "ACTION":
        this.data.put("ACTION",this.actions);
        break;
      case "REGISTER":
        this.data.put("REGISTER",this.register);
        break;
    }
    this.post.put("DATA",this.data);
    if(this.debug){
    	System.out.println("PAYLOAD: "+this.post.toString());
    }
    return this.post.toString(2);
  }
  
  public String send(){
    if(!this.status.equals("BADACT")){
    	JSONObject result = this.makeHTTPPOSTRequest();
        return result.toString(2);
    }else{
      return this.status;
    }
  }
  
  public JSONObject makeHTTPPOSTRequest() {
	  JSONObject back = new JSONObject();
	  String line = "";
	  String meh = "";
      try {
          HttpClient c = new DefaultHttpClient();        
          HttpPost p = new HttpPost(this.syncUrl);        
          p.setEntity(new StringEntity(this.post.toString(), 
                           ContentType.create("application/json")));

          HttpResponse r = c.execute(p);

          BufferedReader rd = new BufferedReader(new InputStreamReader(r.getEntity().getContent()));
          StringBuilder sb = new StringBuilder();
          while ((line = rd.readLine()) != null) {
			//Parse our JSON response
        	  if(line != null){
        		  meh = line;
        	  }
          }
      }
      catch(IOException e) {
          System.out.println(e);
      }
      back = (JSONObject) JSONSerializer.toJSON(meh);
      return back;
  }    

  public String getSyncUrl() {
		return syncUrl;
  }
  
  public static void main(String arg[]){
  }
  
}