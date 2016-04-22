package software_engineering.whatnow;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
	private ArrayList<String/*Event*/> eventDataSource;

	public RecyclerAdapter(ArrayList<String/*Event*/> dataArgs){
		eventDataSource = dataArgs;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.card_layout, parent, false);

		ViewHolder viewHolder = new ViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// EDIT HERE CARD FIELDS
		holder.cardEventName.setText(eventDataSource.get(position));
	/*	holder.cardDescription.setText("");
		holder.cardDate.setText("");
		holder.cardTimes.setText("");
		holder.cardParticipants.setText("");
		holder.cardDistance.setText("");*/
	}

	@Override
	public int getItemCount() {
		return eventDataSource.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{
		protected TextView cardEventName;
		protected TextView cardDescription;
		protected TextView cardDate;
		protected TextView cardTimes;
		protected TextView cardParticipants;
		protected TextView cardDistance;

		public ViewHolder(View cardLayuot) {
			super(cardLayuot);
			cardEventName = (TextView) cardLayuot.findViewById(R.id.card_event_name);
			cardDescription = (TextView) cardLayuot.findViewById(R.id.card_description);
			cardDate = (TextView) cardLayuot.findViewById(R.id.card_date);
			cardTimes = (TextView) cardLayuot.findViewById(R.id.card_time);
			cardParticipants = (TextView) cardLayuot.findViewById(R.id.card_participants);
			cardDistance = (TextView) cardLayuot.findViewById(R.id.card_distance);

			cardLayuot.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), ListedEventActivity.class);
					intent.putExtra("Name", ((TextView) v.findViewById(R.id.card_event_name)).getText());
					v.getContext().startActivity(intent);
				}
			});
		}


	}
}