package com.kouchdb.dynamodb_source;

public record CommitResponse(boolean ok, long instance_start_time) {
}
