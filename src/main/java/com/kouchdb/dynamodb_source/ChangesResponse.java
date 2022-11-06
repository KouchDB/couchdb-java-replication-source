package com.kouchdb.dynamodb_source;

import java.util.List;

public record ChangesResponse(List<Result> results, String last_seq, int pending) {
	
	public record Revision(String rev) {};
	
	public record Result(String seq, String id, List<Revision> changes) {};
	
}
