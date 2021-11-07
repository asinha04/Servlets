package org.kp.foundation.core.enums;

public enum LocaleEnum {
	EN("en_US"), ES("es_US");
	private String label;

	private LocaleEnum(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
		
}
