package domain;

import domain.exceptions.EnrollmentRulesViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Transcript {
    private List<TermTranscript> termsTranscript;

    public Transcript(){
        termsTranscript = new ArrayList<>();
    }


    public List<TermTranscript> getTermTranscript(){
        return termsTranscript;
    }
    private TermTranscript findTermTranscript(Term term){
        for(TermTranscript termTranscript: termsTranscript)
            if(termTranscript.isTermSame(term))
                return termTranscript;
        return null;
    }

    public void addTranscriptRecord(Term term, Course course, double grade) {
        TermTranscript termTranscript = findTermTranscript(term);
        if (termTranscript == null) {
            TermTranscript newTermTranscript = new TermTranscript(term);
            newTermTranscript.addRecord(course, grade);
            termsTranscript.add(newTermTranscript);
        } else {
            termTranscript.addRecord(course, grade);
        }
    }

    public Boolean containsPassedCourse(Course course) {
        for (TermTranscript termTranscript : this.termsTranscript)
            if(termTranscript.containsPassedCourse(course))
                return true;
        return false;
    }

    public List<Course> findNotPassedPrerequisites(List<Course> prerequisites) {
        List<Course> notPassedPrerequisites = new ArrayList<>();
        for(Course prerequisite: prerequisites)
            if(!this.containsPassedCourse(prerequisite))
                notPassedPrerequisites.add(prerequisite);
        return notPassedPrerequisites;
    }

    public double getGpa() {
        double points = 0;
        int totalUnits = 0;
        for (TermTranscript termTranscript : this.termsTranscript) {
            points += termTranscript.getTotalPoints();
            totalUnits += termTranscript.getTotalUnits();
        }
        return points / totalUnits;
    }

}
