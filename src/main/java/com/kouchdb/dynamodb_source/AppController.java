package com.kouchdb.dynamodb_source;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kouchdb.dynamodb_source.ChangesResponse.Result;
import com.kouchdb.dynamodb_source.ChangesResponse.Revision;
import com.kouchdb.dynamodb_source.DocumentResponse.Revisions;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AppController {
	
	private static long startTime = System.currentTimeMillis();

	@RequestMapping(value = "/{db}/_changes", method = RequestMethod.GET)
	@ResponseBody
	public String getChanges(@PathVariable String db,
			@RequestParam(defaultValue = "") String feed,
			@RequestParam(defaultValue = "") String style,
			@RequestParam(defaultValue = "0") String since,
			@RequestParam(defaultValue = "-1") Integer timeout) throws JsonProcessingException {
		if(since.equals("0")) {
			System.err.println(new ObjectMapper().writeValueAsString(new ChangesResponse(Arrays.asList(new Result("1-a11f390ffa77a03c557ffbbc7c5fda75", "id-1",
					Arrays.asList(new Revision("1-abcd")))), "1-a11f390ffa77a03c557ffbbc7c5fda75", 0)));
			return "{\"results\":[\n{\"seq\":\"1-a11f390ffa77a03c557ffbbc7c5fda75\",\"id\":\"id-1\",\"changes\":[{\"rev\":\"1-abcd\"}]}\n],\n\"last_seq\":\"1-a11f390ffa77a03c557ffbbc7c5fda75\",\"pending\":0}";
		}
		else {
			return "{\"results\":[\n\n],\n\"last_seq\":\"1-a11f390ffa77a03c557ffbbc7c5fda75\",\"pending\":0}";
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

	@RequestMapping(value = "/{db}/_local/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public PutResponse putLocal(@PathVariable String db,
			@PathVariable String id, @RequestBody String body) {
		return new PutResponse(true, "_local/" + id, "0-1" );
	}

	@RequestMapping(value = "/{db}", method = {RequestMethod.GET,
			RequestMethod.HEAD})
	@ResponseBody
	public DbResponse getDatabase(@PathVariable String db) {
		return new DbResponse(db, "1-a11f390ffa77a03c557ffbbc7c5fda75", 1, 0);
	}

	@RequestMapping(value = "/{db}/", method = {RequestMethod.GET,
			RequestMethod.HEAD})
	@ResponseBody
	public DbResponse getDatabaseSlash(@PathVariable String db) {
		return this.getDatabase(db);
	}

	@RequestMapping(value = "/{db}/{id}", method = {RequestMethod.GET})
	public void getDocument(@PathVariable String db, @PathVariable String id,
			@RequestParam boolean revs, @RequestParam String open_revs, @RequestParam boolean latest,
			HttpServletResponse response) throws JsonProcessingException, IOException {
		String boundary = "8543aec6b82f9d3714c456a488a25810";
		response.setContentType("multipart/mixed; boundary=\"" + boundary + "\"");
		ServletOutputStream os = response.getOutputStream();
		PrintWriter pw = new PrintWriter(os);
		pw.println("--" + boundary);
		pw.println("Content-Type: application/json");
		pw.println();
		pw.println(new ObjectMapper().writeValueAsString(new DocumentResponse("id-1", "1-abcd",
						new Revisions(1, Arrays.asList("abcd")))));
		pw.println("--" + boundary + "--");
		pw.close();
	}

	@RequestMapping(value = "/_session", method = RequestMethod.POST)
	@ResponseBody
	public SessionResponse startSession(HttpServletResponse response) {
		response.addCookie(new Cookie("AuthSession", "dummy"));
		return new SessionResponse(true, "dbadmin", Arrays.asList("_admin"));
	}
	
	@RequestMapping(value = "/{db}/_ensure_full_commit", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED) // see couch_replicator_api_wrap.erl
	@ResponseBody
	public CommitResponse commit(@PathVariable String db, @RequestBody(required = false) String body) {
		System.err.println("BODY = " + body);
		return new CommitResponse(true, 0);
	}
	
	@RequestMapping(value = "/{db}/_bulk_get", method = RequestMethod.POST)
	@ResponseBody
	public String bulkGet() {
		throw new UnsupportedOperationException();
	}
	
	@RequestMapping(value = "/error", method = RequestMethod.POST)
	@ResponseBody
	public String error(@RequestBody(required = false) String body) {
		System.err.println("BODY = " + body);
		return "{\"ok\":true}";
	}
	
}
