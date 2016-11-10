package rule;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelRead {
	private String temp_LectureCode;
	private String temp_courseMake_Up;
	private String temp_Grade;
	private boolean temp_IsEnglishCourse;

	public ArrayList<Course> getExcelData(String filepath, String tableName) {
		// public ArrayList<Course> getExcelData(String filepath, String
		// tableName){
		ArrayList<Course> courses=new ArrayList<Course>();
		
		try {
			FileInputStream input = new FileInputStream(filepath);
			XSSFWorkbook workbook = new XSSFWorkbook(input);

			XSSFSheet sheet = workbook.getSheetAt(0); // 성적은 시트가 하나만 존재하므로, 0을 준다.														
			
			
			int totalRow = sheet.getPhysicalNumberOfRows(); // 행의 수를 받아온다.
			// 학수강좌번호(G), 등급(L), 재수강구분(N), 원어강의종류(V)
			for (int rowIndex = 1; rowIndex < totalRow; rowIndex++) { // 행을 읽는다.
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					int totalCell = row.getPhysicalNumberOfCells(); // 셀의 수를 읽는다.

					temp_LectureCode=getCellValue(row, 6); //학수강좌번호
					temp_courseMake_Up=getCellValue(row, 13);  //재수강구분
					temp_Grade=getCellValue(row, 11);  //등급
					String EnglishCourse=getCellValue(row, 21);   //원어강의종류
					if(EnglishCourse!="false"){
						temp_IsEnglishCourse=true;
					}
					else temp_IsEnglishCourse=false;
					courses.add(new Course(temp_LectureCode, temp_courseMake_Up, temp_Grade, temp_IsEnglishCourse));					
				}
			}
		}
		catch (Exception e) {  //에러 검사
			e.printStackTrace(System.out);
		}
		
		return courses;
	}
	
	public String getCellValue(XSSFRow row, int columnIndex) {
		XSSFCell cell = row.getCell(columnIndex); // 각 셀을 가져온다.

		String value = ""; // 셀의 값을 받을 변수
		if (cell == null) { // 셀이 비어있다면 NULL
			return null;
		} else {
			// 타입별로 내용 읽기
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_FORMULA:
				value = cell.getCellFormula();
				break;
			case XSSFCell.CELL_TYPE_NUMERIC:
				value = cell.getNumericCellValue() + "";
				break;
			case XSSFCell.CELL_TYPE_STRING:
				value = cell.getStringCellValue() + "";
				break;
			case XSSFCell.CELL_TYPE_BLANK:
				value = cell.getBooleanCellValue() + "";
				break;
			case XSSFCell.CELL_TYPE_ERROR:
				value = cell.getErrorCellValue() + "";
				break;
			default:
				value = null;
			}
		}
		return value;

	}

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExcelRead excel = new ExcelRead();
		excel.getExcelData("2012.xlsx", null);
	}*/
	
	/*
	 * public static List<String> writeToMysql(String filepath, String
	 * tableName) {
	 * 
	 * List<String> sqls = new ArrayList(); try {
	 * 
	 * FileInputStream input = new FileInputStream(filepath); XSSFWorkbook
	 * workbook = new XSSFWorkbook(input);
	 * 
	 * int rowindex = 0; int columnindex = 0;
	 * 
	 * XSSFSheet sheet = workbook.getSheetAt(0); // �뻾�쓽 �닔 int rows =
	 * sheet.getPhysicalNumberOfRows(); // row 泥ル쾲吏몃뒗 �젣紐⑹쓣 �굹���궡誘�濡� �떎�젣
	 * �뜲�씠�꽣�씤 1遺��꽣 �씫�뒗�떎. for (rowindex = 0; rowindex < rows; rowindex++) {
	 * // �뻾�쓣�씫�뒗�떎 XSSFRow row = sheet.getRow(rowindex); if (row != null) { //
	 * ���쓽 �닔 int cells = row.getPhysicalNumberOfCells(); List<XSSFCell>
	 * cellList = new ArrayList(); // sqㅣ에 학수강좌번호(G), 등급(L), 재수강구분(N), 원어강의종류(V)
	 * String sql = "INSERT INTO " + tableName + " VALUES ('"; for (columnindex
	 * = 0; columnindex < cells; columnindex++) {
	 * 
	 * if (columnindex > 0) { sql = sql + "','"; } sql = sql +
	 * row.getCell(columnindex).toString();
	 * 
	 * } sql = sql + "')"; System.out.println(sql); sqls.add(sql); } }
	 * 
	 * input.close(); System.out.println("Success import excel to mysql table");
	 * } catch (Exception e) { e.printStackTrace(System.out); }
	 * 
	 * return sqls; }
	 */
}