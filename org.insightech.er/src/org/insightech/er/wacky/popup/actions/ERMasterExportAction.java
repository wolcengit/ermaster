package org.insightech.er.wacky.popup.actions;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.dbexport.ddl.validator.ValidateResult;
import org.insightech.er.editor.model.dbexport.ddl.validator.Validator;
import org.insightech.er.editor.persistent.Persistent;
import org.insightech.er.editor.view.dialog.dbexport.ExportWarningDialog;

public abstract class ERMasterExportAction implements IObjectActionDelegate {
	protected Shell shell;
	protected ISelection selection;
	protected IFile file;
	protected ERDiagram diagram;

	public void run(IAction action) {
		Persistent persistent = Persistent.getInstance();
		InputStream in = null;
		try {
			file = (IFile) ((IStructuredSelection) this.selection).getFirstElement();
			in = file.getContents();
			this.diagram = persistent.load(in);
			in.close();

			Validator validator = new Validator();
			List<ValidateResult> errorList = validator.validate(this.diagram);
			if (!errorList.isEmpty()) {
				ExportWarningDialog dialog = new ExportWarningDialog(this.shell, errorList);

				if (dialog.open() != 0) {
					return;
				}
			}

			process();
			
			refreshProject();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
	}


	public void refreshProject() {
		IProject project = file.getProject();
		if (project != null) {
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	public abstract void process() throws Exception;

}
