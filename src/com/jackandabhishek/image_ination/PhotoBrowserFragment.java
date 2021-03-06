package com.jackandabhishek.image_ination;

import java.io.FileOutputStream;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoBrowserFragment extends Fragment {
	
	public Bitmap CurrentImage;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static PhotoBrowserFragment newInstance() {
		PhotoBrowserFragment fragment = new PhotoBrowserFragment();
		return fragment;
	}
	
	public PhotoBrowserFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater infl, ViewGroup container, Bundle savedInstanceState) {
		View rootView = infl.inflate(R.layout.fragment_browsephotos, container, false);
		return rootView;
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(MainActivity.BROWSEPHOTOS_FRAGMENT_POSITION);
	//	LaunchGallery();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		Button button = (Button) getActivity().findViewById(R.id.browse_gallery_button);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LaunchGallery();
			}
		});
		
		((Button) getActivity().findViewById(R.id.edit_image_button))
				.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						LaunchEditImageActivity(v);
					}
				});
		
	}
	
	public void LaunchGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, 1);
	}
	
	public void LaunchEditImageActivity(View v) {
		ClearImageView();
		// Intent intent = new Intent(getActivity(), EditImageActivity.class);
		// intent.putExtra(EditImageActivity.IMAGE_KEY, CurrentImage.getPath());
		// // intent.putExtra(EditImageActivity.IMAGE_KEY, CurrentPhoto.toString());
		// startActivity(intent);
		
		try {
			// Write file
			
			String filename = "bitmap12345.png";
			FileOutputStream stream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
			CurrentImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
			
			// Cleanup
			stream.close();
			CurrentImage.recycle();
			
			// Pop intent
			Intent in1 = new Intent(getActivity(), EditImageActivity.class);
			in1.putExtra(EditImageActivity.IMAGE_KEY, filename);
			startActivity(in1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void ClearImageView() {
		ImageView imgview = (ImageView) getActivity().findViewById(R.id.browse_gallery_imageview);
		imgview.setImageBitmap(null);
	}
	
	private void SetImageView(Bitmap bitmap) {
		ImageView imgview = (ImageView) getActivity().findViewById(R.id.browse_gallery_imageview);
		imgview.setImageBitmap(bitmap);
	}
	
	/*
	 * private Point GetDimensions() { Display display = getActivity().getWindowManager().getDefaultDisplay(); Point
	 * size = new Point(); display.getSize(size); // width = size.x // height = size.y return size; }
	 */
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			// clear imageview to free memory
			ClearImageView();
			Uri chosenImageUri = data.getData();
			// Bitmap bitmap = decodeUri(chosenImageUri);
			Bitmap bitmap =
					ImageScaler.decodeSampledBitmapFromUri(getActivity().getContentResolver(),
							chosenImageUri,
							(ImageView) getActivity().findViewById(R.id.browse_gallery_imageview));
			if (bitmap != null) {
				SetImageView(bitmap);
				CurrentImage = bitmap;
			}
		}
	}
}
