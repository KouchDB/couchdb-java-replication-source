package com.kouchdb.dynamodb_source;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DocumentResponse(@JsonProperty("_id") String id, @JsonProperty("_rev") String rev) {
}
