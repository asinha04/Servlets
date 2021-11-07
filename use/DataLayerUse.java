package org.kp.foundation.core.use;

import org.kp.foundation.core.constants.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

public class DataLayerUse extends BaseWCMUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataLayerUse.class);
    private static final String SECURE_PAGE = "/secure";
    private static final String ERROR_MESSAGE = "Current Page Title in Data Layer Use Class {}."; 
    private Page currentPage;

    /**
     * Activate method that HTL executes
     */
    @Override
    public void activate() throws Exception {
        currentPage = getCurrentPage();
    }

    public String getFeatureName(){
        String featureName="KP Home Page";
        int depth = this.currentPage.getDepth();
        if (depth > 4 && this.currentPage.getAbsoluteParent(4) != null) {
          if (this.currentPage.getAbsoluteParent(4).getPath().endsWith(SECURE_PAGE)){
               LOGGER.debug(ERROR_MESSAGE, this.currentPage.getAbsoluteParent(5).getTitle());
               return this.currentPage.getAbsoluteParent(5).getTitle();
          } else {
               LOGGER.debug(ERROR_MESSAGE, this.currentPage.getAbsoluteParent(4).getTitle());
               return this.currentPage.getAbsoluteParent(4).getTitle();
          }  
        } else{
            LOGGER.debug(ERROR_MESSAGE, featureName);
            return featureName;
        }
    }

    /**
     * Retrieves a colon-delimited list, as a String, of the current page's ancestry starting with a depth of 1 to omit /content
     * and ending with its parent.  Example: if currentPage.getPath() is /content/kporg/en/national/why-kp then 
     * getPageName() would return the String kporg:en:national
     * 
     * @return a colon delimited String of the current page's ancestry starting at depth 1 and descending to its parent.
     */
    public String getPageName(){
        String pageName = "";

        // For launches, the absolute parent at depth 1 is a sling:Folder (/content/launches) and not a cq:Page so let's null check
        if(null != this.currentPage.getAbsoluteParent(1)) {
	        for(int i=1; i < this.currentPage.getDepth(); i++) {
	            if(i==1) {
	                pageName=this.currentPage.getAbsoluteParent(i).getName();
	            } else {
	                pageName += ":" + this.currentPage.getAbsoluteParent(i).getName();
	            }
	        }
        }
        String selectors [] = getRequest().getRequestPathInfo().getSelectors();
        for (String sels: selectors) {           
        		pageName = pageName+"."+sels;
        }
        return pageName;
    }

    public String getPrimaryCategory(){
        return GlobalConstants.ORG_NAME;
    }

    public String getSubCategory1(){
        String subCategory1 = GlobalConstants.ORG_NAME;
        int depth = this.currentPage.getDepth();
        if(depth > 2 && this.currentPage.getAbsoluteParent(2) != null){
            subCategory1 +=  ":" + this.currentPage.getAbsoluteParent(2).getName();
            return subCategory1;
        } else{
            return "";
        }
    }

    public String getSubCategory2(){
        int depth = this.currentPage.getDepth();
        if(depth > 3 && this.currentPage.getAbsoluteParent(3) != null){
            return this.getSubCategory1() + ":" + this.currentPage.getAbsoluteParent(3).getName();
        } else{
            return "";
        }
    }

    public String getSubCategory3(){
        int depth = this.currentPage.getDepth();
        if(depth > 4 && this.currentPage.getAbsoluteParent(4) != null){
            return this.getSubCategory2() + ":" + this.currentPage.getAbsoluteParent(4).getName();
        } else{
            return "";
        }
    }

    public String getSubCategory4(){
        int depth = this.currentPage.getDepth();
        if(depth > 5 && this.currentPage.getAbsoluteParent(5) != null){
            return this.getSubCategory3() + ":" + this.currentPage.getAbsoluteParent(5).getName();
        } else{
            return "";
        }
    }

    public String getSubCategory5(){
        int depth = this.currentPage.getDepth();
        if(depth > 6 && this.currentPage.getAbsoluteParent(6) != null){
            return this.getSubCategory4() + ":" + this.currentPage.getAbsoluteParent(6).getName();
        } else{
            return "";
        }
    }
}
