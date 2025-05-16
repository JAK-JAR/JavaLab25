public class DeathCauseStatistic {
    private String ICD10;
    private int[] deathCounts;

    public DeathCauseStatistic(String ICD10, int[] deathCounts) {
        this.ICD10 = ICD10;
        this.deathCounts = deathCounts;
    }

    public static DeathCauseStatistic fromCsvLine(String Line){
        String[] parts = Line.split("\t");
        if(parts.length != 2) {
            throw new IllegalArgumentException("Invalid line format");
        } else {
            String ICD10 = parts[0].trim();
            int[] deaths = new int[21];
            String[] deathTokens = parts[1].trim().split(",");
            for(int i = 0; i < deathTokens.length; i++) {
                deaths[i] = Integer.parseInt(deathTokens[i].trim());
            }
            return new DeathCauseStatistic(ICD10, deaths);
        }
    }

    public String getICD10() {
        return ICD10;
    }

    public class AgeBracketDeaths {
        public final int young;
        public final int old;
        public final int deathCount;

        public AgeBracketDeaths(int young, int old, int deathCount) {
            this.young = young;
            this.old = old;
            this.deathCount = deathCount;
        }
    }

    public AgeBracketDeaths getAgeBracketDeaths(int age) {
        int young, old;
        if(age < 0) throw new IllegalArgumentException("Age must be non-negative");
        else {
            if(age <= 4) {
                old = 4;
                young = 0;
            } else if(age <= 9) {
                old = 9;
                young = 5;
            }
            else if(age <= 14) {
                old = 14;
                young = 9;
            }
            else if(age <= 19) {
                old = 19;
                young = 15;
            }
            else if(age <= 24) {
                old = 24;
                young = 20;
            }
            else if(age <= 29) {
                old = 29;
                young = 25;
            }
            else if(age <= 34) {
                old = 34;
                young = 30;
            }
            else if(age <= 39) {
                old = 39;
                young = 35;
            }
            else if(age <= 44) {
                old = 44;
                young = 40;
            }
            else if(age <= 49) {
                old = 49;
                young = 45;
            }
            else if(age <= 54) {
                old = 54;
                young = 50;
            }
            else if(age <= 59) {
                old = 59;
                young = 55;
            }
            else if(age <= 64) {
                old = 64;
                young = 60;
            }
            else if(age <= 69) {
                old = 69;
                young = 65;
            }
            else if(age <= 74) {
                old = 74;
                young = 70;
            }
            else if(age <= 79) {
                old = 79;
                young = 75;
            }
            else if(age <=84) {
                old =84 ;
                young=80 ;
            } else if(age <= 89) {
                old = 89;
                young = 85;
            }
            else if(age <= 94) {
                old = 94;
                young = 90;
            }
            else {
                old = 100;
                young = 95;
            }
            return new AgeBracketDeaths(young, old, 0);
        }
    }

}
