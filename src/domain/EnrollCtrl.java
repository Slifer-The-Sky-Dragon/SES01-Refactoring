package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;
import domain.exceptions.GeneralException;

public class EnrollCtrl {
    private void checkCourseInterferences(List<Course> newCourses, GeneralException exceptions) {
        for (Course newCourse1: newCourses){
            for (Course newCourse2: newCourses){
                if (newCourse1 == newCourse2)
                    continue;
                if (newCourse1.hasSameExamDateWith(newCourse2))
                    exceptions.addException(new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", newCourse1, newCourse2)));
                if (newCourse1.equals(newCourse2))
                    exceptions.addException(new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", newCourse1.getName())));
            }
        }
    }
    private void checkStudentHasAlreadyPassedCourse(Transcript transcript, Course newCourse,
                                                    GeneralException exceptions) {
        if(transcript.containsPassedCourse(newCourse))
            exceptions.addException(new EnrollmentRulesViolationException(String.format("The student has already passed %s", newCourse.getName())));
    }
    private void checkStudentHasAlreadyPassedCoursePrerequisites(Transcript transcript, Course newCourse,
                                                                 GeneralException exceptions) {
        List<Course> newCoursePrerequisites = newCourse.getPrerequisites();
        List<Course> notPassedPrerequisites = transcript.findNotPassedPrerequisites(newCoursePrerequisites);
        if(notPassedPrerequisites.size() != 0)
            for(Course prerequisite: notPassedPrerequisites)
                exceptions.addException(new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", prerequisite.getName(), newCourse.getName())));
    }
    private void checkNumberOfUnitsWithGpa(Transcript transcript, List<Course> newCourses,
                                           GeneralException exceptions) {
        int unitsRequested = 0;
        for (Course newCourse : newCourses)
            unitsRequested += newCourse.getUnits();
        double gpa = transcript.getGpa();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            exceptions.addException(new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa)));
    }
	public void enroll(Student student, List<Course> newCourses)
            throws GeneralException {
        GeneralException exceptions = new GeneralException();
        checkCourseInterferences(newCourses, exceptions);
        Transcript transcript = student.getTranscript();
        for (Course newCourse : newCourses) {
            checkStudentHasAlreadyPassedCourse(transcript, newCourse, exceptions);
            checkStudentHasAlreadyPassedCoursePrerequisites(transcript, newCourse, exceptions);
		}
        checkNumberOfUnitsWithGpa(transcript, newCourses, exceptions);
        if(exceptions.getAllExceptions().size() == 0)
            for (Course newCourse : newCourses)
                student.takeCourse(newCourse);
        else
            throw exceptions;
	}
}
