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
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twitter4j.json.DataObjectFactory;

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



public final class PrintSampleStream {
	
	public static JSONObject obj = new JSONObject();
	public static JSONObject main = new JSONObject();
	public static int k=0;
	public static List<Tweet> twlist = new LinkedList<Tweet>(); 
	public static List<Long> idList = new LinkedList<Long>();
	public static Random randomGenerator = new Random();
	
    
    public static StatusListener listener = new StatusListener() {
        @Override
        public void onStatus(Status status) {
        	//System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
   		 	twitter4j.User user = status.getUser();
   		 	
            // gets Username
   		 	//System.out.println("Questo è l\' user"+user);
            //String username = status.getUser().getScreenName();
            //System.out.println("Questo è l\' userName"+username);
            String profileLocation = user.getLocation();
            System.out.println("Locazione:_"+profileLocation);
            long tweetId = status.getId(); 
            //System.out.println(tweetId);
            String content = status.getText();
            Date dateCreated = status.getCreatedAt();
            String source = status.getSource();
            
    		it.uniroma3.dia.giw.model.twitter.data.User usr = new it.uniroma3.dia.giw.model.twitter.data.User(user.getId(),user.getName());
    		usr.setLocation(user.getLocation());
    		usr.setFollowersCount(user.getFollowersCount());
    		Tweet tw = new Tweet(tweetId,dateCreated,content,source,usr);
    		twlist.add(tw);
    		idList.add(user.getId());
    		if(idList.size() == 5){
    			
    			//for(int k=0; k<idList.size(); k++){
    				//Long id = idList.get(k);
    				for(int t=0; t<twlist.size()-1; t++){
    					
    					//int r = (int)Math.random() *twlist.size();
    					System.out.println("GETUSER"+twlist.get(t).getUser());
    					final List<it.uniroma3.dia.giw.model.twitter.data.User> pippo = new ArrayList<it.uniroma3.dia.giw.model.twitter.data.User>();
    					pippo.clear();
    					System.out.println("pippo è lungo!"+pippo.size());
    					/*while(r != 0){
    						
    						it.uniroma3.dia.giw.model.twitter.data.User usr3 = twlist.get(r).getUser();
    						r--;
        					pippo.add(usr3);
        					System.out.println("user3"+usr3);
        					//usr.setFollowers(pippo);
        					
    					}*/
    					for(int k=0; k<idList.size(); k++){
    	    				Long id = idList.get(k);
    	    				it.uniroma3.dia.giw.model.twitter.data.User usr3 = new User();
    	    				usr3.setId(id);
    	    				pippo.add(usr3);
    					}
    					
    					twlist.get(t).getUser().setFollowers(pippo);
    					System.out.println("pippo è lungo!"+pippo.size());
    					System.out.println("pippo è!"+pippo);
    					System.out.println("ciccio!"+twlist.get(t).getUser().getFollowers());
    					
    					//System.out.println(twlist.get(t).getUser());
    					//twlist.get(t).getUser().setFollowers(pippo);
    					

    				}
    				 
    				//ObjectMapper objectMapper = new ObjectMapper();
    				//ElasticSearchInputRepository input = new ElasticSearchInputRepository(localClientNode, objectMapper);
    				/*MonitoringActivityId monitor = new MonitoringActivityId("123465");
    				try {
						input.storeToStream(twlist, monitor);
					} catch (InputRepositoryException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}*/
    				
    			//}
    			k++;
    			String x = new Gson().toJson(twlist);
				System.out.println("Keep on rollin"+x);
				try {
					main.put("doc"+k, x);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*FileWriter file = new FileWriter("C:\\Users\\pierfrancesco\\Desktop\\tweet"+k+".json",true);
				file.write("\n"+main+"\n");
				file.flush();
				file.close();*/
				/*ElasticSearchInputRepository go = new ElasticSearchInputRepository(localClientNode, objectMapper);
				try {
					go.storeToStream(twlist, MONITORING_ACTIVITY_ID);
				} catch (InputRepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				twlist.clear();
				idList.clear();
				System.out.println("Keep on rollinBaby"+twlist.size());
    		}
    		


            
        }

        @Override
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        }

        @Override
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
        }

        @Override
        public void onScrubGeo(long userId, long upToStatusId) {
            //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
        }

        @Override
        public void onStallWarning(StallWarning warning) {
            //System.out.println("Got stall warning:" + warning);
        }

        @Override
        public void onException(Exception ex) {
            ex.printStackTrace();
        }
    };

    
    //twitterStream.sample();

	/*public static void value() throws JSONException {
		
		
		}*/
		/**
* Main entry of this application.
*
* @param args
*/
    public static void main(String[] args) throws TwitterException {
        
    	TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    	twitterStream.addListener(listener);
        
    	FilterQuery filter = new FilterQuery();
        
        String[] keywordsArray = {"Italia"};
        filter.track(keywordsArray);
        twitterStream.filter(filter);
        

	   System.out.println("prova");


        
    }
}