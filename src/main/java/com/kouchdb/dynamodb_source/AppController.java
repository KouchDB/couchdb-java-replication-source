package com.kouchdb.dynamodb_source;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kouchdb.dynamodb_source.ChangesResponse.Result;
import com.kouchdb.dynamodb_source.ChangesResponse.Revision;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AppController {
	
	private static long startTime = System.currentTimeMillis();

	@RequestMapping(value = "/{db}/_changes", method = RequestMethod.GET)
	@ResponseBody
	public ChangesResponse getChanges(@PathVariable String db,
			@RequestParam(defaultValue = "") String feed,
			@RequestParam(defaultValue = "") String style,
			@RequestParam(defaultValue = "") String since,
			@RequestParam(defaultValue = "-1") Integer timeout) {
		if(since.equals("0")) {
			return new ChangesResponse(Arrays.asList(new Result("seq-1", "id-1",
					Arrays.asList(new Revision("1-rev")))), "seq-1", 0);
		}
		else {
			return new ChangesResponse(Arrays.asList(), "seq-1", 0);
		}
	}

	@RequestMapping(value = "/{db}/_local/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getLocal(@PathVariable String db,
			@PathVariable String id) {
		return new ResponseEntity<String>(
				"{\"error\":\"not_found\",\"reason\":\"missing\"}",
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/{db}/", method = {RequestMethod.GET,
			RequestMethod.HEAD})
	@ResponseBody
	public DbResponse getDatabase(@PathVariable String db) {
		return new DbResponse(db, "seq-1", 1, 0);
	}

	@RequestMapping(value = "/{db}/{id}", method = {RequestMethod.GET})
	@ResponseBody
	public MultiValueMap<String, Object> getDocument(@PathVariable String db, @PathVariable String id,
			@RequestParam boolean revs, @RequestParam String open_revs, @RequestParam boolean latest,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		response.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
		MultiValueMap<String, Object> form = new LinkedMultiValueMap<String, Object>();  
		form.add("json", new DocumentResponse("id-1", "1-rev"));
		return form;
	}

	@RequestMapping(value = "/_session", method = RequestMethod.POST)
	@ResponseBody
	public SessionResponse startSession(HttpServletResponse response) {
		response.addCookie(new Cookie("AuthSession", "dummy"));
		return new SessionResponse(true, "dbadmin", Arrays.asList("_admin"));
	}
	
	@RequestMapping(value = "/{db}/_ensure_full_commit", method = RequestMethod.POST)
	@ResponseBody
	public CommitResponse commit(@PathVariable String db) {
		return new CommitResponse(true, startTime);
	}
}
