package org.insightech.er.wacky.export;

import org.insightech.er.db.sqltype.*;
import org.insightech.er.wacky.erutil.*;
import java.util.*;

public class WackyExportUtil
{
    public boolean camel;
    
    public WackyExportUtil(final boolean camel) {
         this.camel = camel;
    }
    
    public String getName(final String name) {
        if (this.camel) {
            return this.getCamelName(name);
        }
        return this.getCapitalName(name);
    }
    
    public String getLowerName(final String name) {
        return name.toLowerCase();
    }
    
    public String getCamelName(final String name) {
        if (name == null) {
            return null;
        }
        String result = name.toLowerCase();
        final String[] ary = result.split("_");
        result = "";
        String[] array;
        for (int length = (array = ary).length, i = 0; i < length; ++i) {
            final String s = array[i];
            if (s.length() >= 0) {
                result = String.valueOf(result) + this.getCapitalName(s);
            }
        }
        return result;
    }
    
    public String getCapitalName(final String name) {
        String result = name;
        if (result.length() > 0) {
            final String first = result.substring(0, 1);
            final String other = result.substring(1);
            result = String.valueOf(first.toUpperCase()) + other;
        }
        return result;
    }
    
    public String getJavaClass(final SqlType type) {
        if (type == null) {
            return "";
        }
        final Class clazz = type.getJavaClass();
        String name = clazz.getSimpleName();
        if (name.endsWith("Blob")) {
            name = "byte[]";
        }
        return name;
    }
    
    public void getImportPackges(final SqlType type, final List<String> importPackges) {
        final Class clazz = type.getJavaClass();
        final String name = clazz.getCanonicalName();
        if (name.equals("java.sql.Blob")) {
            return;
        }
        if (!name.startsWith("java.lang") && !importPackges.contains(name)) {
            importPackges.add(name);
        }
    }
    
    public String getParameters(final List<WackyColumn> columns, final int mode) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        final int n = columns.size();
        for (final WackyColumn column : columns) {
            if (++i < n) {
                sb.append(String.valueOf(column.getJavaClass()) + " " + column.getName() + ",");
            }
            else {
                sb.append(String.valueOf(column.getJavaClass()) + " " + column.getName());
            }
            if (i % 2 == mode && i < n) {
                sb.append("\r\n\t\t\t");
            }
        }
        return sb.toString();
    }
    
    public String getCallParameters(final List<WackyColumn> columns, final int mode) {
        final StringBuilder sb = new StringBuilder();
        int i = 0;
        final int n = columns.size();
        for (final WackyColumn column : columns) {
            if (++i < n) {
                sb.append(String.valueOf(column.getName()) + ",");
            }
            else {
                sb.append(column.getName());
            }
            if (i % 2 == mode && i < n) {
                sb.append("\r\n\t\t\t");
            }
        }
        return sb.toString();
    }
}
