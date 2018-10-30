package com.spring.form;

import java.util.Calendar;
import java.util.Date;

public class QueryDetailsBean{
	
	private String queryId;
	
	private String query;
	
	private Date createdDate;
	
	private int docCount;
	
	public QueryDetailsBean() {
		this.createdDate = Calendar.getInstance().getTime();
	}
	
	public QueryDetailsBean(String queryId, String query) {
		this.queryId = queryId;
		this.query = query;
		this.createdDate = Calendar.getInstance().getTime();
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public int getDocCount() {
		return docCount;
	}

	public void setDocCount(int numFound) {
		this.docCount = numFound;
	}

	@Override
	public String toString() {
		return "QueryIdDetailsBean [queryId=" + queryId + ", query=" + query + ", createdDate=" + createdDate
				+ ", docCount=" + docCount + "]";
	}
}
