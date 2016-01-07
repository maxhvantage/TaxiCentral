package com.taxicentral.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MMFA-MANISH on 11/6/2015.
 */
public class TripHistoryModel implements Parcelable {
    Long id;
    String  time,tripName, tripType, date;

    public  TripHistoryModel(){}

    protected TripHistoryModel(Parcel in) {
        id = in.readLong();
        date = in.readString();
        time = in.readString();
        tripName = in.readString();
        tripType = in.readString();
    }

    public static final Creator<TripHistoryModel> CREATOR = new Creator<TripHistoryModel>() {
        @Override
        public TripHistoryModel createFromParcel(Parcel in) {
            return new TripHistoryModel(in);
        }

        @Override
        public TripHistoryModel[] newArray(int size) {
            return new TripHistoryModel[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeLong(id);
        dest.writeSerializable(date);
        dest.writeString(time);
        dest.writeString(tripName);
        dest.writeString(tripType);
    }
}
