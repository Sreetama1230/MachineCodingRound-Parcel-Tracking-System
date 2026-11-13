package com.example.parcel.service;

import com.example.parcel.model.ParcelDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ParcelTrackingSystemImpl implements ParcelTrackingSystem {

	Map<String, Map<String, ParcelDTO>> hm = new HashMap<>();
	Map<String, Map<String, List<ParcelDTO>>> historicalRecordMap = new HashMap<>();

	// ---------- Level 1 ----------
	public void setAttribute(int timestamp, String parcelId, String attribute, int value) {
		ParcelDTO parcelDTO = new ParcelDTO(timestamp, parcelId, attribute, value);
		// maximum expiry time possible
		parcelDTO.setExpiryTime(Integer.MAX_VALUE);

		Map<String, ParcelDTO> map = new HashMap<>();
		map.put(attribute, parcelDTO);

		if (hm.containsKey(parcelId) && historicalRecordMap.containsKey(parcelId)) {
			Map<String, ParcelDTO> existedMap = hm.get(parcelId);
			existedMap.put(attribute, parcelDTO);

			// level - 4
			if (historicalRecordMap.get(parcelId).containsKey(attribute)) {
				List<ParcelDTO> existedList = historicalRecordMap.get(parcelId).get(attribute);

				// existedList.get(existedList.size() - 1).setExpiryTime(timestamp);

				// suppose you have already removed it or it is already expired
				// then definitely we can not change the expiry date
				ParcelDTO lastDTO = existedList.get(existedList.size() - 1);

				if (lastDTO.getExpiryTime() > timestamp) {
					lastDTO.setExpiryTime(timestamp);
				}

				existedList.add(parcelDTO);

			} else {
				List<ParcelDTO> newList = new ArrayList<>();
				newList.add(parcelDTO);
				Map<String, List<ParcelDTO>> newMap = historicalRecordMap.get(parcelId);
				newMap.put(attribute, newList);
				historicalRecordMap.put(parcelId, newMap);

			}

			// can assume the timestamp is the starting time
			// we can set the expiry time for the last inserted record

		} else {
			hm.put(parcelId, map);

			// level - 4
			List<ParcelDTO> dtos = new ArrayList<>();
			dtos.add(parcelDTO);
			Map<String, List<ParcelDTO>> innerMap = new HashMap<>();
			innerMap.put(attribute, dtos);
			historicalRecordMap.put(parcelId, innerMap);

		}

	}

	public Optional<Integer> getAttribute(int timestamp, String parcelId, String attribute) {

		if (!(hm.containsKey(parcelId) && hm.get(parcelId).containsKey(attribute))) {
			return Optional.empty();
		}
		Map<String, ParcelDTO> map = hm.get(parcelId);

		if (timestamp < map.get(attribute).getExpiryTime()) {
			return Optional.of(map.get(attribute).getValue());
		}
		return Optional.empty();
	};

	public boolean updateIfMatch(int timestamp, String parcelId, String attribute, int expectedValue, int newValue) {

		Optional<Integer> attValue = getAttribute(timestamp, parcelId, attribute);

		// which one's expiry date we should set to timestamp

		if (attValue.isPresent()) {
			Integer val = attValue.get();

			Map<String, ParcelDTO> map = hm.get(parcelId);

			if (timestamp < map.get(attribute).getExpiryTime()) {

				if (val == expectedValue) {

					// hm and bigger contain references to the same DTO object.

					ParcelDTO dto = new ParcelDTO(timestamp, parcelId, attribute, newValue);
					// taking the old parcel's expiry
					dto.setExpiryTime(map.get(attribute).getExpiryTime());

					// updating the hm's record
					hm.get(parcelId).put(attribute, dto);

					// updating bigger
					List<ParcelDTO> existedList = historicalRecordMap.get(parcelId).get(attribute);
					// closing the old parcel dto with current timestamp
					existedList.get(existedList.size() - 1).setExpiryTime(timestamp);
					existedList.add(dto);

					return true;
				}

			}

		}
		return false;
	};

	public boolean removeIfMatch(int timestamp, String parcelId, String attribute, int expectedValue) {
		Optional<Integer> attValue = getAttribute(timestamp, parcelId, attribute);

		if (attValue.isPresent()) {
			Integer val = attValue.get();

			if (val == expectedValue) {
				// trying to remove
				Map<String, ParcelDTO> existedMap = hm.get(parcelId);
				existedMap.remove(attribute);

				// only setting up the expiry
				List<ParcelDTO> existedList = historicalRecordMap.get(parcelId).get(attribute);
				existedList.get(existedList.size() - 1).setExpiryTime(timestamp);

				for (ParcelDTO dto : existedList) {
					System.out.println("Value: " + dto.getValue() + " Start: " + dto.getTimestamp() + " Expiry: "
							+ dto.getExpiryTime());
				}
				return true;
			}
		}
		return false;
	};

	// ---------- Level 2 ----------
	/**
	 * Returns "attribute(value)" strings, lexicographically sorted by attribute
	 * name.
	 */
	public List<String> listAttributesByPrefix(int timestamp, String parcelId, String prefix) {
		List<String> list = new ArrayList<>();
		if (hm.containsKey(parcelId)) {
			Map<String, ParcelDTO> map = hm.get(parcelId);
			for (String str : map.keySet()) {
				if (str.startsWith(prefix) && timestamp < map.get(str).getExpiryTime()) {
					list.add(str + "(" + map.get(str).getValue() + ")");
				}
			}

		}

		return list;
	};

	// ---------- Level 3 ----------
	public void setAttributeWithTtl(int timestamp, String parcelId, String attribute, int value, int ttl) {

		ParcelDTO parcelDTO = new ParcelDTO(timestamp, parcelId, attribute, value);

		parcelDTO.setExpiryTime(timestamp + ttl);

		Map<String, ParcelDTO> map = new HashMap<>();
		map.put(attribute, parcelDTO);

		if (hm.containsKey(parcelId) && historicalRecordMap.containsKey(parcelId)) {
			Map<String, ParcelDTO> existedMap = hm.get(parcelId);
			existedMap.put(attribute, parcelDTO);

			// level - 4
			if (historicalRecordMap.get(parcelId).containsKey(attribute)) {
				List<ParcelDTO> existedList = historicalRecordMap.get(parcelId).get(attribute);
				existedList.get(existedList.size() - 1).setExpiryTime(timestamp);
				existedList.add(parcelDTO);
			} else {
				List<ParcelDTO> newList = new ArrayList<>();
				newList.add(parcelDTO);
				Map<String, List<ParcelDTO>> newMap = historicalRecordMap.get(parcelId);
				newMap.put(attribute, newList);
				historicalRecordMap.put(parcelId, newMap);

			}

			// can assume the timestamp is the starting time
			// we can set the expiry time for the last inserted record

		} else {
			hm.put(parcelId, map);

			// level - 4
			List<ParcelDTO> dtos = new ArrayList<>();
			dtos.add(parcelDTO);

			Map<String, List<ParcelDTO>> innerMap = new HashMap<>();
			innerMap.put(attribute, dtos);
			historicalRecordMap.put(parcelId, innerMap);

		}

	};

	// ---------- Level 4 ----------
	public Optional<Integer> getAttributeAt(int timestamp, String parcelId, String attribute, int atTimestamp) {
		if (!(historicalRecordMap.containsKey(parcelId) && historicalRecordMap.get(parcelId).containsKey(attribute))) {
			return Optional.empty();
		}
		List<ParcelDTO> list = historicalRecordMap.get(parcelId).get(attribute);

		for (int i = 0; i < list.size(); i++) {
			// atTimestamp stays between starttime and endtime
			if (atTimestamp >= list.get(i).getTimestamp() && atTimestamp < list.get(i).getExpiryTime()) {
				return Optional.of(list.get(i).getValue());
			}
		}

		return Optional.empty();
	};
}
