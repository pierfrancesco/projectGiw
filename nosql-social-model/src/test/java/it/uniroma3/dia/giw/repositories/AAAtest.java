package it.uniroma3.dia.giw.repositories;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweet;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.Entities;
import it.uniroma3.dia.giw.model.twitter.data.HashTag;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.Url;
import it.uniroma3.dia.giw.model.twitter.data.User;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import com.google.gson.Gson;

public  class AAAtest {


    
    protected List<String> savedMonitoringActivities = new ArrayList<String>();

        public static JSONObject obj = new JSONObject();
    public static JSONObject main = new JSONObject();
    public static int k=0;
    
    public static List<Tweet> twlist = new LinkedList<Tweet>(); 
    public static List<Long> idList = new LinkedList<Long>();
    
    /*protected abstract Logger getLogger();
    
    protected abstract void initRepositories() throws Exception;
    
    protected abstract void tearDownRepositories();
    
    protected abstract void cleanIndices() throws Exception;*/

    public  JSONObject giveMeTweets (){
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
 
    try {
 
        Object obj = parser.parse(new FileReader("c:\\Users\\pierfrancesco\\tweet0.json"));
 
        jsonObject = (JSONObject) obj;
 
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (ParseException e) {
        e.printStackTrace();
    }
 

    return jsonObject;
     }
    


    @Test(enabled = true)
        public void pierfrancesco() throws InputRepositoryException {
            final MonitoringActivityId monitoringActivityId = new MonitoringActivityId("123");
            System.out.println("sono in pierfrancesco");
            JSONObject tweetExternal = giveMeTweets();
            Long id = (Long) tweetExternal.get("id");
            final Tweet tweet = new Tweet();
            tweet.setId(id);
            
            //this.writeRepository.storeToStream(tweet, monitoringActivityId);
            System.out.println(tweet);


    }

    
}
