package com.kouchdb.dynamodb_source;

public record PutResponse(boolean ok, String id, String rev) {

}
