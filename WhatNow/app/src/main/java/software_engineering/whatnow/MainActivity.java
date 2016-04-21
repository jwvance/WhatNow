package software_engineering.whatnow;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupToolbar();
		setupRecyclerView();
	}
	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}
	private void setupRecyclerView() {
		RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
		populateRecyclerView(recyclerView);
	}
	private void populateRecyclerView(RecyclerView recyclerView) {
		recyclerView.setAdapter(new RecyclerView.Adapter() {
			private final static int DUMMY_ITEM_COUNT = 10;
			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
				View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
				return new CardHolder(itemView);
			}
			@Override
			public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
				// We are too lazy for this by now ;-)
			}
			@Override
			public int getItemCount() {
				return DUMMY_ITEM_COUNT;
			}
			class CardHolder extends RecyclerView.ViewHolder {
				public CardHolder(View itemView) {
					super(itemView);
				}
			}
		});


		final View tabBar = findViewById(R.id.fake_tab);
		final View coloredBackgroundView =        findViewById(R.id.colored_background_view);
		final View toolbarContainer = findViewById(R.id.toolbar_container);
		final View toolbar = findViewById(R.id.toolbar);
		recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
					if (Math.abs(toolbarContainer.getTranslationY()) >      toolbar.getHeight()) {
						hideToolbar();
					} else {
						showToolbar();
					}
				}
			}
			private void showToolbar() {
				toolbarContainer
						.animate()
						.translationY(0)
						.start();
			}
			private void hideToolbar() {
				toolbarContainer
						.animate()
						.translationY(-tabBar.getBottom())
						.start();
			}
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				scrollColoredViewParallax(dy);
				if (dy > 0) {
					hideToolbarBy(dy);
				} else {
					showToolbarBy(dy);
				}
			}
			private void scrollColoredViewParallax(int dy) {
				coloredBackgroundView.setTranslationY(coloredBackgroundView.getTranslationY() - dy / 3);
			}
			private void hideToolbarBy(int dy) {
				if (cannotHideMore(dy)) {
					toolbarContainer.setTranslationY(-tabBar.getBottom());
				} else {
					toolbarContainer.setTranslationY(toolbarContainer.getTranslationY() - dy);
				}
			}
			private boolean cannotHideMore(int dy) {
				return Math.abs(toolbarContainer.getTranslationY() - dy) > tabBar.getBottom();
			}
			private void showToolbarBy(int dy) {
				if (cannotShowMore(dy)) {
					toolbarContainer.setTranslationY(0);
				} else {
					toolbarContainer.setTranslationY(toolbarContainer.getTranslationY() - dy);
				}
			}
			private boolean cannotShowMore(int dy) {
				return toolbarContainer.getTranslationY() - dy > 0;
			}
		});
	}
	
	
}