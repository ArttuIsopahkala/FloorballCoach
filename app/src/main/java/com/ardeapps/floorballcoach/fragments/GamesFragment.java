package com.ardeapps.floorballcoach.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.PrefRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.adapters.GameListAdapter;
import com.ardeapps.floorballcoach.dialogFragments.EditSeasonDialogFragment;
import com.ardeapps.floorballcoach.handlers.GetGamesHandler;
import com.ardeapps.floorballcoach.objects.Game;
import com.ardeapps.floorballcoach.objects.Season;
import com.ardeapps.floorballcoach.resources.GamesResource;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.Helper;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;
import com.ardeapps.floorballcoach.views.IconView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GamesFragment extends Fragment implements GameListAdapter.Listener {

    TextView noSeasonsText;
    IconView addSeasonIcon;
    Spinner seasonSpinner;
    Button newGameButton;
    ListView gameList;
    TextView noGamesText;

    GameListAdapter adapter;
    ArrayList<String> seasonIds = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GameListAdapter(AppRes.getActivity());
        adapter.setListener(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_games, container, false);

        noSeasonsText = v.findViewById(R.id.seasonText);
        addSeasonIcon = v.findViewById(R.id.addSeasonIcon);
        seasonSpinner = v.findViewById(R.id.seasonSpinner);
        newGameButton = v.findViewById(R.id.newGameButton);
        gameList = v.findViewById(R.id.gameList);
        noGamesText = v.findViewById(R.id.noGamesText);

        gameList.setEmptyView(noGamesText);
        gameList.setAdapter(adapter);

        setSeasonSpinner();
        // Set default spinner selection
        int seasonPosition = 0;
        if(!AppRes.getInstance().getSeasons().isEmpty()) {
            String seasonId = PrefRes.getSelectedSeasonId(AppRes.getInstance().getSelectedTeam().getTeamId());
            if(seasonId != null) {
                seasonPosition = seasonIds.indexOf(seasonId);
            }
            Helper.setSpinnerSelection(seasonSpinner, seasonPosition > -1 ? seasonPosition : 0);
        }

        addSeasonIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditSeasonDialogFragment dialog = new EditSeasonDialogFragment();
                dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Lisää tunniste");
                dialog.setListener(new EditSeasonDialogFragment.EditSeasonDialogCloseListener() {
                    @Override
                    public void onSeasonSaved(Season season) {
                        AppRes.getInstance().setSeason(season.getSeasonId(), season);
                        setSeasonSpinner();
                        loadGames(season.getSeasonId());
                    }
                });
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Force user to add season before add game
                if(AppRes.getInstance().getSeasons().isEmpty()) {
                    final EditSeasonDialogFragment dialog = new EditSeasonDialogFragment();
                    dialog.show(AppRes.getActivity().getSupportFragmentManager(), "Lisää tunniste");
                    dialog.setListener(new EditSeasonDialogFragment.EditSeasonDialogCloseListener() {
                        @Override
                        public void onSeasonSaved(Season season) {
                            AppRes.getInstance().setSeason(season.getSeasonId(), season);
                            setSeasonSpinner();
                            loadGames(season.getSeasonId());
                            FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(new GameSettingsFragmentData());
                        }
                    });
                } else {
                    FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(new GameSettingsFragmentData());
                }


            }
        });
        return v;
    }

    public void setSeasonSpinner() {
        Map<String, Season> seasons = AppRes.getInstance().getSeasons();

        if(seasons.isEmpty()) {
            noSeasonsText.setVisibility(View.VISIBLE);
            seasonSpinner.setVisibility(View.GONE);
        } else {
            noSeasonsText.setVisibility(View.GONE);
            seasonSpinner.setVisibility(View.VISIBLE);
        }
        seasonIds = new ArrayList<>();
        ArrayList<String> seasonTitles = new ArrayList<>();
        for(Season season : seasons.values()) {
            seasonTitles.add(season.getName());
            seasonIds.add(season.getSeasonId());
        }
        Helper.setSpinnerAdapter(seasonSpinner, seasonTitles);

        seasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seasonId = seasonIds.get(position);
                loadGames(seasonId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void loadGames(final String seasonId) {
        if(seasonId == null) {
            AppRes.getInstance().setGames(new HashMap<String, Game>());
            refreshGames();
        } else {
            Map<String, Season> seasons = AppRes.getInstance().getSeasons();
            Season selectedSeason = seasons.get(seasonId);
            PrefRes.setSelectedSeasonId(AppRes.getInstance().getSelectedTeam().getTeamId(), seasonId);
            AppRes.getInstance().setSelectedSeason(selectedSeason);
            GamesResource.getInstance().getGames(seasonId, new GetGamesHandler() {
                @Override
                public void onGamesLoaded(Map<String, Game> games) {
                    AppRes.getInstance().setGames(games);
                    refreshGames();
                }
            });
        }
    }

    public void refreshGames() {
        ArrayList<Game> games = new ArrayList<>(AppRes.getInstance().getGames().values());
        adapter.setGames(games);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onGameSelected(Game game) {
        FragmentListeners.getInstance().getFragmentChangeListener().goToGameFragment(game);
    }
}
