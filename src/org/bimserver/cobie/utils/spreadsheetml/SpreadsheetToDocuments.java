package org.bimserver.cobie.utils.spreadsheetml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import nl.fountain.xelem.excel.Row;
import nl.fountain.xelem.excel.Workbook;
import nl.fountain.xelem.excel.Worksheet;

import org.bimserver.cobie.cobielite.COBIEType;
import org.bimserver.cobie.cobielite.DocumentType;
import org.bimserver.cobie.utils.COBieUtility;


public class SpreadsheetToDocuments
{
	public static ArrayList<String> DocumentColumnNames = new ArrayList<String>(
			Arrays.asList("Name", "CreatedBy", "CreatedOn", "Category",
					"ApprovalBy", "Stage", "SheetName", "RowName", "Directory",
					"File", "ExtSystem", "ExtObject", "ExtIdentifier",
					"Description", "Reference"));

	public static ArrayList<String> getDocumentColumns()
	{
		return DocumentColumnNames;
	}

	public static enum DocumentColumnNameLiterals
	{
		Name, CreatedBy, CreatedOn, Category, ApprovalBy, Stage, SheetName, RowName, Directory, File, ExtSystem, ExtObject, ExtIdentifier, Description, Reference
	};

	public static void writeDocumentsToCOBie(COBIEType cType, Workbook workbook)
	{
		COBIEType.Documents documents = cType.addNewDocuments();
		Worksheet sheet = workbook
				.getWorksheet(COBieUtility.CobieSheetName.Document.toString());
		Map<String, Integer> columnDictionary = COBieUtility
				.GetWorksheetColumnDictionary(sheet, getDocumentColumns());
		String documentName = "";
		String documentCreatedBy = "";
		Calendar documentCreatedOn;
		String documentCreatedOnString = "";
		String documentCategory = "";
		String documentApprovalBy = "";
		String documentStage = "";
		String documentSheetName = "";
		String documentRowName = "";
		String documentDirectory = "";
		String documentFile = "";
		String documentExtSystem = "";
		String documentExtObject = "";
		String documentExtIdentifier = "";
		String documentDescription = "";
		String documentReference = "";
		int idxName;
		int idxCreatedBy;
		int idxCreatedOn;
		int idxCategory;
		int idxApprovalBy;
		int idxStage;
		int idxSheetName;
		int idxRowName;
		int idxDirectory;
		int idxFile;
		int idxExtSystem;
		int idxExtObject;
		int idxExtIdentifier;
		int idxDescription;
		int idxReference;
		idxName = columnDictionary
				.get(DocumentColumnNameLiterals.Name.toString());
		idxCreatedBy = columnDictionary.get(DocumentColumnNameLiterals.CreatedBy
				.toString());
		idxCreatedOn = columnDictionary.get(DocumentColumnNameLiterals.CreatedOn
				.toString());
		idxCategory = columnDictionary.get(DocumentColumnNameLiterals.Category
				.toString());
		idxApprovalBy = columnDictionary
				.get(DocumentColumnNameLiterals.ApprovalBy.toString());
		idxStage = columnDictionary.get(DocumentColumnNameLiterals.Stage
				.toString());
		idxSheetName = columnDictionary.get(DocumentColumnNameLiterals.SheetName
				.toString());
		idxRowName = columnDictionary.get(DocumentColumnNameLiterals.RowName
				.toString());
		idxDirectory = columnDictionary.get(DocumentColumnNameLiterals.Directory
				.toString());
		idxFile = columnDictionary
				.get(DocumentColumnNameLiterals.File.toString());
		idxExtSystem = columnDictionary.get(DocumentColumnNameLiterals.ExtSystem
				.toString());
		idxExtObject = columnDictionary.get(DocumentColumnNameLiterals.ExtObject
				.toString());
		idxExtIdentifier = columnDictionary
				.get(DocumentColumnNameLiterals.ExtIdentifier.toString());
		idxDescription = columnDictionary
				.get(DocumentColumnNameLiterals.Description.toString());
		idxReference = columnDictionary.get(DocumentColumnNameLiterals.Reference
				.toString());
		int rowIdx;
		int firstRowIdx = Worksheet.firstRow;
		for (Row rowData : sheet.getRows())
		{
			rowIdx = rowData.getIndex();
			if (rowIdx > firstRowIdx && COBieSpreadSheet.isRowPopulated(rowData, 1,100))
			{
				DocumentType tmpDocument = documents.addNewDocument();
				if (idxName > -1)
					documentName = rowData.getCellAt(idxName).getData$();
				if (idxCreatedBy > -1)
					documentCreatedBy = rowData.getCellAt(idxCreatedBy).getData$();
				if (idxCreatedOn > -1)
					documentCreatedOnString = rowData.getCellAt(idxCreatedOn)
							.getData$();
				if (idxCategory > -1)
					documentCategory = rowData.getCellAt(idxCategory).getData$();
				if (idxApprovalBy > -1)
					documentApprovalBy = rowData.getCellAt(idxApprovalBy).getData$();
				if (idxStage > -1)
					documentStage = rowData.getCellAt(idxStage).getData$();
				if (idxSheetName > -1)
					documentSheetName = rowData.getCellAt(idxSheetName).getData$();
				if (idxRowName > -1)
					documentRowName = rowData.getCellAt(idxRowName).getData$();
				if (idxDirectory > -1)
					documentDirectory = rowData.getCellAt(idxDirectory).getData$();
				if (idxFile > -1)
					documentFile = rowData.getCellAt(idxFile).getData$();
				if (idxExtSystem > -1)
					documentExtSystem = rowData.getCellAt(idxExtSystem).getData$();
				if (idxExtObject > -1)
					documentExtObject = rowData.getCellAt(idxExtObject).getData$();
				if (idxExtIdentifier > -1)
					documentExtIdentifier = rowData.getCellAt(idxExtIdentifier)
							.getData$();
				if (idxDescription > -1)
					documentDescription = rowData.getCellAt(idxDescription)
							.getData$();
				if (idxReference > -1)
					documentReference = rowData.getCellAt(idxReference).getData$();

				documentCreatedOn = COBieUtility
						.calendarFromString(documentCreatedOnString);
				tmpDocument.setName(COBieUtility.getCOBieString(documentName));
				tmpDocument.setCreatedBy(COBieUtility
						.getCOBieString(documentCreatedBy));
				tmpDocument.setCreatedOn(documentCreatedOn);
				tmpDocument.setCategory(COBieUtility
						.getCOBieString(documentCategory));
				tmpDocument.setApprovalBy(COBieUtility
						.getCOBieString(documentApprovalBy));
				tmpDocument.setStage(COBieUtility.getCOBieString(documentStage));
				tmpDocument.setSheetName(COBieUtility
						.getCOBieString(documentSheetName));
				tmpDocument
						.setRowName(COBieUtility.getCOBieString(documentRowName));
				tmpDocument.setDirectory(COBieUtility
						.getCOBieString(documentDirectory));
				tmpDocument.setFile(COBieUtility.getCOBieString(documentFile));
				tmpDocument.setExtSystem(COBieUtility
						.getCOBieString(documentExtSystem));
				tmpDocument.setExtObject(COBieUtility
						.getCOBieString(documentExtObject));
				tmpDocument.setExtIdentifier(COBieUtility
						.getCOBieString(documentExtIdentifier));
				tmpDocument.setDescription(COBieUtility
						.getCOBieString(documentDescription));
				tmpDocument.setReference(COBieUtility
						.getCOBieString(documentReference));
			}
		}
	}

}
