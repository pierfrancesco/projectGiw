package it.uniroma3.dia.giw.repositories.performance;

import it.uniroma3.dia.giw.exceptions.InputRepositoryException;
import it.uniroma3.dia.giw.model.ContextTweetId;
import it.uniroma3.dia.giw.model.InputRepository;
import it.uniroma3.dia.giw.model.OutputRepository;
import it.uniroma3.dia.giw.model.StringOccurrences;
import it.uniroma3.dia.giw.model.monitoring.MonitoringActivityId;
import it.uniroma3.dia.giw.model.twitter.data.Tweet;
import it.uniroma3.dia.giw.model.twitter.data.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            "Get screen name occurrences", "Get hashtag occurrences" };
    
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
    
        for (int i = 0; i < xData.length; i++) {
            
            int randomIndex = random.nextInt(xData[i] - 1);
            ContextTweetId storedContextTweetId = null;
            for (int storedTweets = 0; storedTweets < xData[i]; storedTweets++) {
                
                ContextTweetId contextTweetID = this.writeRepository.storeToStream(
                        getRandomTweet(), MONITORING_ACTIVITY_ID);
                if (storedTweets == randomIndex) {
                    storedContextTweetId = contextTweetID;
                }
            }
            
            testPerformance(i, storedContextTweetId);
        }
        
        for (String testLabel : results.keySet()) {
            
            storeVisualization(results.get(testLabel), X_LABEL, testLabel, testLabel, testLabel
                    .toLowerCase().replace(" ", "_"));
        }
    }
    
    private void testPerformance(int xDataIndex, ContextTweetId storedContextTweetId)
            throws InputRepositoryException, IOException {
    
        getLogger().info(
                "Testing performances of a repository containing " + xData[xDataIndex] + " tweets");
        
        storeOneTweet();
        getTweetById(storedContextTweetId);
        getScreenNameOccurrences();
        getHashTagsOccurrences();
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
        startDateTime.withYear(2013);
        startDateTime.withDayOfMonth(01);
        startDateTime.withDayOfMonth(01);
        
        DateTime endDateTime = new DateTime();
        
        long[] allResults = new long[iterations];
        for (int j = 0; j < iterations; j++) {
            
            long startingTime = System.nanoTime();
            StringOccurrences stringOccurrences = this.readRepository.authorOccurrences(
                    MONITORING_ACTIVITY_ID, startDateTime, endDateTime, MAX_AUTHORS);
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
        DateTime dateTime = new DateTime(2013, random.nextInt(12) + 1, random.nextInt(28) + 1, 0, 0);
        Date createdAt = dateTime.toDate();
        String text = "tweet text relative to tweet with id = " + id;
        String source = "source";
        User user = new User(id + 1L);
        user.setScreenName("user_" + id);
        return new Tweet(id, createdAt, text, source, user);
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
