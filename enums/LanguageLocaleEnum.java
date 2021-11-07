package org.kp.foundation.core.enums;

public enum LanguageLocaleEnum {
	EN("en-US"), ES("es-us");
	private String name;
	private static final  String ENGLISH = "en";
	private static final  String ESPANOL = "es";

	private LanguageLocaleEnum(String name) {
		this.name = name;
	}

	public String getLabel() {
		return name;
	}

	public static String getLocalJcrPath(final String val){
		if(EN.getLabel().equalsIgnoreCase(val)){
			return ENGLISH;
		} else if(ES.getLabel().equalsIgnoreCase(val)){
			return ESPANOL;
		}else {
			return ENGLISH;
		}
	}
}
