package com.alto.restaurant.task;

public class Table {

	public final int size; // number of chairs
    public int availableSeats; // available seats
    
    public Table(int size) {
        this.size = size;
        this.availableSeats = size;
    }

    public boolean canAccommodate(ClientsGroup group) {
        return availableSeats >= group.size;
    }

    public void seatGroup(ClientsGroup group) {
        availableSeats -= group.size;
    }

    public void leaveGroup(ClientsGroup group) {
        availableSeats += group.size;
    }

    public boolean isEmpty() {
        return availableSeats == size;
    }

    public boolean isFull() {
        return availableSeats == 0;
    }

	@Override
	public String toString() {
		return "Table [size=" + size + ", availableSeats=" + availableSeats + "]";
	}
    
    
}
