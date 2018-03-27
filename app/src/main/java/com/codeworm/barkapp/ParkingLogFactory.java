package com.codeworm.barkapp;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harvie Marcelino on 03/26/2018.
 */

public final class ParkingLogFactory {

    private static List<ParkingLog> parkingLogList = new ArrayList<>();

    public List<ParkingLog> getParkingLogList() {
        return parkingLogList;
    }

    public void setParkingLogList(List<ParkingLog> parkingLogList) {
        this.parkingLogList = parkingLogList;
    }

    public void clearParkingLogList(){
        parkingLogList.clear();
    }

    public static List<ParkingLog> createParkingLogList(){
        List<ParkingLog> parkingLog = new ArrayList<>();
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2017-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2017-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2017-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2017-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2017-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        parkingLog.add(new ParkingLog("2018-03-25 10:34:28", "N/A", "Roque Ruano Building", "1001"));
        return parkingLog;
    }
}
