package domain;

import java.util.List;
import java.util.Map;

import domain.exceptions.EnrollmentRulesViolationException;

public class EnrollCtrl {
    private void checkCourseInterferences(List<Course> newCourses)
            throws EnrollmentRulesViolationException {
        for (Course newCourse1: newCourses){
            for (Course newCourse2: newCourses){
                if (newCourse1 == newCourse2)
                    continue;
                if (newCourse1.hasSameExamDateWith(newCourse2))
                    throw new EnrollmentRulesViolationException(String.format("Two offerings %s and %s have the same exam time", newCourse1, newCourse2));
                if (newCourse1.equals(newCourse2))
                    throw new EnrollmentRulesViolationException(String.format("%s is requested to be taken twice", newCourse1.getName()));
            }
        }
    }
    private void checkStudentHasAlreadyPassedCourse(Transcript transcript, Course newCourse)
            throws EnrollmentRulesViolationException {
        if(transcript.containsPassedCourse(newCourse))
            throw new EnrollmentRulesViolationException(String.format("The student has already passed %s", newCourse.getName()));
    }
    private void checkStudentHasAlreadyPassedCoursePrerequisites(Transcript transcript, Course newCourse)
            throws EnrollmentRulesViolationException {
        List<Course> newCoursePrerequisites = newCourse.getPrerequisites();
        Course notPassedPrerequisite = transcript.findNotPassedPrerequisites(newCoursePrerequisites);
        if(notPassedPrerequisite != null)
            throw new EnrollmentRulesViolationException(String.format("The student has not passed %s as a prerequisite of %s", notPassedPrerequisite.getName(), newCourse.getName()));
    }
    private void checkNumberOfUnitsWithGpa(Transcript transcript, List<Course> newCourses)
            throws EnrollmentRulesViolationException {
        int unitsRequested = 0;
        for (Course newCourse : newCourses)
            unitsRequested += newCourse.getUnits();
        double gpa = transcript.getGpa();
        if ((gpa < 12 && unitsRequested > 14) ||
                (gpa < 16 && unitsRequested > 16) ||
                (unitsRequested > 20))
            throw new EnrollmentRulesViolationException(String.format("Number of units (%d) requested does not match GPA of %f", unitsRequested, gpa));
    }
	public void enroll(Student student, List<Course> newCourses)
            throws EnrollmentRulesViolationException {
        checkCourseInterferences(newCourses);
        Transcript transcript = student.getTranscript();
        for (Course newCourse : newCourses) {
            checkStudentHasAlreadyPassedCourse(transcript, newCourse);
            checkStudentHasAlreadyPassedCoursePrerequisites(transcript, newCourse);
		}
        checkNumberOfUnitsWithGpa(transcript, newCourses);
		for (Course newCourse : newCourses)
			student.takeCourse(newCourse);
	}
}
