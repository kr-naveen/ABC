package com.daffodil.documentumie.iebusiness;

import javax.swing.JPanel;

import com.daffodil.documentumie.dctm.api.CSServices;
import com.daffodil.documentumie.fileutil.logger.IELogger;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataReader;
import com.daffodil.documentumie.fileutil.metadata.api.MetadataWriter;

public abstract class AbstractUIControl extends JPanel {

	private Object uiInfoBean;

	private IEMainAbstractUIControl mainUIControl;

	public AbstractUIControl() {

	}

	public void initlizeUI() {
		initUI();
		installListener();
	}

	public Object getUiInfoBean() {

		return uiInfoBean;

	}

	public void setUiInfoBean(Object val) {
		this.uiInfoBean = val;
	}

	public String showIEUI(Object uiInfoBean) {
		setUiInfoBean(uiInfoBean);
		preInilize();

		/*StringBuffer msg = null;
		msg = errorMessage();
		System.out.println("inside abstr   "+msg);
		if (msg != null) {
			return msg.toString();
		}*/

		return null;
	}

	public void showMessageDialog(String msg) {
		getIEMessageUtility().showMessageDialog(msg, null);
	}

	public String hideUI() {
		StringBuffer msg = null;
		msg = validateUIInputs();
		if (msg != null) {
			System.out.println("Hide return msg "
					+msg.toString());
			return msg.toString();
		}
		postValidateAction();

		return null;
	}

	public String CheckException(){
		StringBuffer msg;
		msg = errorMessage();
		if (msg != null) {
			return msg.toString();
		} else {
			postInilize();
		}
		return null;
	}

	protected void postValidateAction() {
	}

	protected void initUI() {
		setOpaque(false);
	}

	public void setMainUIControl(IEMainAbstractUIControl ui) {

		mainUIControl = ui;
	}

	protected IELogger getIELogger() {
		return mainUIControl.getIELogger();
	}

	protected CSServices getCSServiceProvider() {

		return mainUIControl.getCSServiceProvider();
	}

	protected MetadataReader getMetadataReader() {
		return mainUIControl.getMetadataReader();
	}

	protected MetadataWriter getMetadataWriter() {
		return mainUIControl.getMetadataWriter();
	}

	protected IEMessageUtility getIEMessageUtility(){
		return mainUIControl.getIEMessageUtility();
	}

	protected void installListener() {
	}

	protected abstract void preInilize();

	protected abstract void postInilize();

	protected abstract StringBuffer validateUIInputs();

	public abstract String getImageLocation();

	public abstract String getShowMessage();

	public IEMainAbstractUIControl getMainUIControl() {
		return mainUIControl;
	}

	protected abstract StringBuffer errorMessage();
}
