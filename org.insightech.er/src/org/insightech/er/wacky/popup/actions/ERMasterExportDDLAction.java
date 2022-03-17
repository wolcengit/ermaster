package org.insightech.er.wacky.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.dbexport.ddl.DDLCreator;
import org.insightech.er.editor.model.dbexport.ddl.DDLTarget;
import org.insightech.er.editor.model.settings.Environment;
import org.insightech.er.editor.model.settings.ExportSetting;
import org.insightech.er.editor.model.settings.export.ExportDDLSetting;
import org.insightech.er.util.io.FileUtils;

public class ERMasterExportDDLAction extends ERMasterExportAction {

	@Override
	public void process() throws Exception {
		File filePath = file.getLocation().toFile();
		String fileSqlName = filePath.getName();
		fileSqlName = String.valueOf(fileSqlName.substring(0, fileSqlName.length() - 3)) + "sql";

		ExportSetting exportSetting = diagram.getDiagramContents().getSettings().getExportSetting();
		Environment environment = exportSetting.getExportDDLSetting().getEnvironment();
		DDLTarget ddlTarget = exportSetting.getExportDDLSetting().getDdlTarget();

		ExportDDLSetting exportDDLSetting = new ExportDDLSetting();
		exportDDLSetting.setDdlOutput(fileSqlName);
		exportDDLSetting.setDdlTarget(ddlTarget);
		exportDDLSetting.setEnvironment(environment);
		exportDDLSetting.setSrcFileEncoding("utf-8");

		DDLCreator ddlCreator = DBManagerFactory.getDBManager(diagram).getDDLCreator(diagram,exportDDLSetting.getCategory(), true);

		ddlCreator.init(exportDDLSetting.getEnvironment(), exportDDLSetting.getDdlTarget(),exportDDLSetting.getLineFeed());

		File fileSql = FileUtils.getFile(filePath.getParentFile(), exportDDLSetting.getDdlOutput());
		fileSql.getParentFile().mkdirs();

		PrintWriter out = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(fileSql), exportDDLSetting.getSrcFileEncoding())));

		out.print(ddlCreator.getDropDDL(diagram));

		out.print(ddlCreator.getCreateDDL(diagram));

		out.close();

	}

}
