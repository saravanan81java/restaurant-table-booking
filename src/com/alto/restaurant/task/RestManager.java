package com.alto.restaurant.task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class RestManager {

	private List<Table> tables;
    private Queue<ClientsGroup> waitingQueue;
    private ConcurrentHashMap<ClientsGroup, Table> seatedClients;

	public RestManager (List<Table> tables)
	   {
		this.tables = new ArrayList<>(tables);
        this.waitingQueue = new LinkedList<>();
        this.seatedClients = new ConcurrentHashMap<>();
	   }

	   // new client(s) show up
	   public void onArrive (ClientsGroup group)
	   {
		   if (!seatGroupImmediately(group)) {
	            waitingQueue.add(group);
	       }
		   seatWaitingGroups();
	   }

	   // client(s) leave, either served or simply abandoning the queue
	   public void onLeave (ClientsGroup group)
	   {
		   if (seatedClients.containsKey(group)) {
	            Table table = seatedClients.get(group);
	            table.leaveGroup(group);
	            seatedClients.remove(group);
	        } else {
	            waitingQueue.remove(group);
	        }
	        seatWaitingGroups();
	   }

	   // return table where a given client group is seated, 
	   // or null if it is still queueing or has already left
	   public Table lookup (ClientsGroup group)
	   {
		   return seatedClients.getOrDefault(group, null);
	   }
	   
	   private boolean seatGroupImmediately(ClientsGroup group) {
	        // Try to find an exact or larger empty table for the group
	        for (Table table : tables) {
	            if (table.isEmpty() && table.size >= group.size) {
	                table.seatGroup(group);
	                seatedClients.put(group, table);
	                return true;
	            }
	        }

	        // Try to find a table with enough available seats
	        for (Table table : tables) {
	            if (!table.isFull() && table.canAccommodate(group)) {
	                table.seatGroup(group);
	                seatedClients.put(group, table);
	                return true;
	            }
	        }

	        return false; // No table available immediately
	    }

	    private void seatWaitingGroups() {
	        Queue<ClientsGroup> tempQueue = new LinkedList<>();

	        while (!waitingQueue.isEmpty()) {
	            ClientsGroup group = waitingQueue.poll();

	            if (!seatGroupImmediately(group)) {
	                // If the group cannot be seated, keep them in the queue
	                tempQueue.add(group);
	            }
	        }

	        // Restore the waiting queue
	        waitingQueue = tempQueue;
	    }
	     
}
