package org.insightech.er.wacky.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableSet;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.wacky.erutil.WackyERDiagram;
import org.insightech.er.wacky.erutil.WackyTable;

public class WackyExport {
	public String templatePath;
	public String ejbPath;
	public String ejbPackage;
	public boolean embeddedId;
	public boolean entity;
	public String idsuffix;
	public boolean camel;
	public boolean binding;
	public boolean base;
	private WackyERDiagram diagram;
	private TableSet tables;
	private VelocityContext context;

	public WackyExport(final IFile file) {
		this.templatePath = null;
		this.ejbPath = null;
		this.ejbPackage = null;
		this.embeddedId = true;
		this.entity = true;
		this.idsuffix = "Id";
		this.camel = true;
		this.binding = true;
		this.diagram = null;
		this.tables = null;
		this.context = null;
		this.diagram = new WackyERDiagram(file);
		this.tables = this.diagram.getDiagram().getDiagramContents().getContents().getTableSet();
	}

	public void initVelocity() {
		Velocity.setProperty("resource.loader", (Object) "class");
		Velocity.setProperty("class.resource.loader.class",
				(Object) "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Velocity.init();
		this.context = new VelocityContext();
	}

	public void createFile(final String vm, final String filename) throws Exception {
		this.log(1, "Creating " + filename);
		final StringWriter w = new StringWriter();
		Velocity.mergeTemplate(vm, "UTF-8", (Context) this.context, (Writer) w);
		try {
			final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "UTF-8");
			out.write(w.toString());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void exportEjb() throws Exception {
		final String msg = "packagename=" + this.ejbPackage + ";suffix=" + this.idsuffix + ";embeddedid=" + this.embeddedId + ";camel=" + this.camel + ";binding=" + this.binding + ";";
		this.log(2, msg);
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		dialog.run(true, true, (IRunnableWithProgress) new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				WackyExport.this.exportEjbInternal(monitor);
			}
		});
	}

	public void exportEjbInternal() {
		this.exportEjbInternal(null);
	}

	public void exportEjbInternal(final IProgressMonitor monitor) {
		final int ticks = this.tables.getList().size() + 3;
		int index = 1;
		if (monitor != null) {
			monitor.beginTask("Begin Export", ticks);
		}
		try {
			if (monitor != null && monitor.isCanceled()) {
				throw new InterruptedException();
			}
			String outputPath = this.ejbPath.replace('\\', '/');
			if (!outputPath.endsWith("/")) {
				outputPath = String.valueOf(outputPath) + "/" + this.ejbPackage.replace('.', '/');
			} else {
				outputPath = String.valueOf(outputPath) + this.ejbPackage.replace('.', '/');
			}
			final String bindingPath = "/binding";
			File file = new File(outputPath);
			file.mkdirs();
			if (this.binding || this.base) {
				File fileBinding = new File(String.valueOf(outputPath) + bindingPath);
				fileBinding.mkdirs();
			}
			if (monitor != null) {
				monitor.worked(index++);
			}
			final WackyExportUtil util = new WackyExportUtil(this.camel);
			this.initVelocity();
			final String vmPath = "/org/insightech/er/wacky/export/vm/ejb/";
			this.context.put("Util", (Object) util);
			this.context.put("ejbPackage", (Object) this.ejbPackage);
			this.context.put("EmbeddedId", (Object) this.embeddedId);
			this.context.put("ColumnDefult", (Object) false);
			this.context.put("IdSuffix", (Object) this.idsuffix);
			this.context.put("IdSuffixName", (Object) util.getName(this.idsuffix));
			this.context.put("IdSuffixVar", (Object) util.getLowerName(this.idsuffix));

			String filetemplate, filename;
			if (this.base) {
				filetemplate = String.valueOf(vmPath) + "ColumnDefault.vm";
				filename = String.valueOf(outputPath) + "/ColumnDefault.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "ColumnDescript.vm";
				filename = String.valueOf(outputPath) + "/ColumnDescript.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "TableDescript.vm";
				filename = String.valueOf(outputPath) + "/TableDescript.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "PKDescript.vm";
				filename = String.valueOf(outputPath) + "/PKDescript.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "TableSynch.vm";
				filename = String.valueOf(outputPath) + "/TableSynch.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "TableEmergency.vm";
				filename = String.valueOf(outputPath) + "/TableEmergency.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "IFoxBindingProxy.vm";
				filename = String.valueOf(outputPath) + bindingPath + "/IFoxBindingProxy.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "FoxBindingProxy.vm";
				filename = String.valueOf(outputPath) + bindingPath + "/FoxBindingProxy.java";
				this.createFile(filetemplate, filename);
				filetemplate = String.valueOf(vmPath) + "EntityBindingProxy.vm";
				filename = String.valueOf(outputPath) + bindingPath + "/EntityBindingProxy.java";
				this.createFile(filetemplate, filename);
				if (monitor != null) {
					monitor.worked(index++);
				}
			}
			final Map<String, ERTable> tbls = new HashMap<String, ERTable>();
			final List<String> ptbls = new ArrayList<String>();
			final Map<String, String> ctbls = new HashMap<String, String>();
			if (!this.embeddedId) {
				for (final ERTable table : this.tables) {
					tbls.put(table.getPhysicalName(), table);
					String des = table.getDescription();
					if (StringUtils.isNotBlank(des) && des.indexOf("@ABS[") != -1) {
						des = des.substring(des.indexOf("@ABS[") + "@ABS[".length());
						des = des.substring(0, des.indexOf("]"));
						final String[] ts = des.split(",");
						final WackyTable wt = new WackyTable(table, this.diagram.getDiagram(), util);
						final String tableEntity = util.getName(wt.getName());
						final String absEntry = "Abstract" + tableEntity;
						int n = 0;
						String[] array;
						for (int length = (array = ts).length, i = 0; i < length; ++i) {
							final String t = array[i];
							if (StringUtils.isNotBlank(t)) {
								++n;
								ctbls.put(t, absEntry);
							}
						}
						if (n <= 0) {
							continue;
						}
						ptbls.add(table.getPhysicalName());
					}
				}
				for (final String tbl : tbls.keySet()) {
					if (ptbls.contains(tbl)) {
						continue;
					}
					if (ctbls.containsKey(tbl)) {
						continue;
					}
					final String htbl = "h" + tbl;
					if (!tbls.containsKey(htbl)) {
						continue;
					}
					final ERTable t2 = tbls.get(tbl);
					final ERTable t3 = tbls.get(htbl);
					final List<NormalColumn> cs1 = (List<NormalColumn>) t2.getExpandedColumns();
					final List<NormalColumn> cs2 = (List<NormalColumn>) t3.getExpandedColumns();
					if (cs1.size() != cs2.size()) {
						continue;
					}
					boolean same = true;
					for (final NormalColumn c1 : cs1) {
						boolean found = false;
						for (final NormalColumn c2 : cs2) {
							if (c2.getPhysicalName().equals(c1.getPhysicalName())) {
								found = true;
								break;
							}
						}
						if (!found) {
							same = false;
							break;
						}
					}
					if (!same) {
						continue;
					}
					ptbls.add(tbl);
					final WackyTable wt2 = new WackyTable(t2, this.diagram.getDiagram(), util);
					final String tableEntity2 = util.getName(wt2.getName());
					final String absEntry2 = "Abstract" + tableEntity2;
					ctbls.put(htbl, absEntry2);
				}
			}
			for (final ERTable table : this.tables) {
				if (monitor != null) {
					monitor.worked(index++);
				}
				final WackyTable wt3 = new WackyTable(table, this.diagram.getDiagram(), util);
				final String tableEntity3 = util.getName(wt3.getName());
				this.context.put("table", (Object) wt3);
				this.context.put("tableNotPkColumns", (Object) wt3.getNotPkColumns());
				this.context.put("tablePkColumns", (Object) wt3.getPkColumns());
				this.context.put("tableAllColumns", (Object) wt3.getAllColumns());
				this.context.put("tableFindColumns", (Object) wt3.getFindColumns());
				this.context.put("tableNotPkPackages", (Object) wt3.getNotPkPageages());
				this.context.put("tablePkPackages", (Object) wt3.getPkPageages());
				this.context.put("tableAllPackages", (Object) wt3.getAllPageages());
				this.context.put("tablePk", (Object) wt3.getPkColumn());
				this.context.put("tableEntity", (Object) tableEntity3);
				this.context.put("tableDescript", (Object) wt3.getDescript());
				this.context.put("tablePKDescript", (Object) wt3.getPKDescript());
				int vmtype = 0;
				final String wtName = wt3.getName();
				String tableAbstractEntity = "Abstract" + util.getName(wtName);
				if (!this.embeddedId) {
					if (ptbls.contains(wtName)) {
						vmtype = 1;
					}
					if (ctbls.containsKey(wtName)) {
						vmtype = 2;
						tableAbstractEntity = ctbls.get(wtName);
					}
					this.context.put("tableAbstractEntity", (Object) tableAbstractEntity);
				}
				if (this.entity) {
					if (monitor != null) {
						monitor.worked(index++);
					}
					if (wt3.getPkSize() == 1) {
						if (vmtype == 0) {
							filetemplate = String.valueOf(vmPath) + "Entity1.vm";
							filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
							this.createFile(filetemplate, filename);
						} else if (vmtype == 1) {
							filetemplate = String.valueOf(vmPath) + "Entity1p.vm";
							filename = String.valueOf(outputPath) + "/Abstract" + tableEntity3 + ".java";
							this.createFile(filetemplate, filename);
							filetemplate = String.valueOf(vmPath) + "Entity1c.vm";
							filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
							this.createFile(filetemplate, filename);
						} else if (vmtype == 2) {
							filetemplate = String.valueOf(vmPath) + "Entity1c.vm";
							filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
							this.createFile(filetemplate, filename);
						}
					} else if (vmtype == 0) {
						filetemplate = String.valueOf(vmPath) + "EntityId.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + this.idsuffix + ".java";
						this.createFile(filetemplate, filename);
						filetemplate = String.valueOf(vmPath) + "EntityN.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
						this.createFile(filetemplate, filename);
					} else if (vmtype == 1) {
						filetemplate = String.valueOf(vmPath) + "EntityIdp.vm";
						filename = String.valueOf(outputPath) + "/Abstract" + tableEntity3 + this.idsuffix + ".java";
						this.createFile(filetemplate, filename);
						filetemplate = String.valueOf(vmPath) + "EntityIdc.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + this.idsuffix + ".java";
						this.createFile(filetemplate, filename);
						filetemplate = String.valueOf(vmPath) + "EntityNp.vm";
						filename = String.valueOf(outputPath) + "/Abstract" + tableEntity3 + ".java";
						this.createFile(filetemplate, filename);
						filetemplate = String.valueOf(vmPath) + "EntityNc.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
						this.createFile(filetemplate, filename);
					} else if (vmtype == 2) {
						filetemplate = String.valueOf(vmPath) + "EntityIdc.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + this.idsuffix + ".java";
						this.createFile(filetemplate, filename);
						filetemplate = String.valueOf(vmPath) + "EntityNc.vm";
						filename = String.valueOf(outputPath) + "/" + tableEntity3 + ".java";
						this.createFile(filetemplate, filename);
					}
				}
				if (this.binding) {
					if (monitor != null) {
						monitor.worked(index++);
					}
					filetemplate = String.valueOf(vmPath) + "EntityBindingProxyTemplate.vm";
					filename = String.valueOf(outputPath) + bindingPath + "/" + tableEntity3 + "BindingProxy.java";
					this.createFile(filetemplate, filename);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
		if (monitor != null) {
			monitor.done();
		}
	}

	public void log(final int level, final String text) {
		final ILog log = ERDiagramActivator.getDefault().getLog();
		log.log((IStatus) new Status(level, "org.insightech.er.wacky", text));
	}
}
