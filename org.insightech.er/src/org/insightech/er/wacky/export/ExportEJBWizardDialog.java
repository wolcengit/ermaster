package org.insightech.er.wacky.export;

import org.eclipse.swt.widgets.*;
import org.eclipse.core.resources.*;
import org.eclipse.jface.wizard.*;

public class ExportEJBWizardDialog extends WizardDialog
{
    public ExportEJBWizardDialog(final Shell parentShell, final IFile file) {
        super(parentShell, (IWizard)new ExportEJBWizard(file));
    }
}
