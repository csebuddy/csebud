package Timetable;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.DataBase;
import database.ExcelRead;
import rule.*;

public class TimetableGenerator{
	
	private ArrayList<LectureCourse> lecture;
	private TimetableFactory factory;
	private ArrayList<Timetable> timetable;
	private ArrayList<LecturePossible> lecturePossible; // 실제 알고리즘을 적용할 강의들
	private int max_credit; //최대로 들을 credit(사용자로 부터 받는다.)
	private int Major_count; //필수과목의 계수를 담는 변수
	private int first_priority_num;

	RegulationManager donggukRM;  //regulation을 총괄하는 곳
	
	public TimetableGenerator() throws SQLException{ // 생성자
		donggukRM=new RegulationManager();  //SQLException 나중에 지워야 할듯
		lecturePossible=new ArrayList<LecturePossible>();
		timetable=new ArrayList<Timetable>();
		factory=new TimetableFactory();
		first_priority_num=0;
	}

	public void CreatePossibleLecture(String filename, String regulation_num) { // Lecture을 LecturePossible에 집어넣는 메소드
		Major_count=match_Regulation_to_Lecture(filename, regulation_num); // Lecture에서 강의를 뽑는다.
		setPriority(Major_count); // 우선순위를 설정한다.
	}

	private int match_Regulation_to_Lecture(String filename, String regulation_num) {// Regulation에서 안 들은 강의를 받아 Lecture에서 강의를 뽑아있는지 비교하는 메소드
		//Regulation에서 과목 코드와 
		
		//여기부터 TEST파일
		rule.ExcelRead excel = new rule.ExcelRead(); //엑셀 읽는 부분
		String ExcelFileName=filename; //성적 파일 이름
	    List<Course> cl = excel.getExcelData(ExcelFileName, null); //성적 엑셀을 읽는다.
	    
	    /*전체 과목정보를 읽어오는 부분*/
	    LectureInfo_ExcelRead excel_Lecture=new LectureInfo_ExcelRead();
	    ArrayList<LectureCourse> Semester_Lecture=new ArrayList<LectureCourse>();
	    Semester_Lecture=excel_Lecture.getExcelData("Lecture.xlsx");
	    
	    int Major_Count=0; //필수과목에서 전공 개수를 저장한다.
	    
	    //2012에서 Regulation을 가져온다, 필수과목만
	    List<Lecture> un = donggukRM.findRegulation(regulation_num).unMeetLectureList(cl, "must"); 
	    for(int i=0; i<un.size(); i++){
	    	String temp_Code=un.get(i).getLectureCode(); //강의 코드를 받아온다.
	    	for(int j=0; j<Semester_Lecture.size(); j++){ //Lecture를 받아서 처리
	    		if(temp_Code.equals(Semester_Lecture.get(j).LectureInfo)){
	    			if(Semester_Lecture.get(j).LectureInfo.contains("CSE")){
		    			Major_Count++;
		    		}
	    			lecturePossible.add(new LecturePossible(Semester_Lecture.get(j).LectureInfo, Semester_Lecture.get(j).Division,
	    					Semester_Lecture.get(j).FirstWeek, Semester_Lecture.get(j).FirstStartTime, 
	    					Semester_Lecture.get(j).FirstEndTime, Semester_Lecture.get(j).SecondWeek,
	    					Semester_Lecture.get(j).SecondStartTime, Semester_Lecture.get(j).SecondEndTime, 
	    					Semester_Lecture.get(j).Credit, 0)); //Possible에 넣는 부분
	    			//break;
	    		}
	    		
	    	}
	    }

	    un = donggukRM.findRegulation(regulation_num).unMeetLectureList(cl, "select"); //선택 필수과목만
	    for(int i=0; i<un.size(); i++){
	    	String temp_Code=un.get(i).getLectureCode(); //강의 코드를 받아온다.
	    	for(int j=0; j<Semester_Lecture.size(); j++){ //Lecture를 받아서 처리
	    		if(temp_Code.equals(Semester_Lecture.get(j).LectureInfo)){
	    			lecturePossible.add(new LecturePossible(Semester_Lecture.get(j).LectureInfo, Semester_Lecture.get(j).Division,
	    					Semester_Lecture.get(j).FirstWeek, Semester_Lecture.get(j).FirstStartTime, 
	    					Semester_Lecture.get(j).FirstEndTime, Semester_Lecture.get(j).SecondWeek,
	    					Semester_Lecture.get(j).SecondStartTime, Semester_Lecture.get(j).SecondEndTime, 
	    					Semester_Lecture.get(j).Credit, 1)); //Possible에 넣는 부분
	    			//break;
	    		}
	    	}
	    }
	      
	    if(donggukRM.findRegulation(regulation_num).getMscMath() > donggukRM.findRegulation(regulation_num).MeetCredit(cl, "mscMath")) //수학과목
	    {
	    	un = donggukRM.findRegulation(regulation_num).unMeetLectureList(cl, "mscMath");
	    	for(int i =0 ; i<un.size();i++ )
	    	{
	    		String temp_Code=un.get(i).getLectureCode(); //강의 코드를 받아온다.
		    	for(int j=0; j<Semester_Lecture.size(); j++){ //Lecture를 받아서 처리
		    		if(temp_Code.equals(Semester_Lecture.get(j).LectureInfo)){
		    			lecturePossible.add(new LecturePossible(Semester_Lecture.get(j).LectureInfo, Semester_Lecture.get(j).Division,
		    					Semester_Lecture.get(j).FirstWeek, Semester_Lecture.get(j).FirstStartTime, 
		    					Semester_Lecture.get(j).FirstEndTime, Semester_Lecture.get(j).SecondWeek,
		    					Semester_Lecture.get(j).SecondStartTime, Semester_Lecture.get(j).SecondEndTime, 
		    					Semester_Lecture.get(j).Credit, 2)); //Possible에 넣는 부분
		    			//break;
		    		}
		    	}
	    	}
	    	  
	    }
	    
	    if(donggukRM.findRegulation(regulation_num).getMscScience() > donggukRM.findRegulation(regulation_num).MeetCredit(cl, "mscScience")){//과학과목
		      un = donggukRM.findRegulation(regulation_num).unMeetLectureList(cl, "mscScience");
		      for(int i =0 ; i<un.size();i++ )
		      {
		    	  String temp_Code=un.get(i).getLectureCode(); //강의 코드를 받아온다.
			    	for(int j=0; j<Semester_Lecture.size(); j++){ //Lecture를 받아서 처리
			    		if(temp_Code.equals(Semester_Lecture.get(j).LectureInfo)){
			    			lecturePossible.add(new LecturePossible(Semester_Lecture.get(j).LectureInfo, Semester_Lecture.get(j).Division,
			    					Semester_Lecture.get(j).FirstWeek, Semester_Lecture.get(j).FirstStartTime, 
			    					Semester_Lecture.get(j).FirstEndTime, Semester_Lecture.get(j).SecondWeek,
			    					Semester_Lecture.get(j).SecondStartTime, Semester_Lecture.get(j).SecondEndTime, 
			    					Semester_Lecture.get(j).Credit, 2)); //Possible에 넣는 부분
			    			//break;
			    		}
			    	}
		      }
	      }
	    
	    return Major_Count;
	      
	}

	private void setPriority(int Major_count) { //우선순위를 정하는 메소드
		int temp_priority=1;
		Major_count+=1;
		ArrayList<LecturePossible> temp_Lecture=new ArrayList<LecturePossible>();

		for(int i=0; i<lecturePossible.size(); i++){ //초기화
			temp_Lecture.add(null);
		}
		
		int First_Major_count=Major_count;
		boolean Is_no_major=false;
		for(int i=0; i<lecturePossible.size();i++){ //우선순위 값을 집어넣는다.
			switch(lecturePossible.get(i).priority){
			case 0:
				if(lecturePossible.get(i).LectureInfo.contains("CSE")){ 
					lecturePossible.get(i).priority=temp_priority++;
					LecturePossible temp=lecturePossible.get(i);
					temp_Lecture.set(temp_priority-2, temp);
				}
				else{//전공이 아니면 뒤로 미룬다.
					lecturePossible.get(i).priority=Major_count++;
					LecturePossible temp=lecturePossible.get(i);
					temp_Lecture.set(Major_count-2, temp);
					Is_no_major=true;
				}
				break;
			default:
				lecturePossible.get(i).priority=temp_priority++;
				LecturePossible temp=lecturePossible.get(i);
				temp_Lecture.set(temp_priority-2, temp);
			}
			if(temp_priority>=(First_Major_count)&&Is_no_major){
				while(temp_priority!=(Major_count))
					temp_priority++;
				Is_no_major=false;
			}
			
		}
		
		//실제 강의 정보를 받아올 곳
		
		lecturePossible=temp_Lecture;
		int real_priority=1;
		for(int i=1; i<lecturePossible.size(); i++){
			if(lecturePossible.get(i-1).LectureInfo.equals(lecturePossible.get(i).LectureInfo)){ //전 인덱스와 강의정보가 같다면,
				lecturePossible.get(i).setPriority(real_priority);
			}
			else{
				lecturePossible.get(i).setPriority(++real_priority);
			}
		}
		
		for(int i=0; i<lecturePossible.size(); i++){
			if(lecturePossible.get(i).priority==1){ //첫 번째 우선순위 개수를 카운트한다.
				first_priority_num++;
			}
			else if(lecturePossible.get(i).priority==2)
				break;
		}
		

		for(int i=0; i<lecturePossible.size(); i++){ //test
			System.out.println(lecturePossible.get(i).LectureInfo+" "+lecturePossible.get(i).priority);
		}
		
	}

	public void autoGenerateTimetable() { // 타임테이블을 생성한다.
		timetable=factory.makeTimetable(lecturePossible, max_credit, first_priority_num);
	}
	
	public void setCredit_max(int max_credit){ //최대로 들을 학점 set
		this.max_credit=max_credit;
	}

}
