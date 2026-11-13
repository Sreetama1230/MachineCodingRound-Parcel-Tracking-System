package com.example.parcel.service;

import com.example.parcel.model.ParcelDTO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Storage: parcelId -> attribute -> versions (ordered by startTime, oldest
 * first). The last element of a version list is the "current" version.
 * Attributes are kept in a TreeMap so prefix listing is already
 * lexicographically sorted.
 */
@Service
public class ParcelTrackingSystemImpl implements ParcelTrackingSystem {

	Map<String, Map<String, ParcelDTO>> hm = new HashMap<>();

	// ---------- Level 1 ----------
	public void setAttribute(int timestamp, String parcelId, String attribute, int value) {
		ParcelDTO parcelDTO = new ParcelDTO(timestamp, parcelId, attribute, value);

		Map<String, ParcelDTO> map = new HashMap<>();
		map.put(attribute, parcelDTO);
		if (hm.containsKey(parcelId)) {
			Map<String, ParcelDTO> existedMap = hm.get(parcelId);
			existedMap.put(attribute, parcelDTO);
		} else {
			hm.put(parcelId, map);
		}

	};

	public Optional<Integer> getAttribute(int timestamp, String parcelId, String attribute) {

		if (!(hm.containsKey(parcelId) && hm.get(parcelId).containsKey(attribute))) {
			return Optional.empty();
		}
		Map<String, ParcelDTO> map = hm.get(parcelId);
		return Optional.of(map.get(attribute).getValue());
	};

	public boolean updateIfMatch(int timestamp, String parcelId, String attribute, int expectedValue, int newValue) {

		Optional<Integer> attValue = getAttribute(timestamp, parcelId, attribute);

		if (attValue.isPresent()) {
			Integer val = attValue.get();

			if (val == expectedValue) {
				setAttribute(timestamp, parcelId, attribute, newValue);
				return true;
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
				Map<String, ParcelDTO> existedMap =
						hm.get(parcelId);
				existedMap.remove(attribute);
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
		if(hm.containsKey(parcelId)) {
			Map<String, ParcelDTO> map =	hm.get(parcelId);
			for(String str : map.keySet()) {
				if(str.startsWith(prefix)) {
					list.add(str+"("+map.get(str).getValue()+")");
				}
			}
		
			
		}
	
		return list;
	};

	// ---------- Level 3 ----------
	public void setAttributeWithTtl(int timestamp, String parcelId, String attribute, int value, int ttl) {
	

	};

	// ---------- Level 4 ----------
	public Optional<Integer> getAttributeAt(int timestamp, String parcelId, String attribute, int atTimestamp) {
		return null;
	};
}
