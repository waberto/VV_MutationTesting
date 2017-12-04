package fr.istic.m2il.vv.mutator.config;

import fr.istic.m2il.vv.mutator.mutant.MutantType;
import fr.istic.m2il.vv.mutator.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MutatingProperties {


    public static List<MutantType> mutantsToAnalysis() throws IOException {
        List<MutantType> mutantTypes = new ArrayList<>();
        String mutatorsPropertie = ApplicationProperties.getInstance().getApplicationPropertiesFile().getProperty(ConfigurationProperties.MUTATORS.toString());
        mutatorsPropertie = mutatorsPropertie.trim();
        String[] mutators = mutatorsPropertie.split(",");
        if(mutators.length == 0){
            for(MutantType mutantType:MutantType.values()){
                mutantTypes.add(mutantType);
            }
        }
        else{
            for(String mutator: mutators){
                for(MutantType mutantType:MutantType.values()){
                    mutator = mutator.trim();
                    if(mutator.matches(mutantType.name()))
                        mutantTypes.add(MutantType.valueOf(mutator));
                }
            }
        }
        return mutantTypes;
    }
}
