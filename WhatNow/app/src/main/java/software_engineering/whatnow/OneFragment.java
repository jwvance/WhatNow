package software_engineering.whatnow;

/**
 * Created by Steve on 4/20/16.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OneFragment extends Fragment{
	private View rootView;
	private RecyclerView recyclerView;
	private Context context;
	String[] dataArray = new String[]{"Event 1","Event 2","Event 3","Event 4","Event 5","Event 6","Event 7","Event 8","Event 9"};

	public OneFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		rootView = inflater.inflate(R.layout.fragment_one, container, false);
		recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(context));
		recyclerView.setAdapter(new RecyclerAdapter(dataArray));

		return rootView;
	}

	public void setRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}