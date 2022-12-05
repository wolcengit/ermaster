package org.insightech.er.wacky.export;

import org.insightech.er.ERDiagramActivator;
import org.insightech.er.wacky.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

class ExportEJBWizard extends Wizard {
	private IFile file;
	private ExportEJBWizardPage page;

	public ExportEJBWizard(final IFile file) {
		this.file = null;
		this.file = file;
		this.setWindowTitle("EJB v20221205");
		final IDialogSettings settings = ERDiagramActivator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection("EJBWizard");
        if (section == null) {
            section = settings.addNewSection("EJBWizard");
            section.put("srcpath", "/FoxhisEntity/src/main/java");
            section.put("packagename", "com.foxhis.entity");
            section.put("suffix", "_PK");

            section.put("embeddedid", false);
            section.put("camel", false);
            
            section.put("entity", true);
            section.put("binding", true);
            section.put("base", false);
        }
        if (section.get("srcpath") == null) {
        	section.put("srcpath", "/FoxhisEntity/src/main/java");
        }
        if (section.get("packagename") == null) {
        	 section.put("packagename", "com.foxhis.entity");
        }
        if (section.get("suffix") == null) {
        	section.put("suffix", "_PK");
        }
        
        if (section.get("embeddedid") == null) {
            section.put("embeddedid", false);
        }
        if (section.get("camel") == null) {
            section.put("camel", false);
        }

        if (section.get("entity") == null) {
            section.put("entity", true);
        }
        if (section.get("binding") == null) {
            section.put("binding", true);
        }
        if (section.get("base") == null) {
            section.put("base", false);
        }
        
		this.setDialogSettings(section);
	}

	public void addPages() {
		this.addPage((IWizardPage) (this.page = new ExportEJBWizardPage("EJB")));
	}

	public boolean performFinish() {
		try {
			this.page.doValidate();
			final IDialogSettings section = this.getDialogSettings();
			final IContainer path = (IContainer) this.page.getOutputFolderResource();
			final WackyExport util = new WackyExport(this.file);
			util.templatePath = null;
			util.ejbPath = path.getLocation().toString();
			util.ejbPackage = section.get("packagename");
			util.idsuffix = section.get("suffix");
			
			util.camel = section.getBoolean("camel");
			util.embeddedId = section.getBoolean("embeddedid");
			util.base = section.getBoolean("base");
			util.entity = section.getBoolean("entity");
			util.binding = section.getBoolean("binding");
			util.base = section.getBoolean("base");
			util.foxsecProcesser= section.getBoolean("foxsec_processer");
			final IContainer foxsecPath = (IContainer) this.page.getFoxsecOutputFolderResource();
			util.foxsecProcesserPath = foxsecPath == null ? null : (foxsecPath.getLocation() == null ? null : foxsecPath.getLocation().toString());
			if (!util.idsuffix.startsWith("@")) {
				util.exportEjb();
			} else {
				IFolder diagramFolder = null;
				if (util.idsuffix.startsWith("@@")) {
					util.idsuffix = util.idsuffix.substring(2);
					diagramFolder = (IFolder) this.file.getParent().getParent();
				} else if (util.idsuffix.startsWith("@")) {
					util.idsuffix = util.idsuffix.substring(1);
					diagramFolder = (IFolder) this.file.getParent();
				}
				section.put("suffix", util.idsuffix);
				final WackyExportFolder util2 = new WackyExportFolder(diagramFolder);
				util2.templatePath = null;
				util2.ejbPath = path.getLocation().toString();
				util2.ejbPackage = section.get("packagename");
				util2.idsuffix = section.get("suffix");
				
				util2.camel = section.getBoolean("camel");
				util2.embeddedId = section.getBoolean("embeddedid");
				
				util2.binding = section.getBoolean("binding");
				util2.base = section.getBoolean("base");
				util2.foxsecProcesser= section.getBoolean("foxsec_processer");
				util2.foxsecProcesserPath = foxsecPath == null ? null : (foxsecPath.getLocation() == null ? null : foxsecPath.getLocation().toString());
				
				util2.exportAll();
			}
			path.refreshLocal(0, (IProgressMonitor) null);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
