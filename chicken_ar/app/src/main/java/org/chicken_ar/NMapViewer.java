/*
 * Copyright 2016 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.chicken_ar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapCompassManager;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

/**
 * Sample class for map viewer library.
 * 
 * @author kyjkim
 */
public class NMapViewer extends NMapActivity {
	private static final String LOG_TAG = "NMapViewer";
	private static final boolean DEBUG = false;

	// set your Client ID which is registered for NMapViewer library.
	private static final String CLIENT_ID = "5QlbWErrgs5Cl4XOuQyG";

	private MapContainerView mMapContainerView;

	private NMapView mMapView;
	private NMapController mMapController;


	private NMapOverlayManager mOverlayManager;
	private NMapMyLocationOverlay mMyLocationOverlay;
	private NMapLocationManager mMapLocationManager;
	private NMapCompassManager mMapCompassManager;

	private NMapViewerResourceProvider mMapViewerResourceProvider;

    public NMapPOIdata poiData;

    private boolean flag = false;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// create map view
		mMapView = new NMapView(this);

		// create parent view to rotate map view
		mMapContainerView = new MapContainerView(this);

		ImageView img = new ImageView(this);
		img.setImageResource(R.drawable.nextbtn);

		img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
				startActivity(intent);
			}
		});


		FrameLayout preview = new FrameLayout(this);


		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.RIGHT|Gravity.BOTTOM;
		params.bottomMargin = 100;
		params.rightMargin = 100;

		FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params1.leftMargin =0;
		params1.topMargin = 0;

		preview.addView(mMapView,params1);
		preview.addView(img,params);
		mMapContainerView.addView(preview);

		// set the activity content to the parent view
		setContentView(mMapContainerView);


		// set a registered Client Id for Open MapViewer Library
		mMapView.setClientId(CLIENT_ID);

		// initialize map view
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		mMapView.setFocusable(true);
		mMapView.setFocusableInTouchMode(true);
		mMapView.requestFocus();

		// register listener for map state changes
		mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);
		//mMapView.setOnMapViewTouchEventListener(onMapViewTouchEventListener);
		//mMapView.setOnMapViewDelegate(onMapViewTouchDelegate);

		// use map controller to zoom in/out, pan and set map center, zoom level etc.
		mMapController = mMapView.getMapController();

		// use built in zoom controls
		/*NMapView.LayoutParams lp = new NMapView.LayoutParams(NMapView.LayoutParams.WRAP_CONTENT,
			NMapView.LayoutParams.WRAP_CONTENT, NMapView.LayoutParams.BOTTOM_RIGHT);
		mMapView.setBuiltInZoomControls(true, lp);*/

		// create resource provider
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

		// set data provider listener
		//super.setMapDataProviderListener(onDataProviderListener);

		// create overlay manager
		mOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);
		// register callout overlay listener to customize it.
		//mOverlayManager.setOnCalloutOverlayListener(onCalloutOverlayListener);
		// register callout overlay view listener to customize it.
		//mOverlayManager.setOnCalloutOverlayViewListener(onCalloutOverlayViewListener);

		// location manager
		mMapLocationManager = new NMapLocationManager(this);
		mMapLocationManager.setOnLocationChangeListener(onMyLocationChangeListener);

		// compass manager
		mMapCompassManager = new NMapCompassManager(this);

		// create my location overlay
		mMyLocationOverlay = mOverlayManager.createMyLocationOverlay(mMapLocationManager, mMapCompassManager);




	}

		/* Test Functions */
		private void startMyLocation() {

            if (mMyLocationOverlay != null) {
                if (!mOverlayManager.hasOverlay(mMyLocationOverlay)) {
                    mOverlayManager.addOverlay(mMyLocationOverlay);
                }

                if (mMapLocationManager.isMyLocationEnabled()) {

                    if (!mMapView.isAutoRotateEnabled()) {
                        mMyLocationOverlay.setCompassHeadingVisible(true);

                        mMapCompassManager.enableCompass();


                        mMapView.setAutoRotateEnabled(true, false);

                        mMapContainerView.requestLayout();
                    }

                    mMapView.postInvalidate();



                } else {
                    boolean isMyLocationEnabled = mMapLocationManager.enableMyLocation(true);
                    if (!isMyLocationEnabled) {
                        Toast.makeText(NMapViewer.this, "Please enable a My Location source in system settings",
                                Toast.LENGTH_LONG).show();

                        Intent goToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(goToSettings);

                        return;
                    }
                }
            }

		}



	/* MyLocation Listener */
	private final NMapLocationManager.OnLocationChangeListener onMyLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {

		@Override
		public boolean onLocationChanged(NMapLocationManager locationManager, NGeoPoint myLocation) {
			if (mMapController != null) {
				// mMapController.animateTo(myLocation);
			}

			return true;
		}

		@Override
		public void onLocationUpdateTimeout(NMapLocationManager locationManager) {

			Toast.makeText(NMapViewer.this, "Your current location is temporarily unavailable.", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLocationUnavailableArea(NMapLocationManager locationManager, NGeoPoint myLocation) {

			Toast.makeText(NMapViewer.this, "Your current location is unavailable area.", Toast.LENGTH_LONG).show();

		}

	};

	/* MapView State Change Listener*/
	private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {

		@Override
		public void onMapInitHandler(NMapView mapView, NMapError errorInfo) {

			if (errorInfo == null) { // success
				// restore map view state such as map center position and zoom level.
//				restoreInstanceState();
				startMyLocation();

				mMapController.setMapCenter(new NGeoPoint(126.9647, 37.545), 19);

                new MyAsyncTask(poiData).execute(mMapLocationManager);


            } else { // fail
				Log.e(LOG_TAG, "onFailedToInitializeWithError: " + errorInfo.toString());

				Toast.makeText(NMapViewer.this, errorInfo.toString(), Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onAnimationStateChange(NMapView mapView, int animType, int animState) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onAnimationStateChange: animType=" + animType + ", animState=" + animState);
			}
		}

		@Override
		public void onMapCenterChange(NMapView mapView, NGeoPoint center) {
            if (DEBUG) {
				Log.i(LOG_TAG, "onMapCenterChange: center=" + center.toString());
			}
		}

		@Override
		public void onZoomLevelChange(NMapView mapView, int level) {
			if (DEBUG) {
				Log.i(LOG_TAG, "onZoomLevelChange: level=" + level);
			}
		}

		@Override
		public void onMapCenterChangeFine(NMapView mapView) {

		}
	};



	/** 
	 * Container view class to rotate map view.
	 */
	private class MapContainerView extends ViewGroup {

		public MapContainerView(Context context) {
			super(context);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
			final int width = getWidth();
			final int height = getHeight();
			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);
				final int childWidth = view.getMeasuredWidth();
				final int childHeight = view.getMeasuredHeight();
				final int childLeft = (width - childWidth) / 2;
				final int childTop = (height - childHeight) / 2;
				view.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
			}

			if (changed) {
				mOverlayManager.onSizeChanged(width, height);
			}
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int w = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
			int h = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
			int sizeSpecWidth = widthMeasureSpec;
			int sizeSpecHeight = heightMeasureSpec;

			final int count = getChildCount();
			for (int i = 0; i < count; i++) {
				final View view = getChildAt(i);

				if (view instanceof NMapView) {
					if (mMapView.isAutoRotateEnabled()) {
						int diag = (((int)(Math.sqrt(w * w + h * h)) + 1) / 2 * 2);
						sizeSpecWidth = MeasureSpec.makeMeasureSpec(diag, MeasureSpec.EXACTLY);
						sizeSpecHeight = sizeSpecWidth;
					}
				}

				view.measure(sizeSpecWidth, sizeSpecHeight);
			}
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}




    public class MyAsyncTask extends AsyncTask<NMapLocationManager, Void, NGeoPoint> {
        NMapPOIdata poiData;

        public MyAsyncTask(NMapPOIdata poiData){
            this.poiData = poiData;
        }

        @Override
        protected NGeoPoint doInBackground(NMapLocationManager... params) {
            NGeoPoint cur;

            while((cur = params[0].getMyLocation()) == null);

            return cur;
        }

        @Override
        protected void onPostExecute(NGeoPoint result) {
            super.onPostExecute(result);

            Log.i("ttttttttttt",NGeoPoint.getDistance(result, new NGeoPoint(127.073673, 37.549022))+"");


            poiData = new NMapPOIdata(3, mMapViewerResourceProvider);

            poiData.beginPOIdata(3);

            if(NGeoPoint.getDistance(result, new NGeoPoint(127.073673, 37.549022)) < 200.0) {
                //poiData.addPOIitem(BuildingInfo.LON0, BuildingInfo.LAT0, null, NMapPOIflagType.PIN, 0); //학생회관
				poiData.addPOIitem(126.9647, 37.545, null, NMapPOIflagType.PIN, 0); //학생회관
            }else{
                poiData.addPOIitem(126.9647, 37.545, null, NMapPOIflagType.SPOT, 0); //학생회관
            }
            if(NGeoPoint.getDistance(result, new NGeoPoint(127.073521, 37.549161)) < 200.0) {
                poiData.addPOIitem(BuildingInfo.LON1, BuildingInfo.LAT1, null, NMapPOIflagType.PIN, 1); //광개토
            }else{
                poiData.addPOIitem(126.966193, 37.545056,  null, NMapPOIflagType.SPOT, 1); //광개토
            }
            if(NGeoPoint.getDistance(result, new NGeoPoint(127.073952, 37.552261)) < 10.0){
                poiData.addPOIitem(BuildingInfo.LON2, BuildingInfo.LAT2, null, NMapPOIflagType.PIN, 2); //충무관
            }else{
                poiData.addPOIitem( 126.966109, 37.545125, null, NMapPOIflagType.SPOT, 2); //충무관
            }
            poiData.endPOIdata();
            NMapPOIdataOverlay poiDataOverlay = mOverlayManager.createPOIdataOverlay(poiData, null);
        }


    }

}



