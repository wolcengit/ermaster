package org.insightech.er.wacky.erutil;

import org.eclipse.core.resources.*;
import org.insightech.er.editor.model.*;
import org.insightech.er.editor.persistent.*;
import java.io.*;

public class WackyERDiagram
{
    private IFile file;
    private ERDiagram diagram;
    
    public WackyERDiagram(final IFile file) {
        this.diagram = null;
        this.file = file;
        this.loadDiagram();
    }
    
    public IFile getFile() {
        return this.file;
    }
    
    private void loadDiagram() {
        final Persistent persistent = Persistent.getInstance();
        InputStream in = null;
        try {
            in = this.file.getContents();
            this.diagram = persistent.load(in);
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ERDiagram getDiagram() {
        return this.diagram;
    }
}
