/* 	CLASS DESCRIPTION:
	-	This is similar to an Activity, but it's a Fragment
	-	The related layout file is the fragment_one
	-	here is everything related to splitting in categories
	-	onCreateView contains the category split part
	-	setSortingCriteria loops on the various events downloaded and sets the
		sorting criteria to the one that TabActivity tells it (got from the Dialog)
*/

package software_engineering.whatnow;

/**
 * Created by Steve on 4/20/16.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;

import software_engineering.whatnow.firebase_stuff.Constants;


public class TabFragment extends Fragment{
	private View rootView;
	private RecyclerView recyclerView;
	private String category;
	//private String[] cardsEventData = new String[]{"Event 1","Event 2","Event 3","Event 4",
	// "Event 5","Event 6","Event 7","Event 8","Event 9"};
	private ArrayList<Event> cardsEventData = new ArrayList<Event>();
	//private ArrayList<Event> events;
	private RecyclerAdapter recyclerAdapter;
	private Context context;
	private int sortingCriteria = 1; //default is incoming
	//private ProgressBar progressBar;

	public TabFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recyclerAdapter = new RecyclerAdapter(cardsEventData);

		context = getContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_one, container, false);
		recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		//	cardsEventData = AddEventActivity.loadEvents(rootView.getContext());

	//	progressBar = (ProgressBar) rootView.findViewById(R.id.fragmentProgressBar);

		// PLACE HERE CALL TO THE SERVER TO GET EVENTS FROM THE SPECIFIC CATEGORY
		/*cardsEventData.add("Event 1");
		cardsEventData.add("Event 2");
		cardsEventData.add("Event 3");
		cardsEventData.add("Event 4");
		cardsEventData.add("Event 5");
		cardsEventData.add("Event 6");
		cardsEventData.add("Event 7");
		cardsEventData.add("Event 8");
		cardsEventData.add("Event 9");*/

		//	recyclerAdapter = new RecyclerAdapter(cardsEventData);

		recyclerView.setAdapter(recyclerAdapter);	// GIVES TO THE ADAPTER ONLY THE EVENTS RELEVANT TO THIS FRAGMENT

		return rootView;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setEvents(ArrayList<Event> events) {
		this.cardsEventData = events;
	}

	public void setSortingCriteria(int sortingCriteria, Context tabActivity){
		this.sortingCriteria = sortingCriteria;
		//	cardsEventData = AddEventActivity.loadEvents(tabActivity);
		for (int i=0; i<cardsEventData.size(); i++){
			cardsEventData.get(i).setSortingCriteria(sortingCriteria);
		}
		Collections.sort(cardsEventData);
		//AddEventActivity.saveEvents(tabActivity, cardsEventData, cardsEventData.size());
		try{
			recyclerAdapter.notifyDataSetChanged();
		}catch(NullPointerException e){
			//e.printStackTrace();
		}
	}

	public void setCategory(String category){
		this.category = category;
	}
}