package com.kouchdb.dynamodb_source;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DocumentResponse(@JsonProperty("_id") String id, @JsonProperty("_rev") String rev,
		@JsonProperty("_revisions") Revisions revs) {
	
	@JsonProperty("some_data")
	public String some_data() {
		return "here";
	}
	
	public record Revisions(int start, List<String> ids) {}
}
