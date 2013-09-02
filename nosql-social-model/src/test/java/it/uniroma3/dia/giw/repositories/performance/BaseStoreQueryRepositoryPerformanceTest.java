package it.uniroma3.dia.giw.repositories.performance;

import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;
import java.io.FileNotFoundException;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;


public abstract class BaseStoreQueryRepositoryPerformanceTest {
    
    private static final String PERFORMANCE_DIRECTORY = "target/performance";
    
    private static final String SINGLE_CLOSING_BRACKET = "]";
    
    private static final int ARRAY_TO_DATA_TABLE_CHARS_LENGTH = 17;
    
    private static final int MAX_AUTHORS = 100;
    
    private static final int MAX_HASHTAGS = 100;
    
    private static final MonitoringActivityId MONITORING_ACTIVITY_ID = new MonitoringActivityId("1");
    
    private static final String[] Y_LABELS = new String[] { "Store one tweet", "Get tweet by id",
            "Get screen name occurrences", "Get hashtag occurrences" ,"Pierfrancescos Test2","removeFollowers","storeFollowers","storeFollowing"};
    
    private static final String X_LABEL = "n. of existing tweets in repository";
    
    private final Random random = new Random();
    
    protected InputRepository writeRepository;
    
    protected OutputRepository readRepository;
    
    protected abstract Logger getLogger();
    
    protected abstract void initRepositories() throws Exception;
    
    protected abstract void tearDownRepositories();
    
    protected int[] xData = new int[] { 10, 100, 1000, 10000, 100000 };
    protected Map<String, List<Long>> results = Maps.newHashMap();
    
    protected List<List<Tweet>> inputData = Lists.newLinkedList();
    
    private final int iterations = 5;
    
    @Test
    public void assessPerformance() throws InputRepositoryException, IOException {
    
        System.out.println("Prova1");
        for (int i = 0; i < xData.length; i++) {
            


            int randomIndex = random.nextInt(xData[i] - 1);
            System.out.println("\n\n andomInde"+randomIndex);
            ContextTweetId storedContextTweetId = null;
            for (int storedTweets = 0; storedTweets < xData[i]; storedTweets++) {
                
                ContextTweetId contextTweetID = this.writeRepository.storeToStream(
                        getRandomTweet(), MONITORING_ACTIVITY_ID);
                //qui devo mettere no get random tweet, ma i tweet che ho preso dal repo;
                if (storedTweets == randomIndex) {
                    storedContextTweetId = contextTweetID;
                }
            }
            
            testPerformance(i, storedContextTweetId);
        }
        
        for (String testLabel : results.keySet()) {
            //System.out.println("\n\n\nora sono qui dentro\n\n\n"+testLabel);
            storeVisualization(results.get(testLabel), X_LABEL, testLabel, testLabel, testLabel
                    .toLowerCase().replace(" ", "_"));
        }
    }
    
    private void testPerformance(int xDataIndex, ContextTweetId storedContextTweetId)
            throws InputRepositoryException, IOException {
    
        getLogger().info(
                "Testing performances of a repository containing " + xData[xDataIndex] + " tweets");
        
        //storeFollowing();
        //storeFollowers();
        //removeFollowers();
        //getFollowers();
        //storeOneTweet();
        //getTweetById(storedContextTweetId);
        getScreenNameOccurrences();
        //getHashTagsOccurrences();
        //pierfrancescoTest();
        
        
    }
    
    public void storeOneTweet() throws InputRepositoryException, IOException {
    
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            this.writeRepository.storeToStream(getRandomTweet(), MONITORING_ACTIVITY_ID);
            long endingTime = System.nanoTime();
            allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[0], allResults);
    }
    
    public void getTweetById(ContextTweetId storedContextTweetId) throws InputRepositoryException,
            IOException {
    
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            this.writeRepository.getByIdFromStream(storedContextTweetId);
            long endingTime = System.nanoTime();
            allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[1], allResults);
        
    }
    
    public void getScreenNameOccurrences() throws InputRepositoryException, IOException {
    
        DateTime startDateTime = new DateTime();
        startDateTime.withYear(2011);
        startDateTime.withDayOfMonth(01);
        
        DateTime endDateTime = new DateTime();
        
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            StringOccurrences stringOccurrences = this.readRepository.authorOccurrences(
                    MONITORING_ACTIVITY_ID, startDateTime, endDateTime, MAX_AUTHORS);
            System.out.println(stringOccurrences);
            Assert.assertNotNull(stringOccurrences);
            long endingTime = System.nanoTime();
            allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[2], allResults);
    }
    
    public void getHashTagsOccurrences() throws InputRepositoryException, IOException {
    
        DateTime startDateTime = new DateTime();
        startDateTime.withYear(2013);
        startDateTime.withDayOfMonth(01);
        startDateTime.withDayOfMonth(01);
        
        DateTime endDateTime = new DateTime();
        
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            StringOccurrences stringOccurrences = this.readRepository.hashTagsOccurrences(
                    MONITORING_ACTIVITY_ID, startDateTime, endDateTime, MAX_HASHTAGS);
            Assert.assertNotNull(stringOccurrences);
            long endingTime = System.nanoTime();
            allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[3], allResults);
    }
        /*public void pierfrancescoTest() throws InputRepositoryException, IOException {
    
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            this.writeRepository.storeToStream(getPierfrancescoTweet(), MONITORING_ACTIVITY_ID);
            long endingTime = System.nanoTime();
            allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[4], allResults);
    }*/
        
        public void getFollowers() throws InputRepositoryException, IOException {

        System.out.println("sono in pierfrancesco2");
                 //System.out.println("\n\n\n\n\n+++++++++++++++++++++++++++\n\n");
        this.writeRepository.storeToStream(getPierfrancescoTweet(), MONITORING_ACTIVITY_ID);
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
        long startingTime = System.nanoTime();
        this.writeRepository.getFollowers("pierfrancesco");
        //System.out.println("result di ids"+ids[0]);
        long endingTime = System.nanoTime();
        allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[4], allResults);
    }
    public void removeFollowers() throws InputRepositoryException, IOException {

        System.out.println("sono in pierfrancesco3");
                 //System.out.println("\n\n\n\n\n+++++++++++++++++++++++++++\n\n");
        final User toDelete = getPierfrancescoUser();
        this.writeRepository.storeToStream(getPierfrancescoTweet(), MONITORING_ACTIVITY_ID);
                 try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
        long startingTime = System.nanoTime();
        this.writeRepository.removeFollowers(toDelete);
        long endingTime = System.nanoTime();
        allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[5], allResults);
    } 
        public void storeFollowers() throws InputRepositoryException, IOException {

        System.out.println("sono in pierfrancesco4");
        final User toStore = getPierfrancescoUser();
        long[] allResults = new long[iterations];
        DateTime startDateTime = new DateTime();
        startDateTime.withYear(2013);
        startDateTime.withDayOfMonth(01);
        startDateTime.withDayOfMonth(01);
                            try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        
        DateTime endDateTime = new DateTime();
        for (int j = 0; j < iterations; j++) {
        long startingTime = System.nanoTime();
        this.writeRepository.storeFollowers(getPierfrancescoFollower(), toStore, startDateTime, endDateTime);
        long endingTime = System.nanoTime();
        allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[6], allResults);
    }   

            public void storeFollowing() throws InputRepositoryException, IOException {

        System.out.println("sono in pierfrancesco5");
        final User toStore = getPierfrancescoUser();
        long[] allResults = new long[iterations];
        DateTime startDateTime = new DateTime();
        startDateTime.withYear(2013);
        startDateTime.withDayOfMonth(01);
        startDateTime.withDayOfMonth(01);
                            try
            {
               
                Thread.sleep(1000);
            } catch (InterruptedException ie)
            {
                ie.printStackTrace();
            }
        
        DateTime endDateTime = new DateTime();
        for (int j = 0; j < iterations; j++) {
        long startingTime = System.nanoTime();
        this.writeRepository.storeFollowing(getPierfrancescoFollowing(), toStore, startDateTime, endDateTime);
        long endingTime = System.nanoTime();
        allResults[j] = endingTime - startingTime;
        }
        
        updateResults(Y_LABELS[7], allResults);
    }   
    private void updateResults(String yLabel, long[] allResults) {
    
        List<Long> currentResults = results.get(yLabel);
        if (currentResults == null) {
            currentResults = Lists.newLinkedList();
        }
        currentResults.add(getAverage(allResults));
        results.put(yLabel, currentResults);
    }
    
    private long getAverage(long[] allResults) {
    
        long average = 0;
        for (long allResult : allResults) {
            
            average += allResult;
        }
        
        return average / allResults.length;
    }
    
    private void storeVisualization(List<Long> resultData, String xLabel, String yLabel,
            String description, String fileName) throws IOException {
    
        String completeFileName = PERFORMANCE_DIRECTORY + "/" + fileName + ".html";
        
        PrintWriter writer = null;
        if (new File(completeFileName).isFile()) {
            
            BufferedReader br = new BufferedReader(new FileReader(completeFileName));
            StringBuilder stringBuilder = new StringBuilder();
            try {
                
                String line = br.readLine();
                
                while (line != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            
            if (stringBuilder.indexOf(this.getClass().getSimpleName()) > 0) {
                // nothing to do
            } else {
                int startIndex = stringBuilder.indexOf("arrayToDataTable");
                int endIndex = stringBuilder.indexOf(");", startIndex);
                startIndex += ARRAY_TO_DATA_TABLE_CHARS_LENGTH;
                String stringArray = stringBuilder.substring(startIndex, endIndex);
                String updatedTable = getUpdatedTable(stringArray, resultData, yLabel);
                stringBuilder.replace(startIndex, endIndex, updatedTable);
                writer = new PrintWriter(completeFileName, "UTF-8");
                writer.println(stringBuilder.toString());
                writer.close();
            }
        } else {
            
            File file = new File(completeFileName);
            file.getParentFile().mkdirs();
            writer = new PrintWriter(completeFileName, "UTF-8");
            
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>");
            writer.println("<script type=\"text/javascript\">");
            writer.println("google.load(\"visualization\", \"1\", {packages:[\"corechart\"]});");
            writer.println("google.setOnLoadCallback(drawChart);");
            writer.println("function drawChart() {");
            writer.println("var data = google.visualization.arrayToDataTable([");
            writer.println("['" + xLabel + "',  '" + this.getClass().getSimpleName() + " - "
                    + yLabel + "'],");
            for (int i = 0; i < xData.length; i++) {
                writer.print("['" + xData[i] + "', " + resultData.get(i) + SINGLE_CLOSING_BRACKET);
                if (i != xData.length - 1) {
                    writer.println(",");
                }
            }
            writer.println("]);");
            writer.println("var options = {title: '" + description + "', hAxis: {title: '" + xLabel
                    + "'}, vAxis: {title: '" + yLabel + "'}};");
            writer.println("var chart = new google.visualization.LineChart(document.getElementById('chart_div'));");
            writer.println("chart.draw(data, options);");
            writer.println(" }");
            
            writer.println("</script>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<div id=\"chart_div\" style=\"width: 900px; height: 500px;\"></div>");
            writer.println("</body>");
            writer.println("</html>");
            writer.close();
        }
        
    }
    
    private String getUpdatedTable(String stringArray, List<Long> resultData, String yLabel) {
    
        StringBuilder stringBuilder = new StringBuilder(stringArray);
        int firstClosingBracketIndex = stringBuilder.indexOf(SINGLE_CLOSING_BRACKET);
        stringBuilder.insert(firstClosingBracketIndex, ", '" + this.getClass().getSimpleName()
                + " - " + yLabel + "'");
        int pastClosingBracketIndex = stringBuilder.indexOf(SINGLE_CLOSING_BRACKET,
                firstClosingBracketIndex + 1);
        
        for (long resultValue : resultData) {
            
            int nextClosingBracketIndex = stringBuilder.indexOf(SINGLE_CLOSING_BRACKET,
                    pastClosingBracketIndex + 1);
            
            stringBuilder.insert(nextClosingBracketIndex, ", " + resultValue);
            
            pastClosingBracketIndex = stringBuilder.indexOf(SINGLE_CLOSING_BRACKET,
                    nextClosingBracketIndex + 1);
            
        }
        
        return stringBuilder.toString();
        
    }
    
    private Tweet getRandomTweet() {
    
        long id = random.nextLong();
        DateTime dateTime = new DateTime(2013, random.nextInt(11) + 1, random.nextInt(28) + 1, 0, 0);
        Date createdAt = dateTime.toDate();
        String text = "ciao a tutti sono dentro get random tweet";
        String source = "source";
        User user = new User(id + 1L);
        user.setScreenName("userCICCIOBELLO"+id);
        //System.out.println("ora sono dentro al metodo getRandomTweet");
        return new Tweet(id, createdAt, text, source, user);
    }

        private Tweet getPierfrancescoTweet() {

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = new JSONObject();
 
                    try {
                 
                        Object obj = parser.parse(new FileReader("src\\test\\resources\\\\tweets\\tweets5.json"));
                        //Object obj = parser.parse(new FileReader("c:\\Users\\pierfrancesco\\tweets\\tweets5.json"));
                 
                        jsonObject = (JSONObject) obj;
                 
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
    
        long id =  (Long) jsonObject.get("id");
        JSONObject userTemp = (JSONObject) jsonObject.get("user");
        ArrayList<User> users = new ArrayList<User>();
        JSONArray followers = (JSONArray) userTemp.get("followers");
        for (int k = 0; k < followers.size(); k++) {
        JSONObject tempUser = (JSONObject)followers.get(k);
        long ids =  (Long) tempUser.get("id");
        String screenName = (String) tempUser.get("screenName");
        User tempUserUser = new User();
        tempUserUser.setScreenName(screenName);
        tempUserUser.setId(ids);
        users.add(tempUserUser);
            }

        DateTime dateTime = new DateTime(2013, random.nextInt(12) + 1, random.nextInt(28) + 1, 0, 0);
        Date createdAt = dateTime.toDate();
        String text = (String) jsonObject.get("text");
        String source = "source";
        User user = new User();
        user.setScreenName("pierfrancesco");
        user.setFollowers(users);
        //System.out.println("ora sono dentro al metodo getRandomTweet");
        return new Tweet(id, createdAt, text, source, user);
    }
            private User getPierfrancescoUser() {

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = new JSONObject();
 
                    try {
                 
                        Object obj = parser.parse(new FileReader("c:\\Users\\pierfrancesco\\tweets0.json"));
                 
                        jsonObject = (JSONObject) obj;
                 
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
    
        long id =  (Long) jsonObject.get("id");
        JSONObject userTemp = (JSONObject) jsonObject.get("user");
        ArrayList<User> users = new ArrayList<User>();
        JSONArray followers = (JSONArray) userTemp.get("followers");
        for (int k = 0; k < followers.size(); k++) {
        JSONObject tempUser = (JSONObject)followers.get(k);
        long ids =  (Long) tempUser.get("id");
        String screenName = (String) tempUser.get("screenName");
        User tempUserUser = new User();
        tempUserUser.setScreenName(screenName);
        tempUserUser.setId(ids);
        users.add(tempUserUser);
            }

        DateTime dateTime = new DateTime(2013, random.nextInt(12) + 1, random.nextInt(28) + 1, 0, 0);
        Date createdAt = dateTime.toDate();
        String text = (String) jsonObject.get("text");
        String source = "source";
        User user = new User();
        user.setScreenName("pierfrancesco");
        user.setFollowers(users);
        //System.out.println("ora sono dentro al metodo getRandomTweet");
        return user;
    }
            private long[] getPierfrancescoFollower() {

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = new JSONObject();
 
                    try {
                 
                        Object obj = parser.parse(new FileReader("c:\\Users\\pierfrancesco\\tweets0.json"));
                 
                        jsonObject = (JSONObject) obj;
                 
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
    
        long id =  (Long) jsonObject.get("id");
        JSONObject userTemp = (JSONObject) jsonObject.get("user");
        ArrayList<User> users = new ArrayList<User>();
        
        JSONArray followers = (JSONArray) userTemp.get("followers");
        long[] listId = new long[followers.size()];
        for (int k = 0; k < followers.size(); k++) {
        JSONObject tempUser = (JSONObject)followers.get(k);
        long ids =  (Long) tempUser.get("id");
        String screenName = (String) tempUser.get("screenName");
        User tempUserUser = new User();
        tempUserUser.setScreenName(screenName);
        tempUserUser.setId(ids);
        listId[k] = ids;
        users.add(tempUserUser);
            }
            return listId;

    }

                private long[] getPierfrancescoFollowing() {

                    JSONParser parser = new JSONParser();
                    JSONObject jsonObject = new JSONObject();
 
                    try {
                 
                        Object obj = parser.parse(new FileReader("c:\\Users\\pierfrancesco\\tweets0.json"));
                 
                        jsonObject = (JSONObject) obj;
                 
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
    
        long id =  (Long) jsonObject.get("id");
        JSONObject userTemp = (JSONObject) jsonObject.get("user");
        ArrayList<User> users = new ArrayList<User>();
        
        JSONArray followers = (JSONArray) userTemp.get("friends");
        long[] listId = new long[followers.size()];
        for (int k = 0; k < followers.size(); k++) {
        JSONObject tempUser = (JSONObject)followers.get(k);
        long ids =  (Long) tempUser.get("id");
        String screenName = (String) tempUser.get("screenName");
        User tempUserUser = new User();
        tempUserUser.setScreenName(screenName);
        tempUserUser.setId(ids);
        listId[k] = ids;
        users.add(tempUserUser);
            }
            return listId;

    }
    
    private List<Tweet> getRandomTweets(int numberOfTweets) {
    
        List<Tweet> randomTweets = Lists.newLinkedList();
        
        for (int i = 0; i < numberOfTweets; i++) {
            
            randomTweets.add(getRandomTweet());
        }
        
        return randomTweets;
    }
    
    private Object getOneAmong(List<?> objects) {
    
        return objects.get(random.nextInt(10));
    }
}
