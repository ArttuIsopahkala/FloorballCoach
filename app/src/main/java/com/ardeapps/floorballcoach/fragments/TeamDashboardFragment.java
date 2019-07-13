package com.ardeapps.floorballcoach.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ardeapps.floorballcoach.AppRes;
import com.ardeapps.floorballcoach.R;
import com.ardeapps.floorballcoach.services.FragmentListeners;
import com.ardeapps.floorballcoach.utils.ImageUtil;
import com.ardeapps.floorballcoach.viewObjects.GameSettingsFragmentData;


public class TeamDashboardFragment extends Fragment {

    Button newGameButton;
    Button linesButton;
    Button playersButton;
    Button gamesButton;
    TextView teamNameText;
    ImageView logoImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_team_dashboard, container, false);

        newGameButton = v.findViewById(R.id.newGameButton);
        gamesButton = v.findViewById(R.id.gamesButton);
        linesButton = v.findViewById(R.id.linesButton);
        playersButton = v.findViewById(R.id.playersButton);
        teamNameText = v.findViewById(R.id.teamNameText);
        logoImage = v.findViewById(R.id.logoImage);

        Bitmap logo = AppRes.getInstance().getSelectedTeam().getLogo();
        if(logo != null) {
            logoImage.setImageBitmap(ImageUtil.getSquarePicture(logo));
        } else {
            logoImage.setImageResource(R.drawable.default_logo);
        }

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToGameSettingsFragment(new GameSettingsFragmentData());
            }
        });
        gamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToGamesFragment();
            }
        });
        linesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToLinesFragment();
            }
        });
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentListeners.getInstance().getFragmentChangeListener().goToPlayersFragment();
            }
        });
        return v;
    }

}
