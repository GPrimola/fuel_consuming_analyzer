package com.giorgio.gasconsuminganalyzer.domain;

public class AccountPassword extends DomainObject {

	private String account;
	private String address;
	private String login;
	private String password;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String service) {
		this.account = service;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	/**
	 * Generated
	 */
	private static final long serialVersionUID = -2639813854309794891L;

}
