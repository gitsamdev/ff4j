package org.ff4j.test.audit;

import static org.ff4j.event.EventConstants.ACTION_CHECK_OFF;
import static org.ff4j.event.EventConstants.ACTION_CHECK_OK;
import static org.ff4j.event.EventConstants.ACTION_CREATE;
import static org.ff4j.event.EventConstants.SOURCE_JAVA;
import static org.ff4j.event.EventConstants.SOURCE_WEB;
import static org.ff4j.event.EventConstants.SOURCE_WEBAPI;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.ff4j.audit.FeatureUsageTracking;
import org.ff4j.chart.BarChart;
import org.ff4j.chart.TimeSeriesChart;
import org.ff4j.event.Event;
import org.ff4j.event.EventBuilder;
import org.ff4j.event.EventPublisher;
import org.ff4j.event.EventQueryDefinition;
import org.ff4j.event.EventSeries;
import org.ff4j.feature.Feature;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.utils.MutableHitCount;
import org.ff4j.utils.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Superclass to test {@link FeatureUsageTracking}.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public abstract class AbstractEventRepositoryTest {
    
    /** Feature List. */
    protected ArrayList<Feature> features;

    /** Target {@link FeatureUsageTracking}. */
    protected FeatureUsageTracking repo;
    
    /** Target publisher. */
    protected EventPublisher publisher;
    
    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        repo      = initRepository();
        publisher = new EventPublisher(repo);
        features  = new ArrayList<Feature>(
                new FeatureStoreInMemory("ff4j.xml").findAll().collect(Collectors.toList()));
    }
   
    // Utility to generate event
    protected Event generateFeatureUsageEvent(String uid) {
        return new EventBuilder().source(SOURCE_JAVA).feature(uid).action(ACTION_CHECK_OK).build();
    }
    
    // Utility to generate event
    protected Event generateFeatureCreate(String uid) {
        return new EventBuilder().source(SOURCE_JAVA).feature(uid).action(ACTION_CREATE).build();
    }
    
    // Generate a random event during the period
    protected Event generateFeatureUsageEvent(String uid, long timestamp) {
        Event event = generateFeatureUsageEvent(uid);
        event.setTimestamp(timestamp);
        return event;
    }
    
    // Generate a random event during the period
    protected Event generateFeatureUsageEvent(String uid, long from, long to) {
        return generateFeatureUsageEvent(uid, from + (long) (Math.random() * (to-from)));
    }
    
    // Generate a random event during the period
    protected Event generateRandomFeatureUsageEvent(long from, long to) {
        return generateFeatureUsageEvent(Util.getRandomElement(features).getUid(), from , to);
    }
    
    // Populate repository for test
    protected void populateRepository(long from, long to, int totalEvent) throws InterruptedException {
        for (int i = 0; i < totalEvent; i++) {
            repo.create(generateRandomFeatureUsageEvent(from, to));
        }
    }
    
    /**
     * Any store test will declare its store through this callback.
     * 
     * @return working feature store
     * @throws Exception
     *             error during building feature store
     */
    protected abstract FeatureUsageTracking initRepository();
    
    @Test
    public void testSaveEventUnit() throws InterruptedException {
        long start = System.currentTimeMillis();
        Assert.assertEquals(0, repo.getFeatureUsageTotalHitCount(new EventQueryDefinition(start, System.currentTimeMillis())));
        repo.create(generateFeatureUsageEvent("f1"));
        Thread.sleep(100);
        Assert.assertEquals(1, repo.getFeatureUsageTotalHitCount(new EventQueryDefinition(start-20, System.currentTimeMillis())));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSaveEventNull() {
        repo.create(null);
    }
    
    @Test
    public void testSaveAuditTrail() throws InterruptedException {
        long start = System.currentTimeMillis();
        Event evt1 = generateFeatureCreate("f1");
        repo.create(evt1);
        Thread.sleep(200);
        Assert.assertEquals(1, repo.getAuditTrail(new EventQueryDefinition(start-10, System.currentTimeMillis())).size());
    }
    
    @Test
    public void testPieChart() throws InterruptedException {
        long start = System.currentTimeMillis();
        Event evt1 = generateFeatureCreate("f1");
        repo.create(evt1);
        Thread.sleep(200);
        
        EventQueryDefinition eqd = new EventQueryDefinition(start-10, System.currentTimeMillis());
        Assert.assertNotNull(repo.getFeatureUsagePieChart(eqd));
        Assert.assertNotNull(repo.getHostPieChart(eqd));
        Assert.assertNotNull(repo.getSourcePieChart(eqd));
        Assert.assertNotNull(repo.getUserPieChart(eqd));
        
        Assert.assertNotNull(repo.getHostBarChart(eqd));
        Assert.assertNotNull(repo.getSourceBarChart(eqd));
        Assert.assertNotNull(repo.getUserBarChart(eqd));
        
    }
    
    @Test
    public void testFeatureUsageBarCharts() throws InterruptedException {
        long start = System.currentTimeMillis();
        // Create Event
        repo.create(generateFeatureCreate("f1"));
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create(generateFeatureUsageEvent("f1"));
            repo.create(generateFeatureUsageEvent("f2"));
        }
        
        // Assert bar chart (2 bars with 8 and 8)
        EventQueryDefinition testQuery = new EventQueryDefinition(start-10, System.currentTimeMillis()+10);
        BarChart bChart = repo.getFeatureUsageBarChart(testQuery);
        Assert.assertEquals(2, bChart.getChartBars().size());
        Assert.assertEquals(new Integer(8), bChart.getChartBars().get(0).getValue());
        Assert.assertEquals(new Integer(8), bChart.getChartBars().get(1).getValue());
        Assert.assertNotNull(bChart.getChartBars().get(0).getColor());
        Assert.assertNotNull(bChart.getChartBars().get(1).getColor());
    }
    
    @Test
    public void testFeatureUsageHitCount() throws InterruptedException {
        long start = System.currentTimeMillis();
        // Create Event
        repo.create(generateFeatureCreate("f1"));
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create(new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build());
            repo.create(new EventBuilder().source(SOURCE_WEB).feature("f2").action(ACTION_CHECK_OK).build());
        }
        Thread.sleep(100);
        
        // Assert bar chart (2 bars with 8 and 8)
        EventQueryDefinition testQuery = new EventQueryDefinition(start, System.currentTimeMillis());
        // Assert Pie Chart (2 sectors with 8 and 8)
        Map < String, MutableHitCount > mapOfHit = repo.getFeatureUsageHitCount(testQuery);
        Assert.assertEquals(2, mapOfHit.size());
        Assert.assertTrue(mapOfHit.containsKey("f1"));
        Assert.assertTrue(mapOfHit.containsKey("f2"));
        Assert.assertEquals(8, mapOfHit.get("f1").get());
    }
    
    @Test
    public void testSearchFeatureUsageEvents() throws InterruptedException {
        long start = System.currentTimeMillis();
        repo.create(generateFeatureCreate("f1"));
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create(new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build());
            repo.create(new EventBuilder().source(SOURCE_WEB).feature("f2").action(ACTION_CHECK_OK).build());
        }
        Thread.sleep(100);
        
        // Then
        EventQueryDefinition testQuery = new EventQueryDefinition(start-20, System.currentTimeMillis());
        EventSeries es = repo.searchFeatureUsageEvents(testQuery);
        Assert.assertEquals(16, es.size());
        
        // Then
        
    }
    
    @Test
    public void testGetFeatureUsageHistory() throws InterruptedException {
        long start = System.currentTimeMillis();
        repo.create(generateFeatureCreate("f1"));
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create(new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build());
            repo.create(new EventBuilder().source(SOURCE_WEB).feature("f2").action(ACTION_CHECK_OK).build());
        }
        Thread.sleep(100);
        
        // Then
        EventQueryDefinition testQuery = new EventQueryDefinition(start-20, System.currentTimeMillis());
        TimeSeriesChart  tsc = repo.getFeatureUsageHistory(testQuery, TimeUnit.HOURS);
        Assert.assertEquals(1, tsc.getTimeSlots().size());
    }
    
    /** TDD. */
    @Test
    public void testSourceHitCount() throws InterruptedException {
        long start = System.currentTimeMillis();
        // When
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create(new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build());
            repo.create(new EventBuilder().source(SOURCE_WEB).feature("f2").action(ACTION_CHECK_OK).build());
        }
        Thread.sleep(200);
        repo.create(new EventBuilder().source(SOURCE_WEBAPI).feature("f1").action(ACTION_CHECK_OK).build());
        Thread.sleep(200);
        
        // Then
        EventQueryDefinition testQuery = new EventQueryDefinition(start-20, System.currentTimeMillis());
        Map < String, MutableHitCount > mapOfHit = repo.getSourceHitCount(testQuery);
        Assert.assertEquals(3, mapOfHit.size());
        Assert.assertTrue(mapOfHit.containsKey(SOURCE_JAVA));
        Assert.assertTrue(mapOfHit.containsKey(SOURCE_WEB));
        Assert.assertEquals(1, mapOfHit.get(SOURCE_WEBAPI).get());
    }
    
    /** TDD. */
    @Test
    public void testUserHitCount() throws InterruptedException {
        long start = System.currentTimeMillis();
        // When
        for(int i = 0;i<8;i++) {
           
            Event e1 =  new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build();
            e1.setOwner("JOHN");
            repo.create(e1);
            Thread.sleep(100);
            
            Event e2 = new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build();
            e2.setOwner("BOB");
            repo.create(e2);
            Thread.sleep(100);
        }
        Thread.sleep(200);
        
        // Then
        EventQueryDefinition testQuery = new EventQueryDefinition(start-20, System.currentTimeMillis());
        Map < String, MutableHitCount > mapOfHit = repo.getUserHitCount(testQuery);
        Assert.assertEquals(2, mapOfHit.size());
        Assert.assertTrue(mapOfHit.containsKey("JOHN"));
        Assert.assertTrue(mapOfHit.containsKey("BOB"));
        Assert.assertEquals(8, mapOfHit.get("BOB").get());
    }
    
    /** TDD. */
    @Test
    public void testHostHitCount() throws InterruptedException {
        long start = System.currentTimeMillis();
        // When
        for(int i = 0;i<8;i++) {
            Thread.sleep(100);
            repo.create( new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build());
        }
        Thread.sleep(200);
        
        // Then
        EventQueryDefinition testQuery = new EventQueryDefinition(start, System.currentTimeMillis());
        Map < String, MutableHitCount > mapOfHit = repo.getHostHitCount(testQuery);
        Assert.assertEquals(1, mapOfHit.size());
        Assert.assertEquals(1, mapOfHit.values().size());
    }
    
    /** TDD. */
    @Test
    public void testSaveCheckOff() throws InterruptedException {
        long start = System.currentTimeMillis();
        // Given
        Event evt1 = new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OFF).build();
        // When
        repo.create(evt1);
        Thread.sleep(100);
        // Then
        Assert.assertEquals(0, repo.getFeatureUsageTotalHitCount(new EventQueryDefinition(start, System.currentTimeMillis())));
        EventSeries es = repo.getAuditTrail(new EventQueryDefinition(start, System.currentTimeMillis()));
        Assert.assertEquals(0, es.size());
    }
    
    /** TDD. */
    @Test
    public void testLimitEventSeries() throws InterruptedException {
        EventSeries es = new EventSeries(5);
        for(int i=0;i<10;i++) {
            Thread.sleep(10);
            es.add(new EventBuilder().source(SOURCE_JAVA).feature("f1").action(ACTION_CREATE).build());
        }
        Assert.assertEquals(5, es.size());
    }
    
    /** TDD. */
    @Test
    public void testGetEventByUID() throws InterruptedException {
        // Given
        String dummyId = "1234-5678-9012-3456";
        Event evt1 = new EventBuilder(dummyId).source(SOURCE_JAVA).feature("f1").action(ACTION_CREATE).build();
        // When
        repo.create(evt1);
        // Let the store to be updated
        Thread.sleep(100);
        // Then
        Optional< Event > evt = repo.findById(dummyId, System.currentTimeMillis());
        Assert.assertTrue(evt.isPresent());
    }
    
    /** TDD. */
    @Test
    public void testGetEventByUID2() throws InterruptedException {
        // Given
        String dummyId = "1234-5678-9012-3456";
        Event evt1 = new EventBuilder(dummyId).source(SOURCE_JAVA).feature("f1").action(ACTION_CREATE).build();;
        // When
        repo.create(evt1);
        // Let the store to be updated
        Thread.sleep(100);
        // Then
        Optional< Event > evt = repo.findById(dummyId, null);
        Assert.assertTrue(evt.isPresent());
    }
    
    /** TDD. */
    @Test
    public void testPurgeEvents() throws InterruptedException {
        // Given, 2 events in the repo
        long topStart = System.currentTimeMillis();
        Event evtAudit = new EventBuilder("1234-5678-9012-3456").source(SOURCE_JAVA).feature("f1").action(ACTION_CREATE).build();
        Event evtFeatureUsage = new EventBuilder("1234-5678-9012-3457").source(SOURCE_JAVA).feature("f1").action(ACTION_CHECK_OK).build();
        repo.create(evtAudit);
        repo.create(evtFeatureUsage);
        Thread.sleep(100);
        Assert.assertNotNull(repo.findById(evtAudit.getUid(), System.currentTimeMillis()));
        Assert.assertNotNull(repo.findById(evtFeatureUsage.getUid(), System.currentTimeMillis()));
        // When
        EventQueryDefinition testQuery = new EventQueryDefinition(topStart-100, System.currentTimeMillis());
        repo.purgeFeatureUsage(testQuery);
        Assert.assertFalse(repo.findById(evtFeatureUsage.getUid(), System.currentTimeMillis()).isPresent());
        Assert.assertTrue(repo.searchFeatureUsageEvents(testQuery).isEmpty());
        
        // Then
        EventQueryDefinition testQuery2 = new EventQueryDefinition(topStart-100, System.currentTimeMillis());
        repo.purgeAuditTrail(testQuery2);
        Assert.assertFalse(repo.findById(evtAudit.getUid(), System.currentTimeMillis()).isPresent());

    }

}

