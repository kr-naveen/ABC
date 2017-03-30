package com.daffodil.documentumie.dctm;

import com.daffodil.documentumie.dctm.exception.DDfException;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfLoginInfo;


public class SessionHandler {

	private static IDfSession iDfSession = null;
	private static IDfSessionManager iDfSessionManager = null;
	
	String repositoryName;
	String userName;
	String password;
	String domain;

	public void login(String repoName, String userName, String password,
			String domain) throws DDfException {

		this.repositoryName = repoName;
		this.userName = userName;
		this.password = password;
		this.domain = domain;
		try {
			getIDfSession();
		} catch (DDfException e) {
		throw new DDfException("Please check username and password " /*+ e.getMessage()*/, e.getCause());
		}
	}

	public IDfSession getIDfSession() throws DDfException {
		if (iDfSession != null) {
			return iDfSession;
		}

		try {
			iDfSession = getIDfSessionManager().getSession(repositoryName);
		} catch (DfIdentityException e) {
			throw new DDfException("user Id or Password invalid " + e.getMessage(), e.getCause());
		} catch (DfAuthenticationException e) {

			throw new DDfException("user Id or Password invalid " + e.getMessage(), e.getCause());
		} catch (DfPrincipalException e) {

			throw new DDfException("user Id or Password invalid " + e.getMessage(), e.getCause());
		} catch (DfServiceException e) {

			throw new DDfException("user Id or Password invalid " + e.getMessage(), e.getCause());
		}
		return iDfSession;
	}

	public IDfSessionManager getIDfSessionManager() throws DDfException {
	/*	if (iDfSessionManager != null) {
			
			return iDfSessionManager;
		}
	*/	
		
		IDfLoginInfo loginInfo = new DfLoginInfo();
		loginInfo.setUser(userName);
		loginInfo.setPassword(password);
		
		iDfSessionManager = getClient().newSessionManager();
		try {
			iDfSessionManager.setIdentity(repositoryName, loginInfo);
		} catch (DfServiceException e) {
			throw new DDfException("user Id or Password invalid "+e.getMessage(), e.getCause());
		}
		return iDfSessionManager;
	}

	public IDfClient getClient() throws DDfException {
		IDfClient client = null;
		try {
			client = DfClient.getLocalClient();
			
		} catch (Exception e) {
			throw new DDfException("Error on Loading of DockBroker "+e.getMessage(), e.getCause());
		}
		return client;
	}
	
	public void closeSession(){
		if (iDfSessionManager != null && iDfSession != null && iDfSession.isConnected()) {
			iDfSessionManager.release(iDfSession);
			iDfSession = null;
			iDfSessionManager = null;
		}
	}
}
