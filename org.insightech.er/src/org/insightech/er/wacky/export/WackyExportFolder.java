package org.insightech.er.wacky.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class WackyExportFolder {
	private IFolder diagramFolder;
	public String templatePath;
	public String ejbPath;
	public String ejbPackage;
	public boolean base;
	public boolean embeddedId;
	public String idsuffix;
	public boolean camel;
	public boolean binding;
	public boolean entity;

	public WackyExportFolder(final IFolder diagramFolder) {
		this.diagramFolder = null;
		this.templatePath = null;
		this.ejbPath = null;
		this.ejbPackage = null;
		this.base = false;
		this.embeddedId = false;
		this.idsuffix = "_PK";
		this.camel = false;
		this.binding = true;
		this.entity = true;
		this.diagramFolder = diagramFolder;
	}

	public void exportAll() throws Exception {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		dialog.run(true, true, (IRunnableWithProgress) new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					WackyExportFolder.this.exportAllprocess(monitor, WackyExportFolder.this.diagramFolder);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void exportAllprocess(final IProgressMonitor monitor, final IFolder diagramFolder) throws Exception {
		IResource[] members;
		for (int length = (members = diagramFolder.members()).length, i = 0; i < length; ++i) {
			final IResource r = members[i];
			if (r instanceof IFile) {
				final IFile file = (IFile) r;
				if ("erm".equalsIgnoreCase(file.getFileExtension())) {
					this.exportInternal(monitor, file);
				}
			} else if (r instanceof IFolder) {
				this.exportAllprocess(monitor, (IFolder) r);
			}
		}
	}

	public void exportInternal(final IProgressMonitor monitor, final IFile file) {
		try {
			final WackyExport util = new WackyExport(file);
			util.templatePath = null;
			util.ejbPath = this.ejbPath;
			util.ejbPackage = this.ejbPackage;
			util.idsuffix = this.idsuffix;
			util.embeddedId = this.embeddedId;
			util.entity = this.entity;
			util.camel = this.camel;
			util.binding = this.binding;
			util.base = this.base;
			util.exportEjbInternal(monitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
