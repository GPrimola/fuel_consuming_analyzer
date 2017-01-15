package com.giorgio.gasconsuminganalyzer.domain;

import java.io.Serializable;

public abstract class DomainObject implements Serializable {
	
	public static final String KEY_ID = "id";
	
	protected Long id;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Generated
	 */
	private static final long serialVersionUID = 2409548885863495274L;

}
