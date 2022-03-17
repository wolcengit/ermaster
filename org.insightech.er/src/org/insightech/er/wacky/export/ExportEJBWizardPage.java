package org.insightech.er.wacky.export;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ExportEJBWizardPage extends WizardPage {
	private Text txtOutputFolder;
	private Button btnOutputFolder;
	private Text txtPackageName;
	private Button btnPackageName;
	private Text txtIdSuffix;
	private Button rbIdClass;
	private Button rbIdEmbedded;
	private Button rbCamelName;
	private Button rbCapitalName;
	private Button cbxEntityBindingProxy;
	private Button cbxBaseClass;
	private Button cbxEntityClass;

	protected ExportEJBWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Generate EJB Entity&Facade for all tables.");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(3, false));

		Label lbl1 = new Label(container, SWT.NONE);
		lbl1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl1.setText("Output Folder");

		txtOutputFolder = new Text(container, SWT.BORDER);
		txtOutputFolder.setText("/FoxhisEntity/src/main/java");
		txtOutputFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnOutputFolder = new Button(container, SWT.NONE);
		btnOutputFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				do_btnOutputFolder_widgetSelected(e);
			}
		});
		btnOutputFolder.setText("   ...   ");

		Label lbl2 = new Label(container, SWT.NONE);
		lbl2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl2.setText("Package Name");

		txtPackageName = new Text(container, SWT.BORDER);
		txtPackageName.setText("com.foxhis.entity");
		txtPackageName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnPackageName = new Button(container, SWT.NONE);
		btnPackageName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				do_btnPackageName_widgetSelected(e);
			}
		});
		btnPackageName.setText("   ...   ");

		Label lbl3 = new Label(container, SWT.NONE);
		lbl3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl3.setText("Entity Id Suffix");

		txtIdSuffix = new Text(container, SWT.BORDER);
		txtIdSuffix.setText("_PK");
		txtIdSuffix.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);

		Composite composite_0 = new Composite(container, SWT.NONE);
		composite_0.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_0.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

		Group grpEntityIdMode = new Group(composite_0, SWT.NONE);
		grpEntityIdMode.setText("Entity Id Mode");
		grpEntityIdMode.setLayout(new FillLayout(SWT.HORIZONTAL));

		rbIdClass = new Button(grpEntityIdMode, SWT.RADIO);
		rbIdClass.setSelection(true);
		rbIdClass.setText("Id Class");

		rbIdEmbedded = new Button(grpEntityIdMode, SWT.RADIO);
		rbIdEmbedded.setText("Id Embedded");

		Composite composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

		Group grpEntityFieldMode = new Group(composite_1, SWT.NONE);
		grpEntityFieldMode.setText("Entity Field Mode");
		grpEntityFieldMode.setLayout(new FillLayout(SWT.HORIZONTAL));

		rbCamelName = new Button(grpEntityFieldMode, SWT.RADIO);
		rbCamelName.setText("Camel Name");

		rbCapitalName = new Button(grpEntityFieldMode, SWT.RADIO);
		rbCapitalName.setSelection(true);
		rbCapitalName.setText("Capital Name");

		cbxEntityClass = new Button(container, SWT.CHECK);
		cbxEntityClass.setSelection(true);
		cbxEntityClass.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		cbxEntityClass.setText("Create Entity Class");

		cbxEntityBindingProxy = new Button(container, SWT.CHECK);
		cbxEntityBindingProxy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		cbxEntityBindingProxy.setSelection(true);
		cbxEntityBindingProxy.setText("Create Entity Binding Proxy");

		cbxBaseClass = new Button(container, SWT.CHECK);
		cbxBaseClass.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		cbxBaseClass.setText("Create Base Class");

		final IDialogSettings section = this.getDialogSettings();
		
		this.txtOutputFolder.setText(section.get("srcpath"));
		this.txtPackageName.setText(section.get("packagename"));
		this.txtIdSuffix.setText(section.get("suffix"));
		
		this.rbIdEmbedded.setSelection(section.getBoolean("embeddedid"));
		this.rbCamelName.setSelection(section.getBoolean("camel"));
		
		this.cbxBaseClass.setSelection(section.getBoolean("base"));
		this.cbxEntityBindingProxy.setSelection(section.getBoolean("entity"));
		this.cbxEntityClass.setSelection(section.getBoolean("binding"));
	}

	protected void do_btnOutputFolder_widgetSelected(SelectionEvent e) {
		selectFolderOrPacakge(0);
	}

	protected void do_btnPackageName_widgetSelected(SelectionEvent e) {
		selectFolderOrPacakge(1);
	}

	public IResource getOutputFolderResource() {
		final String outputDir = this.txtOutputFolder.getText();
		final IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		return wsroot.findMember(outputDir);
	}

	private String getFolderName(final Object result) throws CoreException {
		if (result instanceof IContainer) {
			final IContainer folder = (IContainer) result;
			return folder.getFullPath().toString();
		}
		return "";
	}

	protected void selectFolderOrPacakge(final int selectMode) {
		try {
			IResource init = null;
			if (!this.txtOutputFolder.getText().equals("")) {
				init = this.getOutputFolderResource();
			}
			final Class[] acceptedClasses = { IProject.class, IFolder.class };
			final ISelectionStatusValidator validator = (ISelectionStatusValidator) new TypedElementSelectionValidator(
					acceptedClasses, false);
			final IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			final FolderSelectionDialog dialog = new FolderSelectionDialog(this.getShell(),
					(ILabelProvider) new WorkbenchLabelProvider(),
					(ITreeContentProvider) new WorkbenchContentProvider());
			final ViewerFilter filter = new ViewerFilter() {
				public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
					if (selectMode == 0) {
						return element instanceof IContainer;
					}
					final String outputDir = txtOutputFolder.getText().trim();
					if (outputDir.isEmpty()) {
						return element instanceof IContainer;
					}
					if (element instanceof IContainer) {
						final IContainer e = (IContainer) element;
						final String ep = e.getFullPath().toString();
						if (ep.startsWith(outputDir) || outputDir.startsWith(ep)) {
							return true;
						}
					}
					return false;
				}
			};
			if (selectMode == 0) {
				dialog.setTitle("Select Output Folder");
				dialog.setMessage("Select Output Folder:");
				dialog.addFilter(filter);
				dialog.setInput((Object) wsroot);
				dialog.setValidator(validator);
				dialog.setInitialSelection((Object) init);
				if (dialog.open() == 0) {
					txtOutputFolder.setText(this.getFolderName(dialog.getFirstResult()));
				}
			} else {
				dialog.setTitle("Select Package Name");
				dialog.setMessage("Select Package Name:");
				dialog.addFilter(filter);
				dialog.setInput((Object) wsroot);
				dialog.setValidator(validator);
				dialog.setInitialSelection((Object) init);
				if (dialog.open() == 0) {
					final String outputDir = txtOutputFolder.getText().trim();
					String pn = this.getFolderName(dialog.getFirstResult());
					if (pn.length() <= outputDir.length()) {
						pn = "";
					} else {
						pn = pn.substring(outputDir.length() + 1);
						pn = pn.replace("/", ".");
					}
					this.txtPackageName.setText(pn);
				}
			}
			doValidate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void doValidate() {
		if (this.txtOutputFolder.getText().trim().length() == 0) {
			this.setErrorMessage("Output Folder is required.");
			this.setPageComplete(false);
			return;
		}
		if (this.txtPackageName.getText().trim().length() == 0) {
			this.setErrorMessage("Package name is required.");
			this.setPageComplete(false);
			return;
		}
		if (this.txtIdSuffix.getText().trim().length() == 0) {
			this.setErrorMessage("Entity Id Suffix is required.");
			this.setPageComplete(false);
			return;
		}
		final IDialogSettings section = this.getDialogSettings();
		section.put("srcpath", this.txtOutputFolder.getText().trim());
		section.put("packagename", this.txtPackageName.getText().trim());
		section.put("suffix", this.txtIdSuffix.getText().trim());

		section.put("camel", this.rbCamelName.getSelection());
		section.put("embeddedid", this.rbIdEmbedded.getSelection());

		section.put("base", this.cbxBaseClass.getSelection());
		section.put("entity", this.cbxEntityClass.getSelection());
		section.put("binding", this.cbxEntityBindingProxy.getSelection());

		this.setErrorMessage((String) null);
		this.setPageComplete(true);
	}

}
