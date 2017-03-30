package com.daffodil.documentumie.iebusiness;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class UIInfoBean {
	String repository;
	String userName;
	String password;
	String domain;
	
	protected PropertyChangeSupport propertyChangeSupport;
	
	public UIInfoBean(){
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a property change listener for a specific property.
	 * 
	 * @param propertyName
	 *            The name of the property that was listened on.
	 * @param listener
	 *            The <code>PropertyChangeListener</code> to be removed
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	public String getDomain() {
		return domain;
	}


	public void setDomain(String domain) {
		String old = this.domain; 
		this.domain = domain;
		
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"domain", old, domain));
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		String old = this.password;
		this.password = password;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"password", old, password));
	}


	public String getRepository() {
		return repository;
	}


	public void setRepository(String repository) {
		String old = this.repository;
		this.repository = repository;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"repository", old, repository));
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		String old = this.userName;
		this.userName = userName;
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this,
				"password", old, userName));
	}
	
}
