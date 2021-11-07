package org.kp.foundation.core.models;

/**
 * Language Model class
 * 
 */

public class LanguageModel {

	private String languageTitle;
	private String languagePath;
	private String localeCookie;
	private String locale;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLocaleCookie() {
		return localeCookie;
	}

	public void setLocaleCookie(String localeCookie) {
		this.localeCookie = localeCookie;
	}

	public String getLanguageTitle() {
		return languageTitle;
	}

	public void setLanguageTitle(String languageTitle) {
		this.languageTitle = languageTitle;
	}

	public String getLanguagePath() {
		return languagePath;
	}

	public void setLanguagePath(String languagePath) {
		this.languagePath = languagePath;
	}
	
	@Override
    public String toString() {
        return "LanguageModel [languageTitle=" + languageTitle + ", languagePath=" + languagePath + ", localeCookie="
                + localeCookie + ", locale=" + locale + "]";
    }
}
