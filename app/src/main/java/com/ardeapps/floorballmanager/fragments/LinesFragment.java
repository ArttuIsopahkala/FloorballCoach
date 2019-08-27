package com.ardeapps.floorballmanager.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.analyzer.AllowedPlayerPosition;
import com.ardeapps.floorballmanager.analyzer.BestLineType;
import com.ardeapps.floorballmanager.objects.Line;
import com.ardeapps.floorballmanager.objects.UserConnection;
import com.ardeapps.floorballmanager.resources.GameLinesResource;
import com.ardeapps.floorballmanager.resources.GoalsResource;
import com.ardeapps.floorballmanager.resources.LinesResource;
import com.ardeapps.floorballmanager.analyzer.AnalyzerService;
import com.ardeapps.floorballmanager.utils.Logger;
import com.ardeapps.floorballmanager.views.LineUpSelector;

import java.util.Map;


public class LinesFragment extends Fragment {

    LineUpSelector lineUpSelector;
    Button analyzeChemistryButton;
    Button getBestLinesButton;
    Button saveButton;
    TextView teamChemistryValueText;
    ProgressBar teamChemistryBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lines, container, false);
        lineUpSelector = v.findViewById(R.id.lineUpSelector);
        analyzeChemistryButton = v.findViewById(R.id.analyzeChemistryButton);
        getBestLinesButton = v.findViewById(R.id.getBestLinesButton);
        teamChemistryValueText = v.findViewById(R.id.teamChemistryValueText);
        teamChemistryBar = v.findViewById(R.id.teamChemistryBar);
        saveButton = v.findViewById(R.id.saveButton);

        // Role specific content
        UserConnection.Role role = AppRes.getInstance().getSelectedRole();
        if (role == UserConnection.Role.PLAYER) {
            saveButton.setVisibility(View.GONE);
        } else {
            saveButton.setVisibility(View.VISIBLE);
        }

        teamChemistryValueText.setText("-");
        teamChemistryBar.post(() -> teamChemistryBar.setProgress(0));

        lineUpSelector.createView(this, true);
        final Map<Integer, Line> lines = AppRes.getInstance().getLines();
        lineUpSelector.setLines(lines);
        lineUpSelector.setListener(this::refreshTeamChemistry);

        analyzeChemistryButton.setOnClickListener(button -> {
            if(AppRes.getInstance().getGoalsByGame().isEmpty()) {
                GoalsResource.getInstance().getAllGoals(goals -> {
                    AppRes.getInstance().setGoalsByGame(goals);
                    GameLinesResource.getInstance().getLines(lines1 -> {
                        AppRes.getInstance().setLinesByGame(lines1);
                        lineUpSelector.showLineChemistry();
                        refreshTeamChemistry();
                    });
                });
            } else {
                lineUpSelector.showLineChemistry();
                refreshTeamChemistry();
            }
        });

        getBestLinesButton.setOnClickListener(button -> {
            if(AppRes.getInstance().getActivePlayers().size() < 5) {
                Logger.toast(AppRes.getContext().getString(R.string.lineup_too_few_players));
                return;
            }
            if(AppRes.getInstance().getGoalsByGame().isEmpty()) {
                GoalsResource.getInstance().getAllGoals(goals -> {
                    AppRes.getInstance().setGoalsByGame(goals);
                    GameLinesResource.getInstance().getLines(lines1 -> {
                        AppRes.getInstance().setLinesByGame(lines1);
                        Map<Integer, Line> bestLines = AnalyzerService.getInstance().getBestLines(AllowedPlayerPosition.MOST_GOALS_IN_POSITION, BestLineType.BEST_TEAM_CHEMISTRY);
                        lineUpSelector.setLines(bestLines);
                        lineUpSelector.showLineChemistry();
                        refreshTeamChemistry();
                    });
                });
            } else {
                Map<Integer, Line> bestLines = AnalyzerService.getInstance().getBestLines(AllowedPlayerPosition.MOST_GOALS_IN_POSITION, BestLineType.BEST_TEAM_CHEMISTRY);
                lineUpSelector.setLines(bestLines);
                lineUpSelector.showLineChemistry();
                refreshTeamChemistry();
            }
        });

        saveButton.setOnClickListener(button -> {
            Map<Integer, Line> linesToSave = lineUpSelector.getLines();
            LinesResource.getInstance().saveLines(linesToSave, lines12 -> AppRes.getInstance().setLines(lines12));
        });

        return v;
    }

    private void refreshTeamChemistry() {
        Map<Integer, Line> lines = AppRes.getInstance().getLines();
        int percent = AnalyzerService.getInstance().getTeamChemistryPercent(lines);
        teamChemistryValueText.setText(String.valueOf(percent));
        teamChemistryBar.setProgress(percent);
    }

}
