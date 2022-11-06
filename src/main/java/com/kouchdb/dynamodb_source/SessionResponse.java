package com.kouchdb.dynamodb_source;

import java.util.List;

public record SessionResponse(boolean ok, String name, List<String> roles) {
}
