package org.kp.foundation.core.use;

import com.adobe.cq.sightly.WCMUsePojo;
public class AlertsArticleUse extends WCMUsePojo{
	
	boolean notification=false;
	boolean bulletin=false;
	
	
	
	public boolean isNotification() {
		return notification;
	}



	public void setNotification(boolean isNotification) {
		this.notification = isNotification;
	}



	public boolean isBulletin() {
		return bulletin;
	}
	
	public String getPath() {
		return getCurrentPage().getPath();
	}



	public void setBulletin(boolean isBulletin) {
		this.bulletin = isBulletin;
	}



	@Override
	public void activate() throws Exception {
		String path=getCurrentPage().getPath();
		if(path.endsWith("notification")){
			this.notification=true;
		}
		else if(path.endsWith("bulletin")){
			this.bulletin=true;
		}
		
	}

}
