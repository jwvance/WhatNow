package software_engineering.whatnow;

/**
 * Created by Steve on 4/20/16.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class TabFragment extends Fragment{
	private View rootView;
	private RecyclerView recyclerView;
	private Context context;
	private String category;
	private ArrayList<Event> cardsEventData;
	//private String[] cardsEventData = new String[]{"Event 1","Event 2","Event 3","Event 4",
	// "Event 5","Event 6","Event 7","Event 8","Event 9"};
	//private ArrayList<Event> events;

	public TabFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_one, container, false);
		recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));

		cardsEventData = AddEventActivity.loadEvents(rootView.getContext());
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

		recyclerView.setAdapter(new RecyclerAdapter(cardsEventData));	// GIVES TO THE ADAPTER ONLY THE EVENTS RELEVANT TO THIS FRAGMENT

		return rootView;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/*public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}*/

	public void SetSortingCriteria(int sortingCriteria){
		for (int i=0; i<cardsEventData.size(); i++){
			cardsEventData.get(i).setSortingCriteria(sortingCriteria);
		}
	}

	public void setCategory(String category){
		this.category = category;
	}
}