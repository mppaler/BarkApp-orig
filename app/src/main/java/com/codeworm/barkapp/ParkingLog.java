package com.codeworm.barkapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Harvie Marcelino on 02/16/2018.
 */

public class ParkingLog implements Parcelable{
    private int id;
    private String timestamp;
    private String event;
    private String parking_area;
    private String slot_id;
    private List<ParkingLog> mParkingLog;

    public ParkingLog(int id, String timestamp, String event, String parking_area, String slot_id){
        this.id = id;
        this.timestamp = timestamp;
        this.event = event;
        this.parking_area = parking_area;
        this.slot_id = slot_id;
    }

    protected ParkingLog(Parcel in){
        id = in.readInt();
        timestamp = in.readString();
        event = in.readString();
        parking_area = in.readString();
        slot_id = in.readString();
    }

    public static final Creator<ParkingLog> CREATOR = new Creator<ParkingLog>() {
        @Override
        public ParkingLog createFromParcel(Parcel parcel) {
            return new ParkingLog(parcel);
        }

        @Override
        public ParkingLog[] newArray(int i) {
            return new ParkingLog[i];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getParking_area() {
        return parking_area;
    }

    public void setParking_area(String parking_area) {
        this.parking_area = parking_area;
    }

    public String getSlot_id() {
        return slot_id;
    }

    public void setSlot_id(String slot_id) {
        this.slot_id = slot_id;
    }

    public List<ParkingLog> getmParkingLog() {
        return mParkingLog;
    }

    public void setmParkingLog(List<ParkingLog> mParkingLog) {
        this.mParkingLog = mParkingLog;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(timestamp);
        parcel.writeString(event);
        parcel.writeString(parking_area);
        parcel.writeString(slot_id);
    }
}
