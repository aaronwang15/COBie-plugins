package org.bimserver.cobie.plugins;
/******************************************************************************
 * Copyright (C) 2011  ERDC
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import nl.fountain.xelem.XFactory;
import nl.fountain.xelem.excel.Workbook;
import nl.fountain.xelem.lex.ExcelReader;

import org.bimserver.cobie.cobielite.COBIEDocument;
import org.bimserver.cobie.cobielite.COBIEType;
import org.bimserver.cobie.cobielite.FacilityType;
import org.bimserver.cobie.utils.deserializer.COBieIfcModel;
import org.bimserver.cobie.utils.deserializer.FromCOBieToIfc;
import org.bimserver.cobie.utils.spreadsheetml.SpreadSheetToComponents;
import org.bimserver.cobie.utils.spreadsheetml.SpreadSheetToSpares;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToAssemblies;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToAttributes;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToConnections;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToContacts;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToCoordinates;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToDocuments;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToFacilities;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToFloors;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToImpacts;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToIssues;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToJobs;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToResources;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToSpaces;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToSystems;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToTypes;
import org.bimserver.cobie.utils.spreadsheetml.SpreadsheetToZones;
import org.bimserver.ifc.IfcModel;
import org.bimserver.models.ifc2x3tc1.Ifc2x3tc1Factory;
import org.bimserver.models.ifc2x3tc1.IfcBuilding;
import org.bimserver.models.ifc2x3tc1.IfcGloballyUniqueId;
import org.bimserver.models.ifc2x3tc1.IfcRoot;
import org.bimserver.plugins.deserializers.DeserializeException;
import org.bimserver.plugins.deserializers.EmfDeserializer;
import org.bimserver.plugins.schema.SchemaDefinition;
import org.bimserver.plugins.serializers.EmfSerializer;
import org.bimserver.plugins.serializers.IfcModelInterface;
import org.bimserver.shared.cobie.COBieUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class COBieDeserializer extends EmfDeserializer 
{	
	private static final Logger LOGGER = LoggerFactory.getLogger(COBieDeserializer.class);
	private String templateFilePath;
	private ExcelReader reader = null;
	private Workbook xlWorkbook = null;
	private COBIEDocument COBie;
	private final File configurationFile;
	private final File preImportComplianceDirectory;
	private final COBieCheckSerializerPlugin checkSerializer;
	private static final String LOGGER_STATUS_SPREADSHEET_BEGIN_MSG = "Deserializing COBie SpreadsheetML to COBieLite XML.";
	private static final String LOGGER_STATUS_SPREADSHEET_END_MSG = "COBie SpreadsheetML converted to COBieLite XML.";
	public COBieDeserializer(File configurationFile,File preImportComplianceReportDirectory,COBieCheckSerializerPlugin checkSerializer) 
	{
		this.configurationFile = configurationFile;
		this.preImportComplianceDirectory = preImportComplianceReportDirectory;
		this.checkSerializer = checkSerializer;
	}
	
	
	// general initialize function that any of the constructors will call
	public void initCOBie(InputStream in, String filename2) throws IOException 
	{
		this.COBie = COBIEDocument.Factory.newInstance();
		templateFilePath = configurationFile.getAbsolutePath(); 
		
		try 
		{
			XFactory.setConfigurationFileName(templateFilePath);
			try 
			{
				reader = new ExcelReader();
				xlWorkbook = reader.getWorkbook(templateFilePath);
			} 
			catch (ParserConfigurationException e) 
			{
				LOGGER.error("", e);
			} catch (SAXException e) 
			{
				LOGGER.error("", e);
			}
			xlWorkbook = getWorkBookFromInputStream(in);		
			populateCobieDocument();
			try
			{
				//File complianceFile = new File(preImportComplianceDirectory,filename2.replace("xml", "html"));
				//FileOutputStream complianceFileOutput =
					//	new FileOutputStream (complianceFile);
				//handlePreImportComplianceReport(complianceFileOutput);
			}
			catch(Exception e)
			{
				
			}
		} 
		catch (IOException e) 
		{
			LOGGER.error("", e);
		}
	}
	
	private void handlePreImportComplianceReport(OutputStream out)
	{
		try
		{
			EmfSerializer serializerGeneric = checkSerializer.createSerializer();
			if(serializerGeneric instanceof COBieCheckSerializer)
			{
				LOGGER.info("Begin pre-import COBie Compliance report...");
				COBieCheckSerializer checkSerializer = (COBieCheckSerializer) serializerGeneric;
				checkSerializer.init(this.COBie);
				checkSerializer.writeModeFree(out);
				LOGGER.info("Done writing pre-import COBie Compliance report...");
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Error writing pre-import COBie Compliance report..."+e.getMessage());
		}
	}

	private Workbook getWorkBookFromInputStream(InputStream in)
	{
		Workbook cobieWorkBook = null;
		try 
		{
			InputSource inSrc = new InputSource(in);
			//inSrc.setEncoding("UTF-8");
			//inSrc.setSystemId("");
			nl.fountain.xelem.lex.ExcelReader rdr =
					new nl.fountain.xelem.lex.ExcelReader();
			cobieWorkBook = rdr.getWorkbook(inSrc);
		} 
		catch (ParserConfigurationException e) 
		{
			LOGGER.error("", e);
		} 
		catch (SAXException e) 
		{
			LOGGER.error("", e);
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return cobieWorkBook;
		
	}
	
	/* TODO: 1: Ignore first row in in sheet to avoid re-writing the column names
	 * 		 2: Check to make sure data is being written to the right column
	 * 		 3:	Parallelize this function
	 */
	private void populateCobieDocument() {
		LOGGER.info(LOGGER_STATUS_SPREADSHEET_BEGIN_MSG);
		 	SpreadsheetToContacts.writeContactsToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToFacilities.writeFacilitiesToCOBie(this.GetCobie(),this.xlWorkbook);
		 	SpreadsheetToFloors.writeFloorsToCOBie(this.GetCobie(),this.xlWorkbook);
		 	SpreadsheetToSpaces.writeSpacesToCOBie(this.GetCobie(),this.xlWorkbook);
		 	SpreadsheetToZones.writeZonesToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToTypes.writeTypesToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadSheetToComponents.writeComponentsToCOBie
		 		(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToSystems.writeSystemsToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToAssemblies.writeAssembliesToCOBie(this.GetCobie(),this.xlWorkbook);
		 	SpreadsheetToAttributes.writeAttributesToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToConnections.writeConnectionsToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToDocuments.writeDocumentsToCOBie(this.GetCobie(), this.xlWorkbook);
		 	SpreadsheetToCoordinates.writeCoordinatesToCOBie(this.GetCobie(), xlWorkbook);
		 	SpreadsheetToImpacts.writeImpactsToCOBie(GetCobie(), xlWorkbook);
		 	SpreadsheetToIssues.writeIssuesToCOBie(GetCobie(), xlWorkbook);
		 	SpreadsheetToJobs.writeJobsToCOBie(GetCobie(), xlWorkbook);
		 	SpreadsheetToResources.writeResourcesToCOBie(GetCobie(), xlWorkbook);
		 	SpreadSheetToSpares.writeSparesToCOBie(GetCobie(), xlWorkbook);
		 LOGGER.info(LOGGER_STATUS_SPREADSHEET_END_MSG);
	}
	
	public static void populateCobieDocument(COBIEType cobie,Workbook workbook) {
		LOGGER.info(LOGGER_STATUS_SPREADSHEET_BEGIN_MSG);
		 	SpreadsheetToContacts.writeContactsToCOBie(cobie, workbook);
		 	SpreadsheetToFacilities.writeFacilitiesToCOBie(cobie,workbook);
		 	SpreadsheetToFloors.writeFloorsToCOBie(cobie,workbook);
		 	SpreadsheetToSpaces.writeSpacesToCOBie(cobie,workbook);
		 	SpreadsheetToZones.writeZonesToCOBie(cobie, workbook);
		 	SpreadsheetToTypes.writeTypesToCOBie(cobie, workbook);
		 	SpreadSheetToComponents.writeComponentsToCOBie
		 		(cobie, workbook);
		 	SpreadsheetToSystems.writeSystemsToCOBie(cobie, workbook);
		 	SpreadsheetToAssemblies.writeAssembliesToCOBie(cobie,workbook);
		 	SpreadsheetToAttributes.writeAttributesToCOBie(cobie, workbook);
		 	SpreadsheetToConnections.writeConnectionsToCOBie(cobie, workbook);
		 	SpreadsheetToDocuments.writeDocumentsToCOBie(cobie, workbook);
		 	SpreadsheetToCoordinates.writeCoordinatesToCOBie(cobie, workbook);
		 	SpreadsheetToImpacts.writeImpactsToCOBie(cobie, workbook);
		 	SpreadsheetToIssues.writeIssuesToCOBie(cobie, workbook);
		 	SpreadsheetToJobs.writeJobsToCOBie(cobie, workbook);
		 	SpreadsheetToResources.writeResourcesToCOBie(cobie, workbook);
		 	SpreadSheetToSpares.writeSparesToCOBie(cobie, workbook);
		 LOGGER.info(LOGGER_STATUS_SPREADSHEET_END_MSG);
	}
	
	

	private COBIEType GetCobie()
	{
		COBIEType cType;
		if (this.COBie.getCOBIE()==null)
			cType = this.COBie.addNewCOBIE();
		else
			cType = this.COBie.getCOBIE();
		return cType;
	}

	@Override
	public void init(SchemaDefinition schema) {
	
	}


	@Override
	public IfcModelInterface read(File file, boolean setOids)
			throws DeserializeException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IfcModelInterface read(InputStream in, String filename,
			boolean setOids, long fileSize) throws DeserializeException {
		try
		{
			initCOBie(in,filename);
		} 
		catch (IOException e) 
		{
			LOGGER.error("", e);
		} 
	
		FromCOBieToIfc cobieTransform =
				new FromCOBieToIfc(this.GetCobie());
		COBieIfcModel cobieModel = cobieTransform.getModelFromCOBie();
		return cobieModel;

	}
	
	private IfcModelInterface returnTestModel()
	{
		IfcModel model = new IfcModel();
		IfcBuilding bldg = Ifc2x3tc1Factory.eINSTANCE.createIfcBuilding();
		IfcGloballyUniqueId guid = Ifc2x3tc1Factory.eINSTANCE.createIfcGloballyUniqueId();
		guid.setWrappedValue("3eM8WbY_59RR5TDWry5aRT");
		//model.add(guid);
		bldg.setName("A Building");
		bldg.setGlobalId(guid);
		model.add(bldg);
		IfcRoot rootObj = model.get("3eM8WbY_59RR5TDWry5aRT");
		return model;
	}
	
	
}