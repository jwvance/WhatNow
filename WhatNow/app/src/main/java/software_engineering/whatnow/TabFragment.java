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

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Collections;
=======
import java.util.Collection;
import java.util.HashMap;

import software_engineering.whatnow.firebase_stuff.Constants;
>>>>>>> testingFirebaseEvents


public class TabFragment extends Fragment{
	private View rootView;
	private RecyclerView recyclerView;
	private String category;
<<<<<<< HEAD
	private ArrayList<Event> cardsEventData;
	//private String[] cardsEventData = new String[]{"Event 1","Event 2","Event 3","Event 4",
	// "Event 5","Event 6","Event 7","Event 8","Event 9"};
=======
	private ArrayList<Event> cardsEventData = new ArrayList<Event>();;
	//private String[] cardsEventData = new String[]{"Event 1","Event 2","Event 3","Event 4","Event 5","Event 6","Event 7","Event 8","Event 9"};
>>>>>>> testingFirebaseEvents
	//private ArrayList<Event> events;
	private RecyclerAdapter recyclerAdapter;
	private Context context;

	public TabFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = getContext();
		//cardsEventData = new ArrayList<Event>();	//AddEventActivity.loadEvents(getContext());

		// Use Firebase to populate the list.
		Firebase firebase = new Firebase(Constants.DATABASE_URL/* + "/events/events_list"*/);
		/*firebase.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {

			}
		});*/

		firebase.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				//ArrayList<Event> events;
				try{
					//for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
					//ArrayList<Event> events = (ArrayList<Event>) dataSnapshot.getValue();
					HashMap<String, Event> eventHashMap = (HashMap<String, Event>) dataSnapshot.getValue();
					ArrayList<HashMap> weirdEvents = new ArrayList(eventHashMap.values());
					HashMap e;
					for (int i = 0; i < weirdEvents.size(); i++) {
						e = weirdEvents.get(i);
					//	int id = (int) ((long) e.get("id"));
					//	int hourStart = (int) ((long) e.get("hourStart"));
					//	int minuteStart = (int) ((long) e.get("minuteStart"));
					//	int hourEnd = (int) ((long) e.get("hourEnd"));
					//	int minuteEnd = (int) ((long) e.get("minuteEnd"));
					//	String location = (String) e.get("location");
						Host host = new Host((String) ((HashMap) e.get("host")).get("name"));
					//	String name = (String) e.get("name");
					//	String description = (String) e.get("description");
						Category category = new Category((String) ((HashMap) e.get("category")).get("name"));
					//	long dateStart = (long) e.get("dateStart");
					//	String imagePath = (String) e.get("imagePath");
						Event event = new Event((int) ((long) e.get("id")), (int) ((long) e.get("hourStart")),
								(int) ((long) e.get("minuteStart")), (int) ((long) e.get("hourEnd")),
								(int) ((long) e.get("minuteEnd")), (String) e.get("location"), host,
								(String) e.get("name"), (String) e.get("description"), category,
								(long) e.get("dateStart"), (String) e.get("imagePath"));
						event.setMyLoc(context);
						cardsEventData.add(event);
					//	Log.wtf("FIREBASE event name CEL", i + ": " + weirdEvents.get(i).get("name").toString());
					}
				}catch(Exception e){
					Log.wtf("FIREBASE event name CEL", e.getMessage());
				}
			/*	cardsEventData.add((Event) dataSnapshot.child("title").getValue());*/
				if(cardsEventData.size() > 0 && cardsEventData.get(0) == null)
					cardsEventData.clear();
				try{
					recyclerAdapter.notifyDataSetChanged();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				cardsEventData.remove((Event) dataSnapshot.child("title").getValue());
				try{
					recyclerAdapter.notifyDataSetChanged();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {

			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_one, container, false);
		recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		//	cardsEventData = AddEventActivity.loadEvents(rootView.getContext());


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

		recyclerAdapter = new RecyclerAdapter(cardsEventData);

		recyclerView.setAdapter(recyclerAdapter);	// GIVES TO THE ADAPTER ONLY THE EVENTS RELEVANT TO THIS FRAGMENT

		return rootView;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/*public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}*/

	public void setSortingCriteria(int sortingCriteria, Context tabActivity){
		cardsEventData = AddEventActivity.loadEvents(tabActivity);
		for (int i=0; i<cardsEventData.size(); i++){
			cardsEventData.get(i).setSortingCriteria(sortingCriteria);
		}
		Collections.sort(cardsEventData);
		AddEventActivity.saveEvents(tabActivity, cardsEventData, cardsEventData.size());
	}

	public void setCategory(String category){
		this.category = category;
	}
}