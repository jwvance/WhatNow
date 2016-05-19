/* 	CLASS DESCRIPTION:
	-	This class is responsible of showing the cards properly
	-	there are 2 classes in here
	-	ViewHolder let's say contains the card, it does generic stuff
		to make the life easier to the RecyclerAdapter:
	-	RecyclerAdapter is the one that shows the card, it has access
		to all of its fields and stuff thanks to the ViewHolder.
		it's the place where you have to put things in order to edit
		whatever is related to the card.
		it is related to an ArrayList of Event objs.
		the onBindViewHolder method sets the various fields for the,
		card: it's specifically here that you edit to have the card
		showing specific stuff or responding to specific clicks.
*/

package software_engineering.whatnow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
	private ArrayList<Event> eventDataSource;

	public RecyclerAdapter(ArrayList<Event> dataArgs){
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
		Event event = eventDataSource.get(position);
		holder.setID(event.getId());
		holder.cardEventName.setText(event.getName());
		holder.cardDescription.setText(event.getDescription());
		holder.cardDate.setText(event.getFriendlyDate());
		//holder.cardTimes.setText(event.getStartTime());



		byte[] imageAsBytes = Base64.decode(event.getImageAsString(), Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
		holder.cardImage.setImageBitmap(bitmap);


	/*	File sd = Environment.getExternalStorageDirectory();
		File image = new File(sd+event.getImageAsString(), imageName);
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
		bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
		imageView.setImageBitmap(bitmap);*/

		/*File imgFile = new File(event.getImageAsString());

		if(imgFile.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			holder.cardImage.setImageBitmap(bitmap);
		}*/

		/*Bitmap bitmap = BitmapFactory.decodeFile(event.getImageAsString());
		holder.cardImage.setImageBitmap(bitmap);*/
		//	holder.cardImage.setImageURI(Uri.fromFile(new File(event.getImageAsString())));
		//	holder.cardParticipants.setText("");
		holder.cardDistance.setText(event.getDistance()); //test
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
		protected ImageView cardImage;
		protected int id;
		protected boolean isBookmarked = false;

		public ViewHolder(final View cardLayuot) {
			super(cardLayuot);
			cardEventName = (TextView) cardLayuot.findViewById(R.id.card_event_name);
			cardDescription = (TextView) cardLayuot.findViewById(R.id.card_description);
			cardDate = (TextView) cardLayuot.findViewById(R.id.card_date);
			//cardTimes = (TextView) cardLayuot.findViewById(R.id.card_time);
			cardParticipants = (TextView) cardLayuot.findViewById(R.id.card_participants);
			cardDistance = (TextView) cardLayuot.findViewById(R.id.card_distance);
			cardImage = (ImageView) cardLayuot.findViewById(R.id.card_image);

			cardLayuot.findViewById(R.id.card_view).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					Intent intent = new Intent(v.getContext(), ListedEventActivity.class);
					//	intent.putExtra("Name", ((TextView) v.findViewById(R.id.card_event_name)).getText());
					intent.putExtra("Event_ID", id);
					v.getContext().startActivity(intent);
				}
			});

			cardLayuot.findViewById(R.id.card_share_button).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					String message;
					Intent sendIntent = new Intent();
					sendIntent.setAction(Intent.ACTION_SEND);
					message = "Hey! " + cardEventName.getText() + " is happening on " + cardDate.getText() + ". Find more info by downloading WhatNow from the Play Store!";
					sendIntent.putExtra(Intent.EXTRA_TEXT, message);
					sendIntent.setType("text/plain");
					v.getContext().startActivity(sendIntent);
				}
			});

			cardLayuot.findViewById(R.id.card_bookmark_button).setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					Snackbar snackbar;

					//change bookmark icon and display snackbar
					ImageView img = (ImageView)cardLayuot.findViewById(R.id.card_bookmark_button);
					if(isBookmarked){
						img.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
						isBookmarked = false;
						snackbar = Snackbar.make(v, "Event removed from your profile", Snackbar.LENGTH_SHORT);
					}
					else{
						img.setImageResource(R.drawable.ic_bookmark_black_24dp);
						isBookmarked = true;
						snackbar = Snackbar.make(v, "Event saved for later, find it in your profile", Snackbar.LENGTH_LONG);
					}
					snackbar.show();
				}
			});




		}



		public void setID(int id){
			this.id = id;
		}
	}
}