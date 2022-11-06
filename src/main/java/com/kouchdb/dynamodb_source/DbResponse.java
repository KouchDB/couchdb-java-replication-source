package com.kouchdb.dynamodb_source;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DbResponse(String db_name, String update_seq, int doc_count, int doc_del_count) {
	
	@JsonProperty("instance_start_time")
	public int instance_start_time() {
		return 0;
	}
}
