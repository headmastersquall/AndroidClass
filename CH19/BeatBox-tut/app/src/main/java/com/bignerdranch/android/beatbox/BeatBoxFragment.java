package com.bignerdranch.android.beatbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class BeatBoxFragment extends Fragment {

	private BeatBox beatBox;

	public static BeatBoxFragment newInstance() {
		return new BeatBoxFragment();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		beatBox = new BeatBox(getActivity());
	}

	@Override
	public View onCreateView(
		final LayoutInflater inflater,
		final ViewGroup container,
		final Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		final View view = inflater.inflate(R.layout.fragment_beat_box, container, false);
		final RecyclerView recyclerView =
			(RecyclerView)view.findViewById(R.id.fragment_beat_box_recycler_view);
		recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
		recyclerView.setAdapter(new SoundAdapter(beatBox.getSounds()));
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		beatBox.release();
	}

	private class SoundHolder extends RecyclerView.ViewHolder
		implements View.OnClickListener {
		private Sound sound;
		private Button button;

		public SoundHolder(final LayoutInflater inflater, final ViewGroup container) {
			super(inflater.inflate(R.layout.list_item_sound, container, false));
			button = (Button)itemView.findViewById(R.id.list_item_sound_button);
			button.setOnClickListener(this);
		}

		public void bindSound(final Sound sound) {
			this.sound = sound;
			button.setText(sound.getName());
		}

		@Override
		public void onClick(final View v) {
			beatBox.play(sound);
		}
	}

	private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

		private List<Sound> sounds;

		public SoundAdapter(final List<Sound> sounds) {
			this.sounds = sounds;
		}

		@Override
		public SoundHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
			final LayoutInflater inflater = LayoutInflater.from(getActivity());
			return new SoundHolder(inflater, parent);
		}

		@Override
		public void onBindViewHolder(final SoundHolder holder, final int position) {
			final Sound sound = sounds.get(position);
			holder.bindSound(sound);
		}

		@Override
		public int getItemCount() {
			return sounds.size();
		}
	}
}
