package org.kp.foundation.core.enums;

import static org.kp.foundation.core.constants.RegionPicker.UNKNOWN_REGION;

/**
 * @author Rajesh Dwivedi on 5/23/17.
 */
public enum RegionEnum {
    /**  Whenever we add a region in RegionEnum, we should add corresponding entry in RegionXFEnum to get xf
    work correctly. **/
    REGION_DEFAULT("national", "national"),
    REGION_COLORADO_DENVER_BOULDER_MOUNTAIN_NORTHERN("colorado-denver-boulder-mountain-northern", "DB"),
    REGION_GEORGIA("georgia", "GGA"),
    REGION_HAWAII("hawaii", "HAW"),
    REGION_MARYLAND_VIRGINIA_WASHINGTON_DC("maryland-virginia-washington-dc", "MID"),// mid-atlantic
    REGION_NORTHERN_CALIFORNIA("northern-california", "MRN"),
    REGION_OREGON_WASHINGTON("oregon-washington", "KNW"),// northwest
    REGION_SOUTHERN_CALIFORNIA("southern-california", "SCA"),
    REGION_SOUTHERN_COLORADO("southern-colorado", "CS"),
    REGION_WASHINGTON("washington", "WA"),
    REGION_COLORADO("colorado", "COL"),
    REGION_UNKNOWN(UNKNOWN_REGION, UNKNOWN_REGION);

    private String name;
    private String code;
    RegionEnum(String theName, String theCode){
        this.name = theName;
        this.code = theCode;
    }

    public String getName(){
        return name;
    }

    public String getCode(){
        return code;
    }

    public static RegionEnum getRegionCodeByRegionName(final String regionName){
        for (RegionEnum regionEnum: RegionEnum.values()) {
            if(regionEnum.getName().equals(regionName)){
                return regionEnum;
            }
        }
        return REGION_UNKNOWN;
    }

    public static RegionEnum getRegionNameByRegionCode(final String regionCode){
        for (RegionEnum regionEnum: RegionEnum.values()) {
            if(regionEnum.getCode().equals(regionCode)){
                return regionEnum;
            }
        }
        return REGION_UNKNOWN;
    }
}
