package org.insightech.er.wacky.popup.actions;

import org.insightech.er.wacky.export.ExportEJBWizardDialog;

public class ERMasterExportEJBAction extends ERMasterExportAction {

	@Override
	public void process() throws Exception {
	       final ExportEJBWizardDialog dialog2 = new ExportEJBWizardDialog(this.shell, this.file);
	       dialog2.open();
	}

}
