package com.example.mobileappwithlogin.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mobileappwithlogin.R;
import com.pushpushgo.sdk.PushPushGo;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class ChoosePokemon extends AppCompatActivity implements View.OnClickListener{

    // Declare layout variables
    ImageView pokemonImageContainer;
    TextView pokemonName, pokemonType, pokemonMainAbility;
    ProgressBar progressBar;

    // Instantiate PPG
    private final PushPushGo ppg = PushPushGo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_pokemon);

        // Selectors
        pokemonImageContainer = findViewById(R.id.pokemonImage);
        pokemonName = findViewById(R.id.pokemonNameDisplay);
        pokemonType = findViewById(R.id.pokemonTypeDisplay);
        pokemonMainAbility = findViewById(R.id.pokemonAbilityDisplay);
        progressBar = findViewById(R.id.progressBar);

        //On click listeners
        Button backToProfile = findViewById(R.id.backToProfileButton);
        backToProfile.setOnClickListener(this);

        Button pickYourPokemon = findViewById(R.id.pickPokemonButton);
        pickYourPokemon.setOnClickListener(this);
    }

    // On click event logic
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backToProfileButton:
                startActivity(new Intent(ChoosePokemon.this, ProfileActivity.class));
                break;

            case R.id.pickPokemonButton:
                fetchPokemonData();
                // Send PPG beacon with selector data after click on 'Choose My Pokemon' button
                ppg.createBeacon()
                        .set("clickedpokemon", true)
                        .send();
                break;
        }
    }

    // Fetching Pokemon data from PokeAPI
    private void fetchPokemonData() {

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue volleyQueue = Volley.newRequestQueue(ChoosePokemon.this);

        // Randomize pick of Pokemon ID
        Random random = new Random();
        int pokemonIndex = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            pokemonIndex = random.ints(1, 906).findFirst().orElseThrow(null);
        }

        String url = String.format("https://pokeapi.co/api/v2/pokemon/%s/", pokemonIndex);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    String pokemonImageUrl;
                    String pokemonNameAPI;
                    String pokemonTypeAPI;
                    String pokemonAbilityAPI;
                    try {
                        // Display Pokemon data in App
                        pokemonImageUrl = response.getJSONObject("sprites").getString("front_shiny");
                        Glide.with(ChoosePokemon.this).load(pokemonImageUrl).into(pokemonImageContainer);

                        pokemonNameAPI = response.getString("name");
                        pokemonName.setText(pokemonNameAPI);

                        pokemonTypeAPI = response.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
                        pokemonType.setText(pokemonTypeAPI);

                        pokemonAbilityAPI = response.getJSONArray("moves").getJSONObject(0).getJSONObject("move").getString("name");
                        pokemonMainAbility.setText(pokemonAbilityAPI);
                        progressBar.setVisibility(View.GONE);

                        // Send PPG beacon with picked Pokemon name
                        ppg.createBeacon().set("pokemonname", pokemonNameAPI).send();
                        // Trigger custom automation scenario in PPG app
                        automationScenarioPPGTrigger(pokemonTypeAPI);

                    }catch(JSONException e){
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(ChoosePokemon.this, "Cannot fetch pokemon data :(", Toast.LENGTH_LONG).show();
                    Log.e("Choose Pokemon", "loadPokemonData error: ${error.localizedMessage}");
                    progressBar.setVisibility(View.GONE);
                }
        );
        volleyQueue.add(jsonObjectRequest);
    }

    private void automationScenarioPPGTrigger(String type) {
        RequestQueue volleyQueue = Volley.newRequestQueue(ChoosePokemon.this);

        String subscriberId = ppg.getSubscriberId();
        String url = String.format("https://api.pushpushgo.com/project/639c65b4f20b284ddb715b1d/automation/63a18c36db9efc70c2d93cb2/trigger/%s", subscriberId);

        if (Objects.equals(type, "bug")) {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    null,
                    response -> {},
                    error -> Toast.makeText(ChoosePokemon.this, "Cannot send request :(", Toast.LENGTH_LONG).show()
            )
            {
                @Override
                public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("X-Token", "302b6bda-76e7-48fa-b0cd-78eaa6f10b95");
                return params;
            }
            };
            volleyQueue.add(jsonObjectRequest);
        }
    }

}