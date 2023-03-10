package org.insightech.er.wacky.erutil;

import org.insightech.er.editor.model.diagram_contents.element.node.table.column.*;
import org.insightech.er.editor.model.*;
import org.insightech.er.wacky.export.*;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.insightech.er.*;
import org.insightech.er.util.*;
import org.insightech.er.db.*;

public class WackyColumn
{
    private NormalColumn column;
    private ERDiagram diagram;
    private WackyExportUtil util;
    private boolean vpk;
    
    public WackyColumn(final NormalColumn column, final boolean vpk, final ERDiagram diagram, final WackyExportUtil util) {
        this.column = null;
        this.diagram = null;
        this.util = null;
        this.vpk = false;
        this.util = util;
        this.diagram = diagram;
        this.column = column;
        this.vpk = vpk;
    }
    
    public String getName() {
        return this.column.getPhysicalName();
    }
    
    public String getCamelName() {
        return this.util.getCamelName(this.getName());
    }
    
    public String getCapitalName() {
        return this.util.getCapitalName(this.getName());
    }
    
    public String getUpperName() {
        return this.getName().toUpperCase();
    }
    
    public String getComment() {
        return this.column.getLogicalName();
    }
    
    public boolean isPk() {
        return this.vpk;
    }
    
    public String getJavaClass() {
        return this.util.getJavaClass(this.column.getType());
    }
    
    public boolean hasLength() {
        return this.column.getTypeData().getLength() != null;
    }
    
    public Integer getLength() {
        return this.column.getTypeData().getLength();
    }
    
    public boolean isUniqueKey() {
        return this.column.isUniqueKey();
    }
    
    public boolean isNotNull() {
        return this.column.isNotNull();
    }
    
    public String getColumnAnnotation() {
        final StringBuffer sb = new StringBuffer();
        sb.append("name=\"" + this.getName() + "\" ");
        if (this.isUniqueKey()) {
            sb.append(",unique = true  ");
        }
        sb.append(",nullable = " + !this.isNotNull());
        String tname = column.getWord().getType().getId().toLowerCase();
		if(tname.endsWith("text")){
        	sb.append(",columnDefinition =\"" + tname + "\" ");
        	if("tinytext".equals(tname)) {
        		sb.append(",length = 255 ");
        	}else if("text".equals(tname)) {
        		sb.append(",length = 65535 ");
        	}else if("mediumtext".equals(tname)) {
        		sb.append(",length = 16777215 ");
        	}else if("longtext".equals(tname)) {
        		
        	}
        }else {
            if (this.hasLength()) {
                sb.append(",length = " + this.getLength());
            }
        }
        return sb.toString();
    }
    
    public String getDefault() {
        String value = null;
        if (!Check.isEmpty(this.column.getDefaultValue())) {
            String defaultValue = this.column.getDefaultValue();
            if (ResourceString.getResourceString("label.current.date.time").equals(defaultValue)) {
                defaultValue = this.getDBManager().getCurrentTimeValue()[0];
            }
            if (this.doesNeedQuoteDefaultValue(this.column)) {
                value = "'" + Format.escapeSQL(defaultValue) + "'";
            }
            else {
                value = defaultValue;
            }
        }
        return value;
    }
    
    public boolean hasDefault() {
        final String value = this.getDefault();
        return value != null;
    }
    
    public String getDefaultAnnotation() {
        final String value = this.getDefault();
        if (value == null) {
            return "";
        }
        return "@ColumnDefault(value=\"" + value + "\")";
    }
    
    public String getDescript() {
        String logicalName = this.column.getLogicalName();
        if (logicalName == null) {
            logicalName = "";
        }
        logicalName = logicalName.replace('\r', ' ');
        logicalName = logicalName.replace('\n', ' ');
        String description = this.column.getDescription();
        if (description == null) {
            description = "";
        }
        description = description.replace('\r', ' ');
        description = description.replace('\n', ' ');
        if (description.indexOf(logicalName) == -1) {
            return String.valueOf(logicalName) + " " + description;
        }
        return description;
    }
    /**
     * 获取加密来源字段
     * @return
     */
    public String getOriginColumn() {
         String name = this.column.getPhysicalName();
         if (!name.startsWith("sec_") && !name.startsWith("qry_") && !name.startsWith("sci_")){
        	 return null;
         }
         if(name.startsWith("last_qry_")) {
             return name.substring(9);
         }
         return name.substring(4);
    }
    
    public String getOriginColumnCapitalName() {
    	String originName = getOriginColumn();
        return originName == null ? "" : this.util.getCapitalName(originName);
    }
    
    /**
     * 获取加密字段
     * @return
     */
    public String getSecurityColumn() {
    	 String description = this.column.getDescription();
         if (description == null) {
             description = "";
         }
         log(IStatus.INFO, getName()+" 的description:"+description);
         String name = this.column.getPhysicalName();
         int secOriginIndex= description.indexOf("sec_origin=");
         if (secOriginIndex != -1) {
        	 return name;
         } 
         description = this.column.getLogicalName();
    	 secOriginIndex= description.indexOf("的");
    	 if ((name.startsWith("sec_") || name.startsWith("qry_") || name.startsWith("sci_")) && secOriginIndex > 0) {
    		 return name;
    	 }
    	 return null;
    }
    
    /**
     * 是否是全文加密字段
     * @return
     */
    public boolean isSec() {
    	 String description = this.column.getDescription();
         if (description == null) {
             description = "";
         }
         log(IStatus.INFO, getName()+" 的description:"+description);
         String name = this.column.getPhysicalName();
         int secOriginIndex= description.indexOf("sec_origin=");
         if (secOriginIndex != -1) {
        	 return name.startsWith("sec_");
         } 
         description = this.column.getLogicalName();
    	 secOriginIndex= description.indexOf("的");
    	 if ((name.startsWith("sec_")|| name.startsWith("qry_")) && secOriginIndex > 0) {
    		 return name.startsWith("sec");
    	 }
    	 return false;
    }
    
    /**
     * 是否是模糊查询字段
     * @return
     */
    public boolean isQry() {
    	 String description = this.column.getDescription();
         if (description == null) {
             description = "";
         }
         log(IStatus.INFO, getName()+" 的description:"+description);
         String name = this.column.getPhysicalName();
         int secOriginIndex= description.indexOf("sec_origin=");
         if (secOriginIndex != -1) {
        	 return name.startsWith("qry_");
         } 
         description = this.column.getLogicalName();
    	 secOriginIndex= description.indexOf("的");
    	 if (name.startsWith("qry_") && secOriginIndex > 0) {
    		 return name.startsWith("qry_");
    	 }
    	 return false;
    }
    
    /**
     * 是否是后几位查询字段
     * @return
     */
    public boolean isLastQry() {
    	 String description = this.column.getDescription();
         if (description == null) {
             description = "";
         }
         log(IStatus.INFO, getName()+" 的description:"+description);
         String name = this.column.getPhysicalName();
         int secOriginIndex= description.indexOf("sec_origin=");
         if (secOriginIndex != -1) {
        	 return name.startsWith("last_qry_");
         } 
         description = this.column.getLogicalName();
    	 secOriginIndex= description.indexOf("的");
    	 if (name.startsWith("last_qry_") && secOriginIndex > 0) {
    		 return name.startsWith("last_qry_");
    	 }
    	 return false;
    }
    
    /**
     * 是否是摘要字段
     * @return
     */
    public boolean isSci() {
    	 String description = this.column.getDescription();
         if (description == null) {
             description = "";
         }
         log(IStatus.INFO, getName()+" 的description:"+description);
         String name = this.column.getPhysicalName();
         int secOriginIndex= description.indexOf("sci_origin=");
         if (secOriginIndex != -1) {
        	 return name.startsWith("sci_");
         } 
         description = this.column.getLogicalName();
    	 secOriginIndex= description.indexOf("的");
    	 if (name.startsWith("sci_") && secOriginIndex > 0) {
    		 return name.startsWith("sci_");
    	 }
    	 return false;
    }
    
    protected DBManager getDBManager() {
        return DBManagerFactory.getDBManager(this.diagram);
    }
    
    protected boolean doesNeedQuoteDefaultValue(final NormalColumn normalColumn) {
        return !normalColumn.getType().isNumber() && (!normalColumn.getType().isTimestamp() || Character.isDigit(normalColumn.getDefaultValue().toCharArray()[0]));
    }
    
    public void log(final int level, final String text) {
		final ILog log = ERDiagramActivator.getDefault().getLog();
		log.log((IStatus) new Status(level, "org.insightech.er.wacky", text));
	}
}
