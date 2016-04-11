package com.taxicentral.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by MAX on 11/12/2015.
 */
public class Trip implements Parcelable {
    Long id;
    String distance="", agreement="", fare="", date="", customerName="", customerImage="",travelTime="",
            sourceAddress="", destinationAddress="", month="", number="", tripType="";
    Double sourceLatitude=0.0, sourcelogitude=0.0, destinationLatitude=0.0, destinationLogitude=0.0;
    float customerRating=0;
    Integer  corporateType=0;

    public Trip(){}


    protected Trip(Parcel in) {
        id = in.readLong();
        distance = in.readString();
        agreement = in.readString();
        fare = in.readString();
        date = in.readString();
        customerName = in.readString();
        sourceLatitude = in.readDouble();
        sourcelogitude = in.readDouble();
        destinationLatitude = in.readDouble();
        destinationLogitude = in.readDouble();
        sourceAddress = in.readString();
        destinationAddress = in.readString();
        month = in.readString();
        number = in.readString();
        tripType =  in.readString();
        travelTime =  in.readString();
        corporateType = in.readInt();
        customerImage = in.readString();
        customerRating = in.readFloat();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(Double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public Double getSourcelogitude() {
        return sourcelogitude;
    }

    public void setSourcelogitude(Double sourcelogitude) {
        this.sourcelogitude = sourcelogitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLogitude() {
        return destinationLogitude;
    }

    public void setDestinationLogitude(Double destinationLogitude) {
        this.destinationLogitude = destinationLogitude;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

      public String getAgreement() {
        return agreement;
    }

    public void setAgreement(String agreement) {
        this.agreement = agreement;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public Integer getCorporateType() {
        return corporateType;
    }

    public void setCorporateType(Integer corporateType) {
        this.corporateType = corporateType;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    public Float getCustomerRating() {
        return customerRating;
    }

    public void setCustomerRating(Float customerRating) {
        this.customerRating = customerRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(distance);
        dest.writeString(agreement);
        dest.writeString(fare);
        dest.writeString(date);
        dest.writeString(customerName);
        dest.writeDouble(sourceLatitude);
        dest.writeDouble(sourcelogitude);
        dest.writeDouble(destinationLatitude);
        dest.writeDouble(destinationLogitude);
        dest.writeString(sourceAddress);
        dest.writeString(destinationAddress);
        dest.writeString(month);
        dest.writeString(number);
        dest.writeString(tripType);
        dest.writeString(travelTime);
        dest.writeInt(corporateType);
        dest.writeString(customerImage);
        dest.writeFloat(customerRating);
    }
}
