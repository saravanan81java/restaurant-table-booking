package com.alto.restaurant.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RestManagerTest {
	private RestManager restManager;
    private List<Table> tables;

    @Before
    public void setUp() {
        // Set up tables for each test
        tables = new ArrayList<>();
        tables.add(new Table(2));
        tables.add(new Table(3));
        tables.add(new Table(4));
        tables.add(new Table(5));
        tables.add(new Table(6));
        restManager = new RestManager(tables);
    }

    @Test
    public void testSingleGroupSeatsCorrectly() {
        // Test seating a single group directly without queueing
        ClientsGroup group = new ClientsGroup(4);
        restManager.onArrive(group);
        
        Table table = restManager.lookup(group);
        assertNotNull("Group should be seated", table);
        assertEquals("Group should be seated at a table with size 4 or larger", 4, table.size);
        assertEquals("Remaining seats should be 0", 0, table.availableSeats);
    }

    @Test
    public void testQueueingGroup() {
        // Test that a larger group is added to the queue when no table is available
        ClientsGroup group = new ClientsGroup(7);  // No table for group larger than 6
        restManager.onArrive(group);
        
        Table table = restManager.lookup(group);
        assertNull("Group should not be seated because no table is large enough", table);
    }

    @Test
    public void testAccommodateSmallerGroup() {
        // Test seating a smaller group after a larger group is queued
        ClientsGroup largeGroup = new ClientsGroup(6);
        restManager.onArrive(largeGroup);

        ClientsGroup smallGroup = new ClientsGroup(2);
        restManager.onArrive(smallGroup);

        // Small group should be seated at the table for 2 or 3 people
        Table smallTable = restManager.lookup(smallGroup);
        assertNotNull("Small group should be seated", smallTable);
        assertEquals("Small group should be seated at a table with size 2", 2, smallTable.size);
    }

    @Test
    public void testLeavingGroup() {
        // Test that a seated group leaves and frees up the table
        ClientsGroup group = new ClientsGroup(4);
        restManager.onArrive(group);
        
        Table table = restManager.lookup(group);
        assertNotNull("Group should be seated", table);

        // Group leaves
        restManager.onLeave(group);
        assertNull("Group should no longer be seated", restManager.lookup(group));
        assertEquals("Table should be empty again", 4, table.availableSeats);
    }

    @Test
    public void testAccommodateMultipleGroups() {
        // Test seating multiple groups at the same time and ensuring table sharing works
        ClientsGroup group1 = new ClientsGroup(2);
        ClientsGroup group2 = new ClientsGroup(2);
        restManager.onArrive(group1);
        restManager.onArrive(group2);

        Table table1 = restManager.lookup(group1);
        Table table2 = restManager.lookup(group2);

        assertNotNull("Group 1 should be seated", table1);
        assertNotNull("Group 2 should be seated", table2);

        // Group 1 and Group 2 should be seated at different tables
        assertEquals("Table for group 1 should have size 2", 2, table1.size);
        assertEquals("Table for group 2 should have size 3 or 4", 3, table2.size);
    }

    @Test
    public void testReSeatingAfterGroupLeaves() {
        // Test that a table is reused after a group leaves
        ClientsGroup group = new ClientsGroup(4);
        restManager.onArrive(group);

        Table table = restManager.lookup(group);
        assertNotNull("Group should be seated", table);

        // Group leaves
        restManager.onLeave(group);
        assertEquals("Table should have 4 available seats after the group leaves", 4, table.availableSeats);

        // Another group should now be seated at the same table
        ClientsGroup newGroup = new ClientsGroup(4);
        restManager.onArrive(newGroup);
        Table newTable = restManager.lookup(newGroup);

        assertEquals("New group should be seated at the same table", table, newTable);
        assertEquals("Remaining seats should be 0", 0, newTable.availableSeats);
    }
}
