
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.org.json.JSONArray;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twitter4j.json.DataObjectFactory;

/**
* <p>This is a code example of Twitter4J Streaming API - sample method support.<br>
* Usage: java twitter4j.examples.PrintSampleStream<br>
* </p>
*
* @author Yusuke Yamamoto - yusuke at mac.com
*/
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
    		if(idList.size() == 7){
    			
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
    				
    				
    			//}
    			k++;
    			try {
    				String x = new Gson().toJson(twlist);
    				System.out.println("Keep on rollin"+x);
    				try {
						main.put("doc"+k, x);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				FileWriter file = new FileWriter("C:\\Users\\pierfrancesco\\Desktop\\tweet"+k+".json",true);
    				file.write("\n"+main+"\n");
    				file.flush();
    				file.close();
    				twlist.clear();
    				idList.clear();
    				System.out.println("Keep on rollinBaby"+twlist.size());
    				
    				
    		 
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
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
        

	


        
    }
}