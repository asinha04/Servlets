/**
 * 
 */
package org.kp.foundation.core.enums;

/**
 * This is the Enum holding class names for each region.
 * @author Utkarsh
 *
 */
public enum ColumnControlEnum {
	TWELVE_GRID("12", "columns-12","columns-12"),
	SIX_SIX_GRID("6", "columns-6","columns-6 -tablet-1"),
	EIGHT_GRID("8","columns-8","columns-8 -tablet-1"),
	FOUR_GRID("4","columns-4","columns-4 -tablet-1"),
	NINE_GRID("9", "columns-9","columns-9 -tablet-1"),
	THREE_GRID("3","columns-3","columns-3 -tablet-1"),
    TWO_GRID("2","columns-2","columns-2 -tablet-1");
	
	private final String gridLayout;
	private final String htmlClass;
	private final String htmlClassTablet;
	ColumnControlEnum(String gridLayout, String htmlClass, String htmlClassTablet){
        this.gridLayout = gridLayout;
        this.htmlClass = htmlClass;
        this.htmlClassTablet = htmlClassTablet;
    }
	/**
	 * @return the grid_layout
	 */
	public String getGridLayout() {
		return gridLayout;
	}
	/**
	 * @return the htmlClass
	 */
	public String getHtmlClass() {
		return htmlClass;
	}
	/**
	 * @return the htmlClassTablet
	 */
	public String getHtmlClassTablet() {
		return htmlClassTablet;
	}
	/**
	 * This method will return the grid details for the authored grid. 
	 * If any invalid details are provided it will return 12 Grid
	 * @param grid
	 * @return
	 */
	public static ColumnControlEnum getColumn(final String grid){
        for (ColumnControlEnum column: ColumnControlEnum.values()) {
            if(column.getGridLayout().equals(grid)){
                return column;
            }
        }
		return TWELVE_GRID;
    }
	
}
