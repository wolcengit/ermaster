package org.insightech.er.wacky.erutil;

import org.insightech.er.wacky.export.*;
import org.insightech.er.editor.model.diagram_contents.element.node.table.*;
import org.insightech.er.editor.model.*;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.*;
import java.util.*;
import org.apache.commons.lang.*;

public class WackyTable
{
    private WackyExportUtil util;
    private ERTable table;
    private List<WackyColumn> pkColumns;
    private List<WackyColumn> notPkColumns;
    private List<WackyColumn> notPkColumnsNoSec;
    private List<WackyColumn> allColumns;
    private List<WackyColumn> allColumnsNoSec;
    private List<WackyColumn> findColumns;
    private List<String> pkPageages;
    private List<String> notPkPageages;
    private List<String> allPageages;
    private WackyColumn pkColumn;
    private ERDiagram diagram;
    
    public WackyTable(final ERTable table, final ERDiagram diagram, final WackyExportUtil util) {
        this.util = null;
        this.table = null;
        this.pkColumns = null;
        this.notPkColumns = null;
        this.notPkColumnsNoSec = null;
        this.allColumns = null;
        this.allColumnsNoSec = null;
        this.findColumns = null;
        this.pkPageages = null;
        this.notPkPageages = null;
        this.allPageages = null;
        this.pkColumn = null;
        this.diagram = null;
        this.util = util;
        this.diagram = diagram;
        this.table = table;
        this.pkColumns = new ArrayList<WackyColumn>();
        this.notPkColumns = new ArrayList<WackyColumn>();
        this.notPkColumnsNoSec = new ArrayList<WackyColumn>();
        this.allColumns = new ArrayList<WackyColumn>();
        this.allColumnsNoSec = new ArrayList<WackyColumn>();
        this.findColumns = new ArrayList<WackyColumn>();
        this.pkPageages = new ArrayList<String>();
        this.notPkPageages = new ArrayList<String>();
        this.allPageages = new ArrayList<String>();
        final int pks = table.getPrimaryKeySize();
        for (final NormalColumn column : table.getExpandedColumns()) {
            final String ct = util.getJavaClass(column.getType());
            if (column.isPrimaryKey() || pks == 0) {
                this.pkColumns.add(new WackyColumn(column, true, this.diagram, this.util));
                this.util.getImportPackges(column.getType(), this.pkPageages);
                this.allColumns.add(new WackyColumn(column, true, this.diagram, this.util));
                this.allColumnsNoSec.add(new WackyColumn(column, true, this.diagram, this.util));
                this.util.getImportPackges(column.getType(), this.allPageages);
                if (ct.equals("byte[]")) {
                    continue;
                }
                this.findColumns.add(new WackyColumn(column, true, this.diagram, this.util));
            }
            else {
                this.notPkColumns.add(new WackyColumn(column, column.isPrimaryKey(), this.diagram, this.util));
                this.util.getImportPackges(column.getType(), this.notPkPageages);
                this.allColumns.add(new WackyColumn(column, column.isPrimaryKey(), this.diagram, this.util));
                if(!column.getName().startWith("sec_")&& !column.getName().startWith("qry_")){
                    this.allColumnsNoSec.add(new WackyColumn(column, column.isPrimaryKey(), this.diagram, this.util));
                    this.notPkColumnsNoSec.add(new WackyColumn(column, column.isPrimaryKey(), this.diagram, this.util));
                }
                this.util.getImportPackges(column.getType(), this.allPageages);
                if (ct.equals("byte[]")) {
                    continue;
                }
                this.findColumns.add(new WackyColumn(column, true, this.diagram, this.util));
            }
        }
        if (table.getPrimaryKeySize() == 1) {
            final NormalColumn column = table.getPrimaryKeys().get(0);
            this.pkColumn = new WackyColumn(column, column.isPrimaryKey(), this.diagram, this.util);
        }
    }
    
    public int getCoulmnSize() {
        return this.allColumns.size();
    }
    
    public String getName() {
        return this.table.getPhysicalName();
    }
    
    public String getCamelName() {
        return this.util.getCamelName(this.getName());
    }
    
    public String getCapitalName() {
        return this.util.getCapitalName(this.getName());
    }
    
    public String getComment() {
        return this.table.getLogicalName();
    }
    
    public int getPkSize() {
        return this.table.getPrimaryKeySize();
    }
    
    public boolean isNoPk() {
        return this.table.getPrimaryKeySize() == 0;
    }
    
    public boolean isSinglePk() {
        return this.table.getPrimaryKeySize() == 1;
    }
    
    public boolean isMultPk() {
        return this.table.getPrimaryKeySize() > 1;
    }
    
    public WackyColumn getPkColumn() {
        return this.pkColumn;
    }
    
    public List<WackyColumn> getPkColumns() {
        return this.pkColumns;
    }
    
    public List<WackyColumn> getNotPkColumns() {
        return this.notPkColumns;
    }

    public List<WackyColumn> getNotPkColumnsNoSec() {
        return this.notPkColumnsNoSec;
    }
    
    public List<WackyColumn> getAllColumns() {
        return this.allColumns;
    }
    public List<WackyColumn> getAllColumnsNoSec() {
        return this.allColumnsNoSec;
    }
    
    public List<WackyColumn> getFindColumns() {
        return this.findColumns;
    }
    
    public List<String> getPkPageages() {
        return this.pkPageages;
    }
    
    public List<String> getNotPkPageages() {
        return this.notPkPageages;
    }
    
    public List<String> getAllPageages() {
        return this.allPageages;
    }
    
    public String getAllParameters() {
        return this.util.getParameters(this.allColumns, 0);
    }
    public String getAllParametersNoSec() {
        return this.util.getParameters(this.allColumnsNoSec, 0);
    }
    
    public String getPkParameters() {
        return this.util.getParameters(this.pkColumns, 0);
    }
    
    public String getNotPkParameters() {
        return this.util.getParameters(this.notPkColumns, 1);
    }

    public String getNotPkParametersNoSec() {
        return this.util.getParameters(this.notPkColumnsNoSec, 1);
    }
    
    public String getAllCallParameters() {
        return this.util.getCallParameters(this.allColumns, 0);
    }
    public String getAllCallParametersNoSec() {
        return this.util.getCallParameters(this.allColumnsNoSec, 0);
    }
    
    public String getPkCallParameters() {
        return this.util.getCallParameters(this.pkColumns, 0);
    }
    
    public String getNotPkCallParameters() {
        return this.util.getCallParameters(this.notPkColumns, 1);
    }
    public String getNotPkCallParametersNoSec() {
        return this.util.getCallParameters(this.notPkColumnsNoSec, 1);
    }
    
    public String getAttributeOverrides() {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        if (this.table.getPrimaryKeySize() == 0) {
            final int n = this.allColumns.size();
            for (final WackyColumn column : this.allColumns) {
                if (++i < n) {
                    sb.append("\t\t@AttributeOverride(name = \"" + column.getName() + "\", column = @Column(name = \"" + column.getName() + "\")),\r\n");
                }
                else {
                    sb.append("\t\t@AttributeOverride(name = \"" + column.getName() + "\", column = @Column(name = \"" + column.getName() + "\"))");
                }
            }
        }
        else if (this.table.getPrimaryKeySize() > 1) {
            final int n = this.pkColumns.size();
            for (final WackyColumn column : this.pkColumns) {
                if (++i < n) {
                    sb.append("\t\t@AttributeOverride(name = \"" + column.getName() + "\", column = @Column(name = \"" + column.getName() + "\")),\r\n");
                }
                else {
                    sb.append("\t\t@AttributeOverride(name = \"" + column.getName() + "\", column = @Column(name = \"" + column.getName() + "\"))");
                }
            }
        }
        return sb.toString();
    }
    
    public boolean isSupportI18n() {
        int i18n = 0;
        for (final WackyColumn wc : this.allColumns) {
            if (wc.getCapitalName().equals("Descript")) {
                ++i18n;
            }
            if (wc.getCapitalName().equals("Descript1")) {
                ++i18n;
            }
            if (wc.getCapitalName().equals("Descript2")) {
                ++i18n;
            }
            if (wc.getCapitalName().equals("Descript3")) {
                ++i18n;
            }
        }
        return i18n == 3;
    }
    
    public boolean hasDefault() {
        for (final WackyColumn wc : this.allColumns) {
            if (wc.hasDefault()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSupportSync() {
        final String des = this.table.getDescription();
        return !StringUtils.isBlank(des) && des.indexOf("@TableSync;") != -1;
    }
    
    public boolean isSupportEmergency() {
        final String des = this.table.getDescription();
        return !StringUtils.isBlank(des) && des.indexOf("@TableEmergency;") != -1;
    }
    
    public String getDescript() {
        String logicalName = this.table.getLogicalName();
        if (logicalName == null) {
            logicalName = "";
        }
        logicalName = logicalName.replace('\r', ' ');
        logicalName = logicalName.replace('\n', ' ');
        String description = this.table.getDescription();
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
    
    public String getPKDescript() {
        String des = this.table.getDescription();
        if (StringUtils.isNotBlank(des) && des.indexOf("@PKDES[") != -1) {
            des = des.substring(des.indexOf("@PKDES[") + "@PKDES[".length());
            des = des.substring(0, des.indexOf("]"));
            return "PKDescript(\"" + des + "\")";
        }
        return "";
    }
    
    /**
     * 获取当前表需要加密的字段源集
     * @return
     */
    public List<String> getSecurityOriginColumns(){
    	List<String> columns = null;
    	for (WackyColumn wackyColumn : allColumns) {
    		String originColumn = wackyColumn.getOriginColumn();
    		if (originColumn == null) {
    			continue;
    		}
    		if (columns == null) {
    			columns = new ArrayList();
    		}
    		if (columns.contains(originColumn)) {
    			continue;
    		}
    		columns.add(originColumn);
    	}
    	return columns;
    }
}
