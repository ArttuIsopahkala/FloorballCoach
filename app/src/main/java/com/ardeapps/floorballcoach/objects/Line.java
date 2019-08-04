package com.ardeapps.floorballcoach.objects;

import com.google.firebase.database.Exclude;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Line {

    private String lineId;
    private String gameId;
    private String seasonId;
    private int lineNumber;
    private Map<String, String> playerIdMap;

    public Line() {
    }

    public Line clone() {
        Line clone = new Line();
        clone.lineId = this.lineId;
        clone.gameId = this.gameId;
        clone.seasonId = this.seasonId;
        clone.lineNumber = this.lineNumber;
        clone.playerIdMap = this.playerIdMap;
        return clone;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Map<String, String> getPlayerIdMap() {
        if(playerIdMap == null) {
            playerIdMap = new HashMap<>();
        }
        return playerIdMap;
    }

    public void setPlayerIdMap(Map<String, String> playerIdMap) {
        this.playerIdMap = playerIdMap;
    }

    @Exclude
    public Map<String, String> getSortedPlayers() {
        TreeMap<String, String> sorted = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Player.Position pos1 = Player.Position.valueOf(o1);
                Player.Position pos2 = Player.Position.valueOf(o2);
                if(pos1 == Player.Position.LW) {
                    return -1;
                } else if (pos1 == Player.Position.C && pos2 != Player.Position.LW) {
                    return -1;
                } else if (pos1 == Player.Position.RW && (pos2 != Player.Position.C && pos2 != Player.Position.LW)) {
                    return -1;
                } else if (pos1 == Player.Position.LD && (pos2 != Player.Position.C && pos2 != Player.Position.LW && pos2 == Player.Position.RW)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        sorted.putAll(getPlayerIdMap());
        return sorted;
    }

    public boolean isSame(Line line) {
        if(line.getLineNumber() != getLineNumber() || !line.getPlayerIdMap().equals(getPlayerIdMap())) {
            return false;
        }
        return true;
    }
}
