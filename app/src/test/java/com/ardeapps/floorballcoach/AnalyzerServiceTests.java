package com.ardeapps.floorballcoach;

import com.ardeapps.floorballcoach.objects.Chemistry;
import com.ardeapps.floorballcoach.objects.Goal;
import com.ardeapps.floorballcoach.objects.Line;
import com.ardeapps.floorballcoach.objects.Player;
import com.ardeapps.floorballcoach.services.AnalyzerHelper;
import com.ardeapps.floorballcoach.services.AnalyzerService;
import com.ardeapps.floorballcoach.services.JSONService;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class AnalyzerServiceTests extends JSONService {

    // Some base data
    String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
    String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"

    @Before
    public void initAnalyzerServiceData() {
        AnalyzerService.setGoalsInGames(getTeamGoalsByGameId(teamId));
        AnalyzerService.setLinesInGames(getLinesOfGames(teamId));
        AnalyzerService.setPlayersInTeam(getPlayers(teamId));
    }

    @Test
    public void testGetLineChemistryPercent() {
        Line line = getLine(teamId, lineId);
        int percent = AnalyzerService.getInstance().getLineChemistryPercent(line);
        System.out.println("Percent: " + percent);
    }

    @Test
    public void isChemistryPointsCorrect() {
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";

        Player player = getPlayer(testPlayerId);
        if(player != null) {
            System.out.println("player: " + player.getName());
        }

        System.out.println("All goals by team:");
        ArrayList<Goal> teamGoals = getTeamGoals(teamId);
        for(Goal goal : teamGoals) {
            System.out.println(goal.getGoalId());
        }

        System.out.println("All goals by player:");
        ArrayList<Goal> playerGoals = getPlayerGoals(testPlayerId);
        for(Goal goal : playerGoals) {
            System.out.println(goal.getGoalId());
        }

        Map<String, String> playerIdMap = getPlayersOfLine(teamId, lineId);
        // TODO testaa
        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));
        ArrayList<Chemistry> playerChemistries =  AnalyzerService.getInstance().getPlayerChemistries(players, getTeamGoals(teamId));

        String bestAssister = AnalyzerService.getInstance().getBestScorerOrAssistant(false, "-LYDueLQnDlNS3iny3r2", getTeamGoals(teamId));
        System.out.println(bestAssister);

    }

    @Test
    public void testGetLineChemistry() {
        Line line = getLine(teamId, lineId);
        Map<Player.Position, ArrayList<Chemistry>> lineChemistry = AnalyzerHelper.getChemistriesInLineForPositions(line);
        for (Map.Entry<Player.Position, ArrayList<Chemistry>> chemistry : lineChemistry.entrySet()) {
            Player.Position position = chemistry.getKey();
            ArrayList<Chemistry> chemistries = chemistry.getValue();
            for(Chemistry chem : chemistries) {
                System.out.println(position.toDatabaseName() + " -> " + chem.getComparePosition() + ": " + chem.getChemistryPoints());
            }
        }
    }

    @Test
    public void testGetChemistryPointsPercent() {
        String playerId = "-LZf45PcYqU3sb7p5GFr";
        String comparePlayerId = "-LZf4BuzjpwdhZFWX1G5";

        double percent = AnalyzerHelper.getChemistryPercent(playerId, comparePlayerId);
        System.out.println("percent: " + percent);
    }

    @Test
    public void getMaxAndMinChemistryPoints() {
        int minPoints = AnalyzerHelper.getMinChemistryPoints();
        int maxPoints = AnalyzerHelper.getMaxChemistryPoints();
        System.out.println("maxPoints: " + maxPoints);
        System.out.println("minPoints: " + minPoints);
    }

    @Test
    public void testMethod() {

        String teamId = "-LYDu_zW16xskSIhIlOm"; // "O2 jyväskylä"
        String lineId = "-LYDuaJHLYXZBTjVtWjK"; // "1. kenttä"
        String testPlayerId = "-LYDueLQnDlNS3iny3r2";
        Player testPlayer = getPlayer(testPlayerId);
        Player comparePlayer = getPlayer("-LZf423Q01lgFW8yD5Dl");

        System.out.println("player1 = -LZf423Q01lgFW8yD5Dl");

        String player1Compare = AnalyzerService.getInstance().getBestScorerOrAssistant(false, testPlayerId, getTeamGoals(teamId));
        System.out.println("player1Compare= " + player1Compare);

        System.out.println("player2 = -LZf4BuzjpwdhZFWX1G5");

        String player2Compare = AnalyzerService.getInstance().getBestScorerOrAssistant(true, testPlayerId, getTeamGoals(teamId));
        System.out.println("player2Compare= " + player2Compare);

        int newChemistry = AnalyzerHelper.getChemistryPoints(testPlayer.getPlayerId(), comparePlayer.getPlayerId(), getTeamGoals(teamId));
        System.out.println(newChemistry);

        ArrayList<Player> players = getPlayers(Arrays.asList("-LZf45PcYqU3sb7p5GFr", "-LZf423Q01lgFW8yD5Dl"));

        ArrayList<Chemistry> chemistryList = AnalyzerService.getInstance().getPlayerChemistries(players, getTeamGoals(teamId));
        boolean isEmpty = chemistryList.isEmpty();
        System.out.println(isEmpty);
    }

    @Test
    public void fastTesting() {

        String teamId = "-LYDu_zW16xskSIhIlOm";
        ArrayList<Player> players = new ArrayList<>();
        players.add(getPlayer("-LYDueLQnDlNS3iny3r2"));
        players.add(getPlayer("-LZf3phcktGRlKYCJYNo"));
        players.add(getPlayer("-LZf423Q01lgFW8yD5Dl"));
        players.add(getPlayer("-LZf45PcYqU3sb7p5GFr"));
        players.add(getPlayer("-LZf4BuzjpwdhZFWX1G5"));

        AnalyzerService.getInstance().getBestPlayerChemistries(players, getTeamGoals(teamId));
    }

}

