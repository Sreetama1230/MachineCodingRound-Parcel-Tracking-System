package com.example.parcel.model;

public class ParcelDTO {

	int timestamp;
	String parcelId;
	String attribute;
	int value;
	int ttl;
	
	public int getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}
	public String getParcelId() {
		return parcelId;
	}
	public void setParcelId(String parcelId) {
		this.parcelId = parcelId;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public ParcelDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getTtl() {
		return ttl;
	}
	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	public ParcelDTO(int timestamp, String parcelId, String attribute, int value) {
		super();
		this.timestamp = timestamp;
		this.parcelId = parcelId;
		this.attribute = attribute;
		this.value = value;
	}

}
