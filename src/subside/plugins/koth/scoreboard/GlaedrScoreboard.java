package subside.plugins.koth.scoreboard;

public class GlaedrScoreboard /* extends AbstractScoreboard */ {
//
//    Glaedr glaedr;
//
//    @Override
//    public void load(RunningKoth koth, String titleLoader, String[] textLoader) {
//        this.glaedr = new Glaedr(KothPlugin.getPlugin(), "KoTH", true);
//    }
//
//    @Override
//    public void updateScoreboard() {
//        String[] text = textLoader.clone();
//        
//        sbObject.setTitle(rKoth.fillMessageBuilder(new MessageBuilder(titleLoader)).build()[0]);
//
//        for (int x = 0; x < text.length; x++) {
//            sbObject.setScore(x, rKoth.fillMessageBuilder(new MessageBuilder(text[x])).build()[0]);
//        }
//    }
//
//    @Override
//    public void playerQuit(Player player) {
//        return;
//    }
//
//    @Override
//    public void destroy() {
//        sbObject.clearScoreboard();
//    }
//    
//    
//    
//    
//    class SBObject {
//        private String title;
//        private List<Team> teams = new ArrayList<>();
//        private Scoreboard scoreboard;
//        private Objective obj;
//
//        SBObject() {
//            scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
//            obj = scoreboard.getObjective("KoTH_Scoreboard");
//            if(obj == null){
//                obj = scoreboard.registerNewObjective("KoTH_Scoreboard", "dummy");
//            }
//            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
//            for(int x = 0; x < teams.size(); x++){
//                getTeam(x);
//                
//            }
//        }
//
//        public void setTitle(String ttl) {
//            if (this.title != null) if (this.title.equals(ttl)) return;
//            
//            this.title = ttl;
//            obj.setDisplayName(title);
//
//        }
//        
//        public Team getTeam(int x){
//            if(x > 8){
//                return null;
//            }
//            
//            if(teams.size() <= x ||teams.get(x) == null){
//                while(teams.size() <= x){
//                    teams.add(scoreboard.registerNewTeam("Kteam"+teams.size()));
//                }
//                teams.get(x).addEntry(ChatColor.values()[x].toString());
//                
//                reindexTeams();
//            }
//            return teams.get(x);
//        }
//        
//        public void reindexTeams(){
//            for(int x = 0; x < teams.size(); x++){
//                obj.getScore(ChatColor.values()[x].toString()).setScore(teams.size()-x);
//            }
//        }
//
//        public void setScore(int x, String scr) {
//            getTeam(x).setPrefix(scr);
//
//        }
//        
//        public void clearScoreboard(){
//            for(Team team : teams){
//                for(String entry : team.getEntries()){
//                    team.removeEntry(entry);
//                    scoreboard.resetScores(entry);
//                }
//                team.unregister();
//            }
//        }
//
//        public Scoreboard getScoreboard() {
//            return scoreboard;
//        }
//    }
//    
    
}
