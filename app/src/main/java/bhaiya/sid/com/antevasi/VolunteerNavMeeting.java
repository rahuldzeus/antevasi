package bhaiya.sid.com.antevasi;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.agrawalsuneet.loaderspack.loaders.MultipleRippleLoader;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.southernbox.parallaxrecyclerview.ParallaxRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VolunteerNavMeeting extends Fragment {
    View view;
    CoordinatorLayout coordinatorLayout;
    JsonArrayRequest jsonArrayRequest;
    ArrayList<VolunteerMeetingResponse> volunteerMeetingResponseArrayList = new ArrayList<VolunteerMeetingResponse>();
    ParallaxRecyclerView recyclerView;
    VolunteerMeetingAdapter adapter;
    MultipleRippleLoader multipleRippleLoader;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.volunteer_nav_meeting,container,false);
        coordinatorLayout=view.findViewById(R.id.coordinatorLayout);
        recyclerView = view.findViewById(R.id.mRecyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        if (new ConnectionDetector().isConnectingToInternet(getActivity().getApplicationContext())) {
            multipleRippleLoader=view.findViewById(R.id.progress_ripple);
            multipleRippleLoader.setVisibility(View.VISIBLE);
            jsonArrayRequest  = new JsonArrayRequest("https://storyclick.000webhostapp.com/biography.php?req=meeting",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                volunteerMeetingResponseArrayList.clear();
                                Gson gson = new Gson();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    VolunteerMeetingResponse volunteerMeetingResponse = gson.fromJson(String.valueOf(jsonObject), VolunteerMeetingResponse.class);
                                    volunteerMeetingResponseArrayList.add(volunteerMeetingResponse);
                                }
                                adapter = new VolunteerMeetingAdapter(getActivity().getApplicationContext(), volunteerMeetingResponseArrayList);
                                recyclerView.setAdapter(adapter);
                                multipleRippleLoader.setVisibility(View.GONE);

                            } catch (JSONException e) {
                                multipleRippleLoader.setVisibility(View.GONE);
                                Snackbar.make(coordinatorLayout,"Something went wrong", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            multipleRippleLoader.setVisibility(View.GONE);
                            Snackbar.make(coordinatorLayout,"Try again later", Snackbar.LENGTH_LONG).show();
                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.add(jsonArrayRequest);
        }
        else{
            Snackbar.make(coordinatorLayout,"No Internet Connection", Snackbar.LENGTH_LONG).show();
        }
        return view;
    }
    @Override
    public void onDestroy() {
        jsonArrayRequest.cancel();
        super.onDestroy();

    }
}
