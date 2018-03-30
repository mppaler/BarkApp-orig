package com.codeworm.barkapp;

import java.util.Comparator;

/**
 * Created by Harvie Marcelino on 03/26/2018.
 */

public class ParkingLogComparator {

    private ParkingLogComparator(){
        //no instance
    }

    public static Comparator<ParkingLog> getTimestampComparator() {
        return new ParkingLogTimestampComparator();
    }
    public static Comparator<ParkingLog> getEventComparator() {
        return new ParkingLogEventComparator();
    }
    public static Comparator<ParkingLog> getParkingAreaComparator() {
        return new ParkingLogParkingAreaComparator();
    }
    public static Comparator<ParkingLog> getSlotIdComparator() {
        return new ParkingLogSlotIdComparator();
    }

    private static class ParkingLogTimestampComparator implements Comparator<ParkingLog> {

        @Override
        public int compare(final ParkingLog log1, final ParkingLog log2) {
            return log1.getTimestamp().compareTo(log2.getTimestamp());
        }
    }

    private static class ParkingLogEventComparator implements Comparator<ParkingLog> {

        @Override
        public int compare(final ParkingLog log1, final ParkingLog log2) {
            return log1.getEvent().compareTo(log2.getEvent());
        }
    }

    private static class ParkingLogParkingAreaComparator implements Comparator<ParkingLog> {

        @Override
        public int compare(final ParkingLog log1, final ParkingLog log2) {
            return log1.getParking_area().compareTo(log2.getParking_area());
        }
    }

    private static class ParkingLogSlotIdComparator implements Comparator<ParkingLog> {

        @Override
        public int compare(final ParkingLog log1, final ParkingLog log2) {
            return log1.getSlot_id().compareTo(log2.getSlot_id());
        }
    }
}
