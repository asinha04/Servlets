package org.kp.foundation.core.enums;

public enum PictureDataHeroImageEnum {

	MOBILE("-mobile", "(max-width: 599px)"), TABLET("-tablet", "(max-width: 767px)"), DESKTOP_SMALL("-s-dt",
			"(max-width: 1023px)"), DESKTOP_MEDIUM("-m-dt", "(max-width: 1279px)"), DESKTOP_LARGE("-l-dt", "");

	private String extension;
	private String mediaString;

	PictureDataHeroImageEnum(String extension, String media_string) {
		this.extension = extension;
		this.mediaString = media_string;
	}

	public String getExtension() {
		return extension;
	}

	public String getMediaString() {
		return mediaString;
	}
}