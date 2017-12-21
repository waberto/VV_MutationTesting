package fr.istic.m2il.vv.mutator.report;

import fr.istic.m2il.vv.mutator.mutant.MutantState;
import fr.istic.m2il.vv.mutator.mutant.Mutator;

import java.io.IOException;
import java.util.*;


public class ReportService implements IReportService{

    private Long scanClassesTime = new Long(0);
    private Long coverageAndDependencyAnalysisTime = new Long(0);
    private Long buildMutationTestsTime = new Long(0);
    private Long runMutationAnalysisTime = new Long(0);
    private Integer ranTestsNumber;
    private HashMap<Mutator, List<Report>> reports;
    private static ReportService instance;
    private IReportStrategy reportStrategy;

    private ReportService(){
        reports = new HashMap<>();

        ranTestsNumber = new Integer(0);
    }

    public static ReportService getInstance(){
        if(instance == null){
            instance = new ReportService();
        }
        return instance;
    }

    public HashMap<Mutator, List<Report>> getReports() {
        return reports;
    }

    @Override
    public void addReport(Mutator mutator, Report report) {
        if(reports.get(mutator) == null){
            List<Report> mutantReportList = new ArrayList<>();
            mutantReportList.add(report);
            reports.put(mutator, mutantReportList);
        }
        else{
            reports.get(mutator).add(report);
        }
    }

    public Long getScanClassesTime() {
        return scanClassesTime;
    }

    public void setScanClassesTime(Long scanClassesTime) {
        this.scanClassesTime = scanClassesTime;
    }

    public Long getCoverageAndDependencyAnalysisTime() {
        return coverageAndDependencyAnalysisTime;
    }

    public void setCoverageAndDependencyAnalysisTime(Long coverageAndDependencyAnalysisTime) {
        this.coverageAndDependencyAnalysisTime = coverageAndDependencyAnalysisTime;
    }

    public Long getBuildMutationTestsTime() {
        return buildMutationTestsTime;
    }

    public void setBuildMutationTestsTime(Long buildMutationTestsTime) {
        this.buildMutationTestsTime = buildMutationTestsTime;
    }

    public void newRanTest(){
        ranTestsNumber++;
    }
    public Integer getRanTestsNumber() {
        return ranTestsNumber;
    }

    public Long getRunMutationAnalysisTime() {
        return runMutationAnalysisTime;
    }

    public void setRunMutationAnalysisTime(Long runMutationAnalysisTime) {
        this.runMutationAnalysisTime = runMutationAnalysisTime;
    }

    @Override
    public String toMarkdown() {
        return "";
    }

    private void displayStatictics() {

        System.out.println("- Statistics ");
        System.out.println("================================================================================");
        System.out.println(">> Generated " + this.getTotalMutationsNumber() + " mutations killed " + this.getTotalKilledMutantsNumber() + " (" + String.format("%.2f", this.getRate(this.getTotalKilledMutantsNumber(), this.getTotalMutationsNumber())) + "%)");
        System.out.println(">> Test Classe Ran " + this.getTotalMutationsNumber() + " (1 test class per mutation)");
        System.out.println(">> Total Test Cases Ran " + this.getTotalTestsCasesRan());
        System.out.println();
    }

    private void displayTimings() {
        System.out.println();
        System.out.println("- Timings ");
        System.out.println("================================================================================");
        System.out.println("> scan classpath : < " + (this.getScanClassesTime()+1) + " second(s)");
        //System.out.println("> scan classpath : < " + 1 + " second");
        //System.out.println("> scan classpath : < " + 1 + " second");
        System.out.println("> run mutation analysis : < " + this.getRunMutationAnalysisTime() + " second(s)");

        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("> Total : " + doTotalTime() + " second(s)");
        System.out.println();
    }

    private void displayMutators() {
        Iterator iterator = this.reports.entrySet().iterator();
        System.out.println("- Mutators ");
        System.out.println("================================================================================");
        while(iterator.hasNext()){
            Map.Entry reportMutator = (Map.Entry) iterator.next();
            System.out.println("> " + ((Mutator)reportMutator.getKey()).getClass().getName());
            System.out.println(">> Generated " + ((List<Report>)reportMutator.getValue()).size() + " Killed " +getStateMutantsNumber(MutantState.KILLED, (List<Report>)reportMutator.getValue()) +" " + String.format("%.2f", this.getRate(getStateMutantsNumber(MutantState.KILLED, (List<Report>)reportMutator.getValue()), ((List<Report>)reportMutator.getValue()).size())) + "%" );

            System.out.println("> KILLED " + getStateMutantsNumber(MutantState.KILLED, (List<Report>)reportMutator.getValue()) + " SURVIVED " + getStateMutantsNumber(MutantState.SURVIVED, (List<Report>)reportMutator.getValue()) +" TIMED_OUT " + getStateMutantsNumber(MutantState.TIMED_OUT, (List<Report>)reportMutator.getValue())  + " NON_VIABLE " + getStateMutantsNumber(MutantState.NON_VIABLE, (List<Report>)reportMutator.getValue()) );
            /*for(Report r:((List<Report>)reportMutator.getValue())){
                System.out.println(" Report info " + r.getMutationDescription());
            }*/
            System.out.println("> MEMORY_ERROR 0 NOT_STARTED 0 STARTED 0 RUN_ERROR 0");
            System.out.println("--------------------------------------------------------------------------------");
        }



    }

    @Override
    public void doReport() {
        displayTimings();
        displayStatictics();
        displayMutators();
    }

    @Override
    public void toGraphicReport() throws IOException {
        this.reportStrategy.execute();
    }


    public Long doTotalTime() {
        return  scanClassesTime + coverageAndDependencyAnalysisTime + buildMutationTestsTime + runMutationAnalysisTime;
    }

    /*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString() {
        return "ReportService [reports=" + reports + "]";
    }

    /**
     *  Return the number of mutants that state is mutantState in a list of reports
     * @param mutantState state of mutant
     * @param reports list of reports about generated mutants
     * @return the Mutant killed number in the list of mutants
     */

    public Integer getStateMutantsNumber(MutantState mutantState, List<Report> reports){
        int stateMutantsNumber = 0;
        for(Report report: reports){
            if(report.getMutantState() == mutantState){
                stateMutantsNumber++;
            }
        }
        return stateMutantsNumber;
    }

    public Integer getTotalMutationsNumber(){
        Integer mutationNumber = new Integer(0);
        Iterator iterator = this.reports.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry reportMutator = (Map.Entry)iterator.next();
            mutationNumber += ((List<Report>)reportMutator.getValue()).size();
        }
        return mutationNumber;
    }
    public Integer getTotalTestsCasesRan(){
        Integer testsCasesNumber = new Integer(0);
        Iterator iterator = this.reports.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry reportMutator = (Map.Entry)iterator.next();
            for(Report report: (List<Report>)reportMutator.getValue()){
                testsCasesNumber+= report.getTestsRan();
            }
        }
        return testsCasesNumber;
    }

    public Integer getTotalKilledMutantsNumber(){
        Integer totalKilledMutantsNumber = new Integer(0);
        Iterator iterator = this.reports.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry reportMutator = (Map.Entry)iterator.next();
            totalKilledMutantsNumber += getStateMutantsNumber(MutantState.KILLED,((List<Report>)reportMutator.getValue()));
        }
        return totalKilledMutantsNumber;
    }

    public Float getRate(Integer subset, Integer total){
        return Float.valueOf(((Float.valueOf(subset) / Float.valueOf(total))) * 100).floatValue();
    }


    public void setReports(HashMap<Mutator, List<Report>> reports) {
        this.reports = reports;
    }

    public void setRanTestsNumber(Integer ranTestsNumber) {
        this.ranTestsNumber = ranTestsNumber;
    }

    public IReportStrategy getReportStrategy() {
        return reportStrategy;
    }

    public void setReportStrategy(IReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
        this.reportStrategy.configure(this);
    }
}
